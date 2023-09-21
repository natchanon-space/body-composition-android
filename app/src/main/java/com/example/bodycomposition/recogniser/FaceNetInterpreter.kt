package com.example.bodycomposition.recogniser

import android.content.Context
import com.example.bodycomposition.utils.Constant
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp

class FaceNetInterpreter(context: Context) {
    private val interpreter = Interpreter(FileUtil.loadMappedFile(context, "facenet.tflite" ))
    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(Constant.INPUT_IMAGE_DIM, Constant.INPUT_IMAGE_DIM, ResizeOp.ResizeMethod.BILINEAR))
        .build()

    companion object {
        private const val TAG = "FaceNetInterpreter"
    }
}