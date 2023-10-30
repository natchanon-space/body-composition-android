package com.example.bodycomposition.fragment

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import com.example.bodycomposition.R
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentFaceRegistrationBinding
import com.example.bodycomposition.model.DataViewModel
import com.example.bodycomposition.recogniser.FaceRecognitionProcessor
import com.example.bodycomposition.recogniser.FaceRecognitionProcessor.FaceRecognitionCallback
import com.example.bodycomposition.utils.RequirePermissions
import java.util.concurrent.ExecutorService


@ExperimentalGetImage class FaceRegistrationFragment : BaseFragment<FragmentFaceRegistrationBinding>(), ImageAnalysis.Analyzer, FaceRecognitionCallback {

    private val viewModel: DataViewModel by activityViewModels()

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var faceRecognitionProcessor: FaceRecognitionProcessor

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFaceRegistrationBinding {
        return FragmentFaceRegistrationBinding.inflate(inflater, container, false)
    }

    override fun bindData() {
        binding.apply {
            faceRegistrationFragment = this@FaceRegistrationFragment
            viewModel = this.viewModel
        }

        faceRecognitionProcessor = FaceRecognitionProcessor(requireContext(), binding.overlay, binding.viewFinder, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (RequirePermissions.allPermissionsGranted(this)) {
            startCamera()
        } else {
            RequirePermissions.requestPermissions(this)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview binding
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), this)

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("Error", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    fun takePicture() {
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

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun analyze(imageProxy: ImageProxy) {
        faceRecognitionProcessor.liveDetect(imageProxy)
    }

    override fun onFaceDetected(faceBitmap: Bitmap?, faceVector: FloatArray?) {

        Log.d(TAG, "Suspend 2: register value")

        if (faceBitmap != null && faceVector != null) {
            viewModel.setFaceBitmap(faceBitmap)
            viewModel.setFaceVector(faceVector)

            Log.d(TAG, "Navigate is called!")
            findNavController().navigate(R.id.action_faceRegistrationFragment_to_addFaceFragment)
        } else {
            Toast.makeText(requireContext(), "No face detected!!!", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "CroppedBitmap and/or FaceVector are null!")
        }
    }

    companion object {
        private const val TAG = "FaceRegistrationFragment"
    }
}