package com.travel.kotlin_ltuddd

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cursoradapter.widget.CursorAdapter


class History : AppCompatActivity() {

    private var dbQR: DatabaseQR? = null
    private var _DisplayCursorAdapter: DisplayCursorAdapter? = null
    private var listView: ListView? = null
    var edtTextFind: EditText? = null
    var btnFind: ImageButton? = null

    companion object {
        const val EXTRA_DATA = "EXTRA_DATA"
        const val RESULT_OK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        edtTextFind = findViewById(R.id.qr_info_timkiem)
        btnFind = findViewById(R.id.qr_info_find)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

        listView = findViewById<ListView>(R.id.listView)

        Log.v("QR his1", "start create")
        dbQR = DatabaseQR(this)
        Log.v("QR his2", "OK")
        dbQR?.open()
        Log.v("QR his3", "OK")
        showData2ListView("")

        edtTextFind?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                showData2ListView(edtTextFind!!.getText().toString())
            }
        })
    }



    private fun showData2ListView(inputFilter: String) {
        val cursor: Cursor = dbQR?.QR_fetchData(inputFilter, 0) ?: return
        _DisplayCursorAdapter = DisplayCursorAdapter(this, cursor, dbQR!!)
        Log.v("QR his4", "OK")
        listView?.setAdapter(_DisplayCursorAdapter)
    }

    inner class DisplayCursorAdapter(context: Context?, cursor: Cursor?, private val dbQR: DatabaseQR) :
        CursorAdapter(context, cursor, 0) {

        var sNoidung = ""
        var sThoigian = ""

        override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
            return LayoutInflater.from(context).inflate(R.layout.qr_info, parent, false)
        }

        override fun bindView(view: View, context: Context, cursor: Cursor) {
            val qr_info_noidung = view.findViewById<View>(R.id.qr_info_noidung) as TextView
            val qr_info_thoigian = view.findViewById<View>(R.id.qr_info_thoigian) as TextView
            val qr_info_reused = view.findViewById<View>(R.id.qr_info_reused) as ImageView
            val qr_info_trash = view.findViewById<View>(R.id.qr_info_trash) as ImageView
            val qr_info_layout = view.findViewById<View>(R.id.qr_info_layout) as LinearLayout


            val sNoidung = cursor?.getString(cursor.getColumnIndexOrThrow(DatabaseQR.QR_NOIDUNG))
                ?.replace("<br>", "\n")

            sThoigian = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseQR.QR_DATE))
                .replace("<br>", "\n")

            var sPhanloai = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseQR.QR_PHANLOAI))

            sPhanloai = if (sPhanloai == "SCAN") " - scan" else " - creat"
            qr_info_noidung.text = sNoidung
            qr_info_thoigian.text = sThoigian
            qr_info_reused.tag =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseQR.QR_ROWID)) + "#@@" + sNoidung + "#@@" + sThoigian
            qr_info_reused.setOnClickListener {
                val arrTag =
                    qr_info_reused.tag.toString().split("#@@".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                // returnIntent.putExtra("QRresult", arrTag[1]);

                val data = Intent()
                data.putExtra(EXTRA_DATA, arrTag[1])
                setResult(RESULT_OK, data)
                finish()
            }
            qr_info_trash.setOnClickListener { v ->
                val arrTag =
                    qr_info_reused.tag.toString().split("#@@".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val alertDialogBuilder = AlertDialog.Builder(
                    v.context
                )
                alertDialogBuilder.setTitle("Confirm?")
                alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, id ->
                        val dbAdapterMega = DatabaseQR(getApplicationContext())
                        dbAdapterMega.open()
                        dbAdapterMega.QR_deleteDatabyID(arrTag[0].toInt())
                        showData2ListView("")
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, id -> dialog.cancel() }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }

        }
    }

}