package com.example.bodycomposition.fragment

import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bodycomposition.databinding.FragmentFaceRegistrationBinding
import com.example.bodycomposition.utils.RequirePermissions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import java.util.concurrent.ExecutorService

@ExperimentalGetImage class FaceRegistrationFragment : Fragment(), ImageAnalysis.Analyzer {

    private lateinit var binding: FragmentFaceRegistrationBinding

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentFaceRegistrationBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.apply {

        }
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

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            // Prepare input image using CameraX
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Get base face detection model
            val detector = FaceDetection.getClient()

            // Event listener for image processing
            detector.process(image)
                .addOnSuccessListener { faces: List<Face> ->
                    // Drawing face bounding box on overlay
                    val faceBounds: MutableList<RectF> = arrayListOf()
                    val rotation = imageProxy.imageInfo.rotationDegrees
                    val reverseDimens = rotation == 90 || rotation == 270
                    val width = if (reverseDimens) imageProxy.height else imageProxy.width
                    val height = if (reverseDimens) imageProxy.width else imageProxy.height
                    for (face in faces) {
                        val bounds = face.boundingBox.transform(width, height)
                        faceBounds.add(RectF(bounds))
                    }
                    binding.overlay.drawBox(faceBounds)

                    // Close before starting new image analysis
                    imageProxy.close()
                }
                .addOnFailureListener {
                    Log.e(TAG, it.toString(), it)
                    imageProxy.close()
                }
        }
    }

    private fun Rect.transform(width: Int, height: Int): RectF {
        val scaleX = binding.viewFinder.width / width.toFloat()
        val scaleY = binding.viewFinder.height / height.toFloat()

        // Flip for front camera len
        val flippedLeft = width - right
        val flippedRight = width - left

        // Scale all coordinates to match preview
        val scaledLeft = scaleX * flippedLeft
        val scaledTop = scaleY * top
        val scaleRight = scaleX * flippedRight
        val scaleBottom = scaleY * bottom

        return RectF(scaledLeft, scaledTop, scaleRight, scaleBottom)
    }

    companion object {
        private const val TAG = "FaceRegistrationFragment"
    }
}