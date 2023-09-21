package com.example.bodycomposition.recogniser

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp

class FaceNetInterpreter(context: Context) {

    // Run to get predict result from tflite model
    private val interpreter = Interpreter(FileUtil.loadMappedFile(context, "facenet.tflite"))

    // Image preprocessing tool with options
    private val imageTensorProcessor = ImageProcessor.Builder()
        .add(ResizeOp(inputDims, inputDims, ResizeOp.ResizeMethod.BILINEAR))
        .build()

    fun getFaceVector() {
        // TODO: Extract face vector from bitmap input
    }

    companion object {
        private const val TAG = "FaceNetInterpreter"

        // Model info
        private const val modelPath = "facenet.tflite"
        private const val inputDims = 160 // Square dimension of input image
        private const val outputSize = 128 // Desired vector size
    }
}