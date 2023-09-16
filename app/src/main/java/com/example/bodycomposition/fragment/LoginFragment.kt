package com.example.bodycomposition.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentLoginBinding
import com.example.bodycomposition.recogniser.FaceRecognitionProcessor
import com.example.bodycomposition.utils.RequirePermissions
import java.util.concurrent.ExecutorService

@ExperimentalGetImage
class LoginFragment : BaseFragment<FragmentLoginBinding>(), ImageAnalysis.Analyzer, FaceRecognitionProcessor.FaceRecognitionCallback {

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
        }

        faceRecognitionProcessor = FaceRecognitionProcessor(binding.overlay, binding.viewFinder, this)
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
        Toast.makeText(requireContext(), "Login button pressed!", Toast.LENGTH_SHORT)
        Log.d(TAG, "Login button pressed!")
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

    override fun onFaceDetected(faceBitmap: Bitmap?) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "LoginFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}