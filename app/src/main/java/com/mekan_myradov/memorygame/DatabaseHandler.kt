package com.mekan_myradov.memorygame

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.security.AccessControlContext

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "db_memorygame"
        private const val TABLE_NAME = "tbl_scores"

        private const val KEY_ID = "id"
        private const val KEY_DATE = "date"
        private const val KEY_TIME = "time"
        private const val KEY_SCORE = "score"
        private const val KEY_LEVEL = "level"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                     + KEY_DATE + " TEXT," + KEY_TIME + " TEXT," + KEY_SCORE + " INTEGER,"
                     + KEY_LEVEL + " INTEGER" + ")")
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addRecord(rec: ModelClass): Long{
        val db = this.writableDatabase

        // Prepare data
        val contentValues = ContentValues()
        contentValues.put(KEY_DATE, rec.date)
        contentValues.put(KEY_TIME, rec.time)
        contentValues.put(KEY_SCORE, rec.score)
        contentValues.put(KEY_LEVEL, rec.level)

        // Insert
        val success = db.insert(TABLE_NAME, null, contentValues)

        db.close()
        return success
    }

    @SuppressLint("Range")
    fun readRecord(): ArrayList<ModelClass> {

        val recList: ArrayList<ModelClass> = ArrayList<ModelClass>()

        val query = "SELECT * FROM $TABLE_NAME ORDER BY id DESC"

        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(query, null)
        }catch (e: SQLiteException){
            db.execSQL(query)
            return ArrayList()
        }

        var date: String
        var time: String
        var score: Int
        var level: Int

        if (cursor.moveToFirst()){
            do{
                date = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                time = cursor.getString(cursor.getColumnIndex(KEY_TIME))
                score = cursor.getInt(cursor.getColumnIndex(KEY_SCORE))
                level = cursor.getInt(cursor.getColumnIndex(KEY_LEVEL))

                val rec = ModelClass(date = date, time = time, score = score, level = level)
                recList.add(rec)
            }while (cursor.moveToNext())
        }

        return recList
    }
}