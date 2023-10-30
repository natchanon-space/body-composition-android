package com.example.bodycomposition.recogniser

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.example.bodycomposition.component.BBoxOverlay
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceRecognitionProcessor(context: Context, overlay: BBoxOverlay? = null, previewView: PreviewView? = null, callback: FaceRecognitionCallback) {

    interface FaceRecognitionCallback {
        fun onFaceDetected(faceBitmap: Bitmap?, faceVector: FloatArray?)
    }

    private var detector: FaceDetector
    private val overlay: BBoxOverlay?
    private val previewView: PreviewView?
    private val callback: FaceRecognitionCallback
    private val faceNetInterpreter = FaceNetInterpreter(context)

    // TODO: Refactor all face recognition related code to this! (except for facenet interpreter)
    init {
        this.overlay = overlay
        this.previewView = previewView
        this.callback = callback

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
                    faceBounds.add(face.boundingBox.transform(width, height, previewView!!))
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
    @RequiresApi(Build.VERSION_CODES.O)
    @androidx.camera.core.ExperimentalGetImage
    fun cropBiggestFace(imageProxy: ImageProxy): Pair<Bitmap?, FloatArray?> {

        Log.d(TAG, "==START CROP BIGGEST FACE==")

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        val bitmap = imageProxy.toBitmap()
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        Log.d(TAG, "(cropBiggestFace) Original w:${bitmap.width} h:${bitmap.height}")

        var croppedBitmap: Bitmap? = null
        var faceVector: FloatArray? = null

        detector.process(inputImage)
            .addOnSuccessListener {faces ->
                Log.d(TAG, "(cropBiggestFace) Begin processing")
                Log.d(TAG, "len faces ${faces.size}")

                if (faces.size > 0) {
                    // TODO: change to return biggest face

                    Log.d(TAG, "FACE[0]: ${faces[0].boundingBox}")
                    val face = faces[0]
                    croppedBitmap = cropToBBox(bitmap, face.boundingBox)


                    if (croppedBitmap != null) {
                        Log.d(TAG, "(cropBiggestFace) Cropped w:${croppedBitmap!!.width} h:${croppedBitmap!!.height}")
                        croppedBitmap = flipBitmap(rotateBitmap(croppedBitmap!!, rotationDegrees))

                        // FaceNet
                        faceVector = faceNetInterpreter.getFaceVector(croppedBitmap!!)
                        Log.d(TAG, "Complete?? $faceVector")
                    } else {
                        Log.d(TAG, "(cropBiggestFace) Cropped: null")
                    }

                    imageProxy.close()
                } else {
                    Log.d(TAG, "(cropBiggestFace) No face detected!")
                    imageProxy.close()
                }
            }
            .addOnCompleteListener {
                Log.d(TAG, "cropped image is null: ${croppedBitmap == null}")
                callback.onFaceDetected(croppedBitmap, faceVector)
            }

        return croppedBitmap to faceVector
    }

    private fun Rect.transform(width: Int, height: Int, previewView: PreviewView): RectF {
        // TODO: handle screen rotation
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

    private fun cropToBBox(image: Bitmap, boundingBox: Rect): Bitmap? {
        var width = boundingBox.width()
        var height = boundingBox.height()
        if ((boundingBox.left + width) > image.width) {
            width = image.width - boundingBox.left
        }
        if ((boundingBox.top + height) > image.height) {
            height = image.height - boundingBox.top
        }
        return Bitmap.createBitmap(image, boundingBox.left, boundingBox.top, width, height)
    }

    private fun ImageProxy.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun rotateBitmap(bitmap: Bitmap, rotationDegree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotationDegree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
    }

    private fun flipBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    companion object {
        const val TAG = "FaceRecognitionProcessor"

    }
}
