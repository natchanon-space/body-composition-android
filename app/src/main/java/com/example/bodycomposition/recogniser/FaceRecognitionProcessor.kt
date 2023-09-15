package com.example.bodycomposition.recogniser

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.example.bodycomposition.component.BBoxOverlay
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceRecognitionProcessor(overlay: BBoxOverlay? = null, previewView: PreviewView? = null) {

    private var detector: FaceDetector
    private val overlay: BBoxOverlay?
    private val previewView: PreviewView?

    init {
        this.overlay = overlay
        this.previewView = previewView

        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .enableTracking()
            .build()
        this.detector = FaceDetection.getClient(faceDetectorOptions)
    }

    /**
     * Detect faces and render bounding box on attached PreviewView
     */
    @androidx.camera.core.ExperimentalGetImage
    fun liveDetect(imageProxy: ImageProxy) {
        val image = imageProxy.image
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        val inputImage = InputImage.fromMediaImage(image!!, rotationDegrees)

        val reverseDimens = rotationDegrees == 90 || rotationDegrees == 270
        val width = if (reverseDimens) imageProxy.height else imageProxy.width
        val height = if (reverseDimens) imageProxy.width else imageProxy.height

        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                val faceBounds: MutableList<RectF> = arrayListOf()
                for (face in faces) {
                    faceBounds.add(face.boundingBox.transform(width, height, previewView!!)
                    )
                }
                overlay!!.drawBox(faceBounds)
                    imageProxy.close()
            }
            .addOnFailureListener {
                Log.e(TAG, it.toString(), it)
                imageProxy.close()
            }
    }

    /**
     * Return cropped biggest face for face recognition process
     */
    fun cropBiggestFace(imageProxy: ImageProxy): Bitmap {

        return TODO("Provide the return value")
    }

    private fun Rect.transform(width: Int, height: Int, previewView: PreviewView): RectF {
        val scaleX = previewView.width / width.toFloat()
        val scaleY = previewView.height / height.toFloat()

        // Flip for front camera len
        val flippedLeft = width - right
        val flippedRight = width - left

        // Scale all coordinates to match preview
        val scaledLeft = scaleX * flippedLeft
        val scaleTop = scaleY * top
        val scaledRight = scaleX * flippedRight
        val scaledBottom = scaleY * bottom

        return RectF(scaledLeft, scaleTop, scaledRight, scaledBottom)
    }

    private fun cropToBBox(image: Bitmap, bbox: Rect, rotation: Int): Bitmap? {
        var shift: Int = 0
        var image: Bitmap = image
        if (rotation != 0) {
            var matrix: Matrix = Matrix()
            matrix.postRotate(rotation.toFloat())
            image = Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        }
        if (bbox.top >= 0
            && bbox.bottom <= image.width
            && bbox.top + bbox.height()  <= image.height
            && bbox.left >= 0
            && bbox.left + bbox.width() <= image.width) {
            return Bitmap.createBitmap(image, bbox.left, bbox.top + shift, bbox.width(), bbox.height())
        } else {
            return null
        }
    }

    companion object {
        const val TAG = "FaceRecognitionProcessor"
    }
}

