package com.travel.kotlin_ltuddd

import android.Manifest.permission
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

class GenerateActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate)

        Log.v(" start gen", "OK")

        val genBtn: Button = findViewById(R.id.btn_gen)
        val genEdt: EditText = findViewById(R.id.txt_genQR)

        genBtn.setOnClickListener(View.OnClickListener {
            val mtFW = MultiFormatWriter()
            try {
                val text: String = genEdt.getText().toString()
                Log.v("txtgen", text)
                var dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_generate)
                Log.v("di1", "ok")

                val utf8Bytes = text.toByteArray(StandardCharsets.UTF_8)
                Log.v("di2", "ok")

                val utf8Text = String(utf8Bytes, StandardCharsets.ISO_8859_1)
                Log.v("di3", "ok")
                val bMatrix = mtFW.encode(utf8Text, BarcodeFormat.QR_CODE, 700, 700)
                val bcEncoder = BarcodeEncoder()
                val bitmap = bcEncoder.createBitmap(bMatrix)
                Log.v("bitmap", bitmap.toString())
                val imageView = dialog.findViewById<ImageView>(R.id.img_genqr480)
                imageView?.let {
                    it.setImageBitmap(bitmap)
                }

                dialog.show()
                dialog.setCancelable(false)

                dialog.findViewById<Button>(R.id.btnDialogDownload).setOnClickListener {
                    if (!checkPermission()) {
                        requestPermission(applicationContext)
                        if (!checkPermissionAgain(applicationContext)) {
                            Toast.makeText(
                                dialog.context,"Storage permission denied!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Log.v("img_bitmap", imageView.getDrawable().toBitmap().toString())
                    val bitmap2 = imageView.getDrawable().toBitmap()
                    saveImageToGallery(applicationContext, bitmap2)
                }

                dialog.findViewById<Button>(R.id.btnDialogCancel).setOnClickListener {
                    dialog.dismiss()
                }

            } catch (e: WriterException) {
                throw RuntimeException(e)
            }
        })
    }

    private fun requestPermission(context: Context?) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun checkPermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED && result == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermissionAgain(context: Context): Boolean {
        val permission = "android.permission.CAMERA"
        val res = context.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    private fun saveImageToGallery(context: Context, bmp: Bitmap): Boolean {
        try {
            val storePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + "QRscan"
            val appDir = File(storePath)
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val file = File(appDir, fileName)
            val fos = FileOutputStream(file)

            val isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos)
            fos.flush()
            fos.close()

            val uri = Uri.fromFile(file)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            return if (isSuccess) {
                Toast.makeText(context, "Saved to Photos", Toast.LENGTH_SHORT).show()
                true
            } else {
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

}