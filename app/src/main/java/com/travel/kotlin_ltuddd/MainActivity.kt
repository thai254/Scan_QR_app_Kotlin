package com.travel.kotlin_ltuddd

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BinaryBitmap
import com.google.zxing.ChecksumException
import com.google.zxing.FormatException
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val _databaseQR: DatabaseQR? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scanBtn: ImageButton = findViewById(R.id.scan_btn)
        val genBtn: ImageButton = findViewById(R.id.gen_btn)
        val albumBtn: ImageButton = findViewById(R.id.album_btn)
        val imgToTextBtn: ImageButton = findViewById(R.id.img_to_text_btn)
        val hisBtn: ImageButton = findViewById(R.id.his_btn)
        val usBtn: ImageButton = findViewById(R.id.about_us_btn)

        scanBtn.setOnClickListener{
            val intent = Intent(this, ScanCamera::class.java)
            startActivity(intent)
        }

        hisBtn.setOnClickListener{
            val intent = Intent(this, History::class.java)
            startActivityForResult(intent, 100)
        }

        genBtn.setOnClickListener{
            val intent = Intent(this, GenerateActivity::class.java)
            Log.v("gen", "OK")
            startActivity(intent)
        }

        albumBtn.setOnClickListener {
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            Log.v("album", pickIntent.toString())
            startActivityForResult(pickIntent, 111)
        }

        usBtn.setOnClickListener{
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111) {
            if (data != null) {
                val selectedImage = data.data
                if (selectedImage != null) {
                    try {
                        val inputStream = contentResolver.openInputStream(selectedImage)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream!!.close()

                        // Quét mã QR của ảnh bằng ZXing
                        val reader = QRCodeReader()
                        try {
                            val pixels = IntArray(bitmap.width * bitmap.height)
                            bitmap.getPixels(
                                pixels,
                                0,
                                bitmap.width,
                                0,
                                0,
                                bitmap.width,
                                bitmap.height
                            )
                            val source = RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
                            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                            val qrResult = reader.decode(binaryBitmap)

                            // Xử lý kết quả mã QR ở đây
                            Log.v("QR Code", qrResult.text)
                            val IntentImage = Intent(this@MainActivity, ResultActivity::class.java)

                            IntentImage.putExtra("resultImage", qrResult.text)
                            startActivity(IntentImage)


                        } catch (e: NotFoundException) {
                            e.printStackTrace()
                            Log.v("QR Code", "Không thể quét mã QR từ ảnh")
                            Toast.makeText(
                                this@MainActivity,
                                "Không thể quét ảnh",
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: ChecksumException) {
                            e.printStackTrace()
                            Log.v("QR Code", "Không thể quét mã QR từ ảnh")
                            Toast.makeText(
                                this@MainActivity,
                                "Không thể quét ảnh",
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: FormatException) {
                            e.printStackTrace()
                            Log.v("QR Code", "Không thể quét mã QR từ ảnh")
                            Toast.makeText(
                                this@MainActivity,
                                "Không thể quét ảnh",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        if (requestCode == 100) {
            if (resultCode == History.RESULT_OK) {
                val result = data!!.getStringExtra(History.EXTRA_DATA)
            } else {
            }
        }
    }

}