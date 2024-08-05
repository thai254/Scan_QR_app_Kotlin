package com.travel.kotlin_ltuddd

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date

class DatabaseQR(private val mCtx: Context) {
    private var mDbHelper: DatabaseHelper? = null
    private var mDb: SQLiteDatabase? = null
    private var dateFormat: SimpleDateFormat? = null
    private val formatDateTime = "yyyy-MM-dd HH:mm:ss"

    private class DatabaseHelper internal constructor(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            // Log.w(TAG, "CREATE DATABASE");
            db.execSQL(DATABASE_CREATE_QR)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.w(
                TAG, "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
            )
            db.execSQL("DROP TABLE IF EXISTS " + QR_TABLE)
            onCreate(db)
        }
    }

    @Throws(SQLException::class)
    fun open(): DatabaseQR {
        mDbHelper = DatabaseHelper(mCtx)
        mDb = mDbHelper!!.writableDatabase
        return this
    }

    fun close() {
        if (mDbHelper != null) {
            mDbHelper!!.close()
        }
    }

    @Throws(Exception::class)
    fun QR_insertData(phanloai: String, noidung: String, datetime: Date?): Long {
        dateFormat = SimpleDateFormat(formatDateTime)
        val initialValues = ContentValues()
        initialValues.put(QR_PHANLOAI, phanloai.trim { it <= ' ' })
        initialValues.put(QR_NOIDUNG, noidung.trim { it <= ' ' })
        initialValues.put(QR_DATE, dateFormat!!.format(datetime))
        return mDb!!.insert(QR_TABLE, null, initialValues)
        Log.v("insert data", QR_PHANLOAI +" + "+ QR_DATE +" + "+ QR_NOIDUNG)
    }

    @Throws(Exception::class)
    fun QR_updateData(rowId: Long, phanloai: String, noidung: String, datetime: String?): Long {
        dateFormat = SimpleDateFormat(formatDateTime)
        val initialValues = ContentValues()
        initialValues.put(QR_PHANLOAI, phanloai.trim { it <= ' ' })
        initialValues.put(QR_NOIDUNG, noidung.trim { it <= ' ' })
        return mDb!!.update(QR_TABLE, initialValues, QR_ROWID + "=" + rowId, null)
            .toLong()
    }

    fun QR_deleteAllData(): Boolean {
        var doneDelete = 0
        doneDelete = mDb!!.delete(QR_TABLE, null, null)
        //Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0
    }

    fun QR_deleteDatabyID(_id: Int): Boolean {
        var doneDelete = 0
        doneDelete = mDb!!.delete(QR_TABLE, "_id=$_id", null)
        //Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0
    }

    @Throws(SQLException::class)
    fun QR_fetchData(inputText: String?, iTypeOrder: Int): Cursor? {
        val sOrder = QR_DATE + " DESC "
        var mCursor: Cursor? = null
        mCursor = if (inputText == null || inputText.length == 0) {
            mDb!!.query(
                QR_TABLE, arrayOf(
                    QR_ROWID, QR_PHANLOAI,
                    QR_NOIDUNG, QR_DATE
                ),
                QR_PHANLOAI + " NOT IN ('RSA') ", null, null, null, sOrder
            )
        } else {
            val _inputText: String = inputText
            mDb!!.query(
                QR_TABLE, arrayOf(
                    QR_ROWID, QR_PHANLOAI,
                    QR_NOIDUNG, QR_DATE
                ),
                QR_NOIDUNG + " like '%" + _inputText + "%' ",
                null, null, null, sOrder
            )
        }
        mCursor?.moveToFirst()
        return mCursor
    }

    companion object {
        const val DATABASE_NAME = "DBQR"
        private const val DATABASE_VERSION = 1

        //table QR CODE
        private const val QR_TABLE = "tblQR"
        const val QR_ROWID = "_id"
        const val QR_PHANLOAI = "phanloai" //CREAT, SCAN
        const val QR_NOIDUNG = "noidung"
        const val QR_DATE = "time"
        private const val TAG = "EncryptDbAdapter"
        private const val DATABASE_CREATE_QR = "CREATE TABLE if not exists " + QR_TABLE + " (" +
                QR_ROWID + " integer PRIMARY KEY autoincrement," +
                QR_PHANLOAI + "," + QR_DATE + "," + QR_NOIDUNG + "," +
                " UNIQUE (" + QR_ROWID + "));"
    }
}
