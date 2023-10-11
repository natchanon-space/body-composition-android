package com.example.bodycomposition.fragment

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bodycomposition.R
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.dao.AppDatabase
import com.example.bodycomposition.dao.User
import com.example.bodycomposition.databinding.FragmentLoginBinding
import com.example.bodycomposition.model.DataViewModel
import com.example.bodycomposition.recogniser.FaceNetInterpreter
import com.example.bodycomposition.recogniser.FaceRecognitionProcessor
import com.example.bodycomposition.utils.RequirePermissions
import com.example.bodycomposition.utils.UserInfo
import com.example.bodycomposition.utils.UserType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService

@ExperimentalGetImage
class LoginFragment : BaseFragment<FragmentLoginBinding>(), ImageAnalysis.Analyzer, FaceRecognitionProcessor.FaceRecognitionCallback {

    private val viewModel: DataViewModel by activityViewModels()

    private lateinit var db: AppDatabase

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var faceRecognitionProcessor: FaceRecognitionProcessor

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun bindData() {
        binding.apply {
            loginFragment = this@LoginFragment
            viewModel = this.viewModel
        }

        faceRecognitionProcessor = FaceRecognitionProcessor(requireContext(), binding.overlay, binding.viewFinder, this)

        db = AppDatabase.getDatabase(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request camera permissions
        if (RequirePermissions.allPermissionsGranted(this)) {
            startCamera()
        } else {
            RequirePermissions.requestPermissions(this)
        }
    }

    fun takePhoto() {
        // TODO: Implement login method
        Toast.makeText(requireContext(), "Login button pressed!", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Login button pressed!")

        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    Log.d(TAG, "==TAKE PICTURE STARTING==")

                    Log.d(TAG, "Suspend 1: crop image")
                    faceRecognitionProcessor.cropBiggestFace(imageProxy)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.d(TAG, "Image capture error!", exception)
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), this)

            // Select front camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalysis)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun analyze(imageProxy: ImageProxy) {
        faceRecognitionProcessor.liveDetect(imageProxy)
    }

    override fun onFaceDetected(faceBitmap: Bitmap?, faceVector: FloatArray?) {
        // TODO: find closest face and return user then navigate to registration
        Log.d(TAG, "onFaceDetected")

        val dao = db.userDao()
        var userList: List<User>? = null

        lifecycleScope.launch(Dispatchers.IO) {
            suspend {
                userList = dao.getAll()
                Log.d(TAG, "1st: Retrieve user list")
            }.invoke()

            lifecycleScope.launch(Dispatchers.Main) {
                suspend {
                    // Run Toast on Thread requirement
                    if (Looper.myLooper() == null) {
                        Looper.prepare()
                    }

                    val threshold: Double = 1.1 // Temporary threshold
                    var user: User? = null
                    var distance: Double? = null

                    Log.d(TAG, "2nd: Finding closest face")
                    if (userList == null || userList!!.isEmpty()) {
                        Log.d(TAG, "No face in database!")
                        Toast.makeText(requireContext(), "No face in database!", Toast.LENGTH_SHORT).show()
                    }

                    // Find the closest face within threshold
                    for (u in userList!!) {
                        val currentDistance = FaceNetInterpreter.calculateDistance(faceVector!!, u.faceVector!!)
                        var update = false

                        Log.d(TAG, "Comparing to ${u.name}, distance: $currentDistance")

                        if (currentDistance <= threshold) {
                            if (user != null) {
                                if (currentDistance < distance!!) {
                                    update = true
                                }
                            } else {
                                update = true
                            }
                        }

                        if (update) {
                            user = u
                            distance = currentDistance
                        }
                    }

                    if (user != null) {
                        viewModel.setUserInfo(UserInfo(user.date!!, user.height!!, user.name!!))
                        viewModel.setUserType(UserType.AUTH)

                        Log.d(TAG, "Distance: $distance Name: ${user.name}")

                        Log.d(TAG, "Navigate is called!")
                        findNavController().navigate(
                            R.id.action_loginFragment_to_scanDeviceFragment
                        )
                    } else {
                        Log.d(TAG, "No face recognized!")
                        Toast.makeText(requireContext(), "No face recognized", Toast.LENGTH_SHORT).show()
                    }
                }.invoke()
            }
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}