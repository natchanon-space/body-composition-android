package com.example.bodycomposition.recogniser

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * [ref](https://arxiv.org/pdf/1503.03832.pdf)
 */
class FaceNetInterpreter(context: Context) {

    // Run to get predict result from tflite model
    private val interpreter = Interpreter(FileUtil.loadMappedFile(context, "facenet.tflite"))

    // Image preprocessing tool with options
    private val tensorImageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(inputDims, inputDims, ResizeOp.ResizeMethod.BILINEAR))
        .add(StandardizeOp())
        .build()

    fun getFaceVector(image: Bitmap): FloatArray {
        // TODO: Extract face vector from bitmap input
        val tick = System.currentTimeMillis()

        var input = TensorImage.fromBitmap(image)
        input = tensorImageProcessor.process(input)

        Log.d(TAG, "input datatype: ${input.dataType}")

        val output = Array(1) { FloatArray(outputSize) }

        interpreter.run(input.buffer, output)
        Log.d(TAG, "Inference speed in ms: ${System.currentTimeMillis() - tick}")
        Log.d(TAG, output[0].joinToString(" "))
        Log.d(TAG, output[0].size.toString())

        return output[0]
    }

    companion object {
        private const val TAG = "FaceNetInterpreter"

        // Model info
        private const val modelPath = "facenet.tflite"
        private const val inputDims = 160 // Square dimension of input image
        private const val outputSize = 512 // Desired vector size

        // Util function
        /**
         * Squared Euclidean (L2) distance
         */
        fun calculateDistance(face1: FloatArray, face2: FloatArray): Double {
            var distance = 0.0

            if (face1.size != face2.size) {
                throw ArithmeticException("Array size is not equals ${face1.size} and ${face2.size}")
            }

            val size = face1.size
            for (i in 0 until size) {
                distance += (face1[i] + face2[i]).toDouble().pow(2.0)
            }

            return distance
        }
    }

    class StandardizeOp : TensorOperator {

        override fun apply(p0: TensorBuffer?): TensorBuffer {
            val pixels = p0!!.floatArray
            val mean = pixels.average().toFloat()
            var std = sqrt(pixels.map { pi -> (pi - mean).pow(2) }.sum() / pixels.size.toFloat())
            std = max(std, 1f / sqrt(pixels.size.toFloat()))
            for (i in pixels.indices) {
                pixels[i] = (pixels[i] - mean) / std
            }
            val output = TensorBufferFloat.createFixedSize(p0.shape, DataType.FLOAT32)
            output.loadArray(pixels)
            return output
        }
    }
}