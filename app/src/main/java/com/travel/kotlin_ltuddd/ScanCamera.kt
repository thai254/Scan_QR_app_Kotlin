package com.travel.kotlin_ltuddd

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback

class ScanCamera : AppCompatActivity() {

    private var mCodeScanner: CodeScanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_camera)
        Log.v("1", "OK")
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

        if (ContextCompat.checkSelfPermission(this@ScanCamera, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this@ScanCamera,
                arrayOf(Manifest.permission.CAMERA),
                123
            )
        } else {
            startScanning()
        }
    }

    private fun startScanning() {
        Log.v("2", "OK")
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        mCodeScanner = CodeScanner(this, scannerView)
        mCodeScanner!!.formats = CodeScanner.ALL_FORMATS
        mCodeScanner!!.isAutoFocusEnabled = true
        mCodeScanner!!.autoFocusMode = AutoFocusMode.SAFE
        mCodeScanner!!.setAutoFocusInterval(2000L)
        Log.v("3scan", "3ok")
        mCodeScanner!!.decodeCallback = DecodeCallback { result ->
            runOnUiThread {
                val IntentText = Intent(this@ScanCamera, ResultActivity::class.java)
                IntentText.putExtra("result", result.text)
                setResult(RESULT_OK, IntentText)
                startActivity(IntentText)
            }
        }
        scannerView.setOnClickListener { mCodeScanner!!.startPreview() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show()
                startScanning()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mCodeScanner?.startPreview()
    }

    override fun onPause() {
        mCodeScanner?.releaseResources()
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}