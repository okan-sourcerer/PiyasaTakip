package com.example.piyasatakip

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBConnectionHelper (val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, VERSION) {

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        const val DB_NAME = "Piyasa_Takip"
        const val TABLE_NAME = "IMAGE_MAP"
        const val NAME = "NAME"
        const val LOCATION = "URI"
        const val DATE = "TYPE"
        const val VERSION = 1
        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE $TABLE_NAME (" +
                    "$NAME TEXT PRIMARY KEY," +
                    "$LOCATION TEXT" +
                    ")"
    }

    fun addEntry(){

    }
}