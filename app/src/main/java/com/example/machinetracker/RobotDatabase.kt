package com.example.machinetracker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//class to manage database
class RobotDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    //writable database; by rows
    val allData: Cursor
        get() {
            val db = this.writableDatabase
            return db.rawQuery("select * from $TABLE_NAME", null)
        }

    //creating a database of characteristics
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,DESCRIPTION TEXT,HEIGHT INTEGER)")
    }

    //updating database table
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    //to insert data; it checks if it is appropriate
    fun insertData(name: String, description: String, height: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, name)
        contentValues.put(COL_3, description)
        contentValues.put(COL_4, height)
        val result : Long = db.insert(TABLE_NAME, null, contentValues)
        return result != -1L //trying to return true in this case
    }

    //updates data
    fun updateData(id: String, robotName: String, description: String, height: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_1, id)
        contentValues.put(COL_2, robotName)
        contentValues.put(COL_3, description)
        contentValues.put(COL_4, height)
        db.update(TABLE_NAME, contentValues, "ID = ?", arrayOf(id))
        return true
    }

    //deletes data by row according to ID
    fun deleteData(id: String): Int? {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "ID = ?", arrayOf(id))
    }

    //objects
    companion object {
        val DATABASE_NAME = "Robot.db"
        val TABLE_NAME = "robot_table"
        val COL_1 = "ID"
        val COL_2 = "NAME"
        val COL_3 = "DESCRIPTION"
        val COL_4 = "HEIGHT"
    }

    //gets ID of the robot to put into listView ID in ListView of activity_list.xml
    fun getItemID(id: String): Cursor {
        val db: SQLiteDatabase = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        return db.rawQuery(query, null)
    }
}