package com.example.bodycomposition.fragment

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.icomon.icdevicemanager.ICDeviceManager
import cn.icomon.icdevicemanager.ICDeviceManagerDelegate
import cn.icomon.icdevicemanager.model.data.ICCoordData
import cn.icomon.icdevicemanager.model.data.ICKitchenScaleData
import cn.icomon.icdevicemanager.model.data.ICRulerData
import cn.icomon.icdevicemanager.model.data.ICSkipData
import cn.icomon.icdevicemanager.model.data.ICSkipFreqData
import cn.icomon.icdevicemanager.model.data.ICWeightCenterData
import cn.icomon.icdevicemanager.model.data.ICWeightData
import cn.icomon.icdevicemanager.model.data.ICWeightHistoryData
import cn.icomon.icdevicemanager.model.device.ICDevice
import cn.icomon.icdevicemanager.model.device.ICDeviceInfo
import cn.icomon.icdevicemanager.model.device.ICScanDeviceInfo
import cn.icomon.icdevicemanager.model.device.ICUserInfo
import cn.icomon.icdevicemanager.model.other.ICConstant.*
import cn.icomon.icdevicemanager.model.other.ICDeviceManagerConfig
import com.example.bodycomposition.R
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentVisualizeBinding
import com.example.bodycomposition.model.DataViewModel
import com.example.bodycomposition.utils.Constant.DATE_FORMAT
import com.example.bodycomposition.utils.EventMgr
import com.example.bodycomposition.utils.UserInfo
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class VisualizeFragment : BaseFragment<FragmentVisualizeBinding>(), ICDeviceManagerDelegate, EventMgr.Event {

    private val viewModel: DataViewModel by activityViewModels()

    private var device: ICDevice? = null
    private var deviceInfo: ICScanDeviceInfo? = null
    private var unitIndex: Int? = null

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVisualizeBinding {
        return FragmentVisualizeBinding.inflate(inflater, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData() {
        binding.apply {
            visualizeFragment = this@VisualizeFragment
            viewModel = this.viewModel
        }

        val currentUserInfo: UserInfo = viewModel.userInfo.value!!
        binding.name.text = "name: " + currentUserInfo.name
        binding.height.text = "height: " + currentUserInfo.height.toString() + " cm."
        binding.date.text = "date of birth: " + currentUserInfo.dateOfBirth.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
        when (currentUserInfo.sex) {
            ICSexType.ICSexTypeUnknown -> binding.sex.text = "sex: Unspecified"
            ICSexType.ICSexTypeMale -> binding.sex.text = "sex: Male"
            ICSexType.ICSexTypeFemal -> binding.sex.text = "sex: Female"
        }

        Log.d(TAG, "data in viewModel name: ${viewModel.userInfo.value?.name} height: ${viewModel.userInfo.value?.height} dob: ${viewModel.userInfo.value?.dateOfBirth}")

        EventMgr.addEvent("SCAN", this)

        if (viewModel.deviceMcAddress.value != null) {
            binding.deviceName.text = viewModel.deviceMcAddress.value
            binding.data.text = "Connected!"
        } else {
            binding.deviceName.text = "None"
            binding.data.text = "No device!"
        }

        initSDK()
    }

    fun addDevice() {
        findNavController().navigate(R.id.action_visualizeFragment_to_scanDeviceFragment)
    }

    fun deleteDevice() {
        Log.d(TAG, "deleteDevice Clicked! device is null?: ${device == null}")
        if (device == null) {
            device = ICDevice()
        }
        device!!.setMacAddr(viewModel.deviceMcAddress.value)
        ICDeviceManager.shared().removeDevice(device) {
            device, code -> addLog("delete device state: $code")
        }
        viewModel.setDeviceMcAddress(null)
        binding.deviceName.text = "None"
    }

    companion object {
        private const val TAG = "VisualizeFragment"
    }

    private fun addLog(string: String) {
//        binding.bleState.text = string
        Log.d(TAG, string)
    }

    /**
     * EventMgr.Event
     */
    override fun onCallBack(obj: Any?) {
        Log.d(TAG, "onCallBack called!")

        deviceInfo = obj as ICScanDeviceInfo
        Log.d(TAG, "Device type: ${deviceInfo!!.getType().name}")

        if (device == null) {
            device = ICDevice()
        }
        deviceInfo!!.getMacAddr().also {
            viewModel.setDeviceMcAddress(it)
            device!!.setMacAddr(it)
        }

        // Add device on call back event
        ICDeviceManager.shared().addDevice(device) { device, code ->
            addLog("add device state : $code")
        }

        // Set Device Name
        binding.deviceName.text = device!!.getMacAddr()
        Log.d(TAG, device!!.getMacAddr())
        Log.d(TAG, viewModel!!.deviceMcAddress.value.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initSDK() {
        val config = ICDeviceManagerConfig()
        config.context = requireContext()

        // Set user info
        val icUserInfo = ICUserInfo()

        icUserInfo.kitchenUnit = ICKitchenScaleUnit.ICKitchenScaleUnitG
        icUserInfo.rulerUnit = ICRulerUnit.ICRulerUnitInch
        icUserInfo.age = Period.between(
            viewModel.userInfo.value!!.dateOfBirth,
            LocalDate.now()
        ).years
        icUserInfo.weight = 70.0 // temp weight
        icUserInfo.height = viewModel.userInfo.value!!.height
        icUserInfo.sex = viewModel.userInfo.value!!.sex
        icUserInfo.userIndex = 1
        icUserInfo.peopleType = ICPeopleType.ICPeopleTypeNormal

        ICDeviceManager.shared().delegate = this
        ICDeviceManager.shared().updateUserInfo(icUserInfo)
        ICDeviceManager.shared().initMgrWithConfig(config)
    }

    override fun onInitFinish(bSuccess: Boolean) {
        addLog("SDK init result: $bSuccess")
    }

    override fun onBleState(state: ICBleState?) {
        addLog("ble state: $state")
    }

    override fun onDeviceConnectionChanged(device: ICDevice?, state: ICDeviceConnectState?) {
        addLog("${device!!.getMacAddr()}: connect state : $state")
    }

    override fun onNodeConnectionChanged(
        device: ICDevice?,
        nodeId: Int,
        state: ICDeviceConnectState?
    ) {

    }

    override fun onReceiveWeightData(device: ICDevice?, data: ICWeightData?) {
        binding.data.text = "Weight = ${data!!.weight_kg}"
        if (data!!.isStabilized) {
            if (data!!.imp.toInt() != 0) {
                val t = String.format(
                    "weight=%.2f,\nbmi=%.2f,\nbody fat=%.2f,\nmuscle=%.2f,\nwater=%.2f,\nbone=%.2f,\nprotein=%.2f,\nbmr=%d,\nvisceral=%.2f,\nskeletal muscle=%.2f,\nphysical age=%d,\nimp=%.2f,\nimp2=%.2f,\nimp3=%.2f,\nimp4=%.2f,\nimp5=%.2f,\ndata_cal_type=%d",
                    data.weight_kg,
                    data.bmi,
                    data.bodyFatPercent,
                    data.musclePercent,
                    data.moisturePercent,
                    data.boneMass,
                    data.proteinPercent,
                    data.bmr,
                    data.visceralFat,
                    data.smPercent,
                    data.physicalAge.toInt(),
                    data.imp,
                    data.imp2,
                    data.imp3,
                    data.imp4,
                    data.imp5,
                    data.data_calc_type
                )
                addLog("Data received!")
                binding.data.text = t
            }
        }
    }

    override fun onReceiveKitchenScaleData(device: ICDevice?, data: ICKitchenScaleData?) {
        addLog(
            device!!.getMacAddr() + ": kitchen data:" + data!!.value_g + "\t" + data.value_lb + "\t" + data.value_lb_oz
                    + "\t" + data.isStabilized
        )
    }

    override fun onReceiveKitchenScaleUnitChanged(device: ICDevice?, unit: ICKitchenScaleUnit?) {
        addLog(device!!.getMacAddr() + ": kitchen unit changed :" + unit)
    }

    override fun onReceiveCoordData(device: ICDevice?, data: ICCoordData?) {
        addLog(device!!.getMacAddr() + ": coord data:" + data!!.getX() + "\t" + data.getY() + "\t" + data.getTime())
    }

    override fun onReceiveRulerData(device: ICDevice?, data: ICRulerData?) {
        addLog(
            device!!.getMacAddr() + ": ruler data :" + data!!.getDistance_cm() + "\t" + data!!.getPartsType() + "\t"
                    + data!!.getTime() + "\t" + data!!.isStabilized()
        )
        if (data!!.isStabilized()) {
            // demo, auto change device show body parts type
            if (data!!.getPartsType() == ICRulerBodyPartsType.ICRulerPartsTypeCalf) {
                return
            }
            ICDeviceManager.shared().settingManager.setRulerBodyPartsType(
                device,
                ICRulerBodyPartsType.valueOf(data!!.getPartsType().value + 1)
            ) { }
        }
    }

    override fun onReceiveRulerHistoryData(device: ICDevice?, data: ICRulerData?) {

    }

    override fun onReceiveWeightCenterData(device: ICDevice?, data: ICWeightCenterData?) {
        addLog(
            device!!.getMacAddr() + ": center data :L=" + data!!.getLeftPercent() + "   R=" + data.getRightPercent()
                    + "\t" + data.getTime() + "\t" + data.isStabilized()
        )
    }

    override fun onReceiveWeightUnitChanged(device: ICDevice?, unit: ICWeightUnit?) {
        addLog(device!!.getMacAddr() + ": weigth unit changed :" + unit)
    }

    override fun onReceiveRulerUnitChanged(device: ICDevice?, unit: ICRulerUnit?) {
        addLog(device!!.getMacAddr() + ": ruler unit changed :" + unit)
    }

    override fun onReceiveRulerMeasureModeChanged(device: ICDevice?, mode: ICRulerMeasureMode?) {
        addLog(device!!.getMacAddr() + ": ruler measure mode changed :" + mode)
    }

    override fun onReceiveMeasureStepData(device: ICDevice?, step: ICMeasureStep?, data2: Any?) {
        when (step) {
            ICMeasureStep.ICMeasureStepMeasureWeightData -> {
                val data = data2 as ICWeightData
                onReceiveWeightData(device, data)
            }

            ICMeasureStep.ICMeasureStepMeasureCenterData -> {
                val data = data2 as ICWeightCenterData
                onReceiveWeightCenterData(device, data)
            }

            ICMeasureStep.ICMeasureStepAdcStart -> {
                addLog(device!!.getMacAddr() + ": start imp... ")
                binding.data.text = "measuring..."
            }

            ICMeasureStep.ICMeasureStepAdcResult -> {
                addLog(device!!.getMacAddr() + ": imp over")
                binding.data.text = "measuring over"
            }

            ICMeasureStep.ICMeasureStepHrStart -> {
                addLog(device!!.getMacAddr() + ": start hr")
            }

            ICMeasureStep.ICMeasureStepHrResult -> {
                val hrData = data2 as ICWeightData
                addLog(device!!.getMacAddr() + ": over hr: " + hrData.hr)
            }

            ICMeasureStep.ICMeasureStepMeasureOver -> {
                val data = data2 as ICWeightData
                addLog(device!!.getMacAddr() + ": over measure")
                onReceiveWeightData(device, data)
            }

            else -> {}
        }
    }

    override fun onReceiveWeightHistoryData(device: ICDevice?, icWeightHistoryData: ICWeightHistoryData?) {
        addLog(
            device!!.getMacAddr() + ": history weight_kg=" + icWeightHistoryData!!.weight_kg + ", imp="
                    + icWeightHistoryData!!.imp
        )
    }

    override fun onReceiveSkipData(device: ICDevice?, data: ICSkipData?) {
        addLog(
            device!!.getMacAddr() + ": skip data: mode=" + data!!.mode + ", param=" + data!!.setting + ",use_time="
                    + data!!.elapsed_time + ",count=" + data!!.skip_count
        )
        if (data!!.isStabilized) {
            val free = StringBuilder()
            free.append("[")
            for (freqData: ICSkipFreqData in data!!.freqs) {
                free.append("dur=").append(freqData.duration).append(", jumpcount=")
                    .append(freqData.skip_count)
                    .append(";")
            }
            free.append("]")
            addLog(
                device!!.getMacAddr() + ": skip data2 : time=" + data!!.time + " mode=" + data!!.mode + ", param="
                        + data!!.setting + ",use_time=" + data!!.elapsed_time + ",count=" + data!!.skip_count + ", avg="
                        + data!!.avg_freq + ", fastest=" + data!!.fastest_freq + ", freqs=" + free
            )
        }
    }

    override fun onReceiveHistorySkipData(device: ICDevice?, data: ICSkipData?) {

    }

    override fun onReceiveBattery(device: ICDevice?, battery: Int, ext: Any?) {

    }

    override fun onReceiveUpgradePercent(
        device: ICDevice?,
        status: ICUpgradeStatus?,
        percent: Int
    ) {

    }

    override fun onReceiveDeviceInfo(device: ICDevice?, deviceInfo: ICDeviceInfo?) {

    }

    override fun onReceiveDebugData(device: ICDevice?, type: Int, obj: Any?) {

    }

    override fun onReceiveConfigWifiResult(device: ICDevice?, state: ICConfigWifiState?) {

    }

    override fun onReceiveHR(device: ICDevice?, hr: Int) {

    }

    override fun onReceiveUserInfo(device: ICDevice?, userInfo: ICUserInfo?) {

    }

    override fun onReceiveRSSI(device: ICDevice?, rssi: Int) {

    }
}