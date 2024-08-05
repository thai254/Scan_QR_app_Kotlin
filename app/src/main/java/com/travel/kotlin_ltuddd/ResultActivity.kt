package com.travel.kotlin_ltuddd

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class ResultActivity : AppCompatActivity() {

    private val _databaseQR: DatabaseQR? = null
    var bitmap: Bitmap? = null

    companion object {
        const val PERMISSION_REQUEST_CODE = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val btnCopy: Button = findViewById(R.id.btn_copy)
        val txtResult: TextView = findViewById(R.id.txtQR)
        Log.v("album bug", "1")

        //nhận dữ liệu từ ScanCamera
        val intent = intent
        val result = intent.getStringExtra("result")
        // nhận dữ liệu từ MainA
        val resultImage = intent.getStringExtra("resultImage")

        if (result != null) txtResult.setText(result)
        try {
            val currentTime = Calendar.getInstance().time
            Log.v("Time right now", currentTime.toString())
            _databaseQR?.QR_insertData("creat", result.toString(), currentTime)
            Log.v("SAVE DB", "OK + " + result.toString() + "")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        if (resultImage != null) {
            txtResult.setText(resultImage)
        }

        //sao chép vào clipboard
        btnCopy.setOnClickListener {
            val tQR: String = txtResult.getText().toString()
            val clipBM = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Data", tQR)
            clipBM.setPrimaryClip(clipData)
            Toast.makeText(this@ResultActivity, "Đã sao chép vào clipboard", Toast.LENGTH_SHORT).show()
        }

    }

}