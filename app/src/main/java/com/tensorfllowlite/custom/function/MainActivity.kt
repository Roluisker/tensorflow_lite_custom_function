package com.tensorfllowlite.custom.function

import android.content.res.AssetFileDescriptor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    var interpreter: Interpreter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            interpreter = loadModelFile()?.let { Interpreter(it) }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        button.setOnClickListener {
            try {
                textView.text = powFunctionFromTfLite(editText.text.toString().toFloat())
            } catch (e: IOException) {
                e.printStackTrace()
                textView.text = e.message
            }
        }
    }

    private fun powFunctionFromTfLite(number: Float): String {
        val interpreter = Interpreter(loadModelFile()!!)

        val input = FloatArray(1)
        input[0] = number

        val output = FloatArray(1)

        interpreter.run(input, output)
        return output[0].toString() + ""
    }

    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer? {
        val assetFileDescriptor = this.assets.openFd("concrete_function.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
    }
}