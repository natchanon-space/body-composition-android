package com.example.bodycomposition.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.navigation.fragment.findNavController
import cn.icomon.icdevicemanager.ICDeviceManager
import cn.icomon.icdevicemanager.callback.ICScanDeviceDelegate
import cn.icomon.icdevicemanager.model.device.ICScanDeviceInfo
import com.example.bodycomposition.R
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentScanDeviceBinding
import com.example.bodycomposition.utils.EventMgr

class ScanDeviceFragment : BaseFragment<FragmentScanDeviceBinding>(), ICScanDeviceDelegate {

    private var listView: ListView? = null
    private var adapter: ArrayAdapter<String>? = null
    private var _devices = ArrayList<ICScanDeviceInfo>()
    private val data = java.util.ArrayList<String>()


    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentScanDeviceBinding {
        return FragmentScanDeviceBinding.inflate(inflater, container, false)
    }

    override fun bindData() {
        binding.apply {
            scanDeviceFragment = this@ScanDeviceFragment
        }

        // initiate list view
        listView = binding.lvScan
        adapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_list_item_1, data
        )
        listView!!.adapter = adapter

        listView!!.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                ICDeviceManager.shared().stopScan()
                val device = _devices[i]
                EventMgr.post("SCAN", device)
            }

        ICDeviceManager.shared().scanDevice(this)
    }

    fun onSubmit() {
        findNavController().navigate(R.id.action_scanDeviceFragment_to_visualizeFragment)
    }

    /**
     * ICScanDeviceDelegate
     */
    override fun onScanResult(deviceInfo: ICScanDeviceInfo) {
        var isE = false
        for (deviceInfo1 in _devices) {
            if (deviceInfo1.getMacAddr().equals(deviceInfo.getMacAddr(), ignoreCase = true)) {
                deviceInfo1.setRssi(deviceInfo.getRssi())
                isE = true
                break
            }
        }
        if (!isE) {
            _devices.add(deviceInfo)
        }
        data.clear()
        for (deviceInfo1 in _devices) {
            val str =
                deviceInfo1.getName() + "   " + deviceInfo1.getMacAddr() + "   " + deviceInfo1.getRssi()
            data.add(str)
        }
        adapter!!.notifyDataSetChanged()
    }
}
