package com.example.machinetracker

import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayList

class RobotList : AppCompatActivity() {

    internal lateinit var mDatabaseHelper: RobotDatabase
    private var mListView: ListView? = null
    var robotName:String? = null
    var robotDescription:String? = null
    var robotHeight:String? = null
    var robotId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        mListView = findViewById<View>(R.id.listView) as ListView
        mDatabaseHelper =  RobotDatabase(this)
        populateListView()

    }

    //get the data and append to a list
    private fun populateListView() {
        val data = mDatabaseHelper.allData
        val listData = ArrayList<String>()

        while (data.moveToNext()) {

            //get the values from the database, and then adds it to the ArrayList to be view in list
            listData.add(
                "Special ID :    " + data.getString(1) + "\n" +
                        "Robot ID :       " + data.getLong(0) + "\n" +
                        "Description :  " + data.getString(2) + "\n" +
                        "Height :           " + data.getString(3) + "\n")

            robotName = (data.getString(1))
            robotDescription = (data.getString(2))
            robotHeight = (data.getString(3))
            robotId = (data.getString(0))

            //Enable default list adapter and set the adapter; arrayAdapter to viewAdapter essentially
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
            mListView!!.adapter = adapter

            //set an onItemClickListener to the ListView; to view/edit data (back in mainActivity)
            mListView!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val robot = adapterView.getItemAtPosition(i).toString()

                //stores all the inputted data into variable
                val data = mDatabaseHelper.getItemID(robot)

                var itemID = -1

                //gets robot information from SQL table/database associated with its ID
                while (data.moveToNext()) {
                    itemID = data.getInt(0)
                }

                //to ensure that all special IDs get editScreenIntent (arrays<Strings> start at 0)
                if (itemID > -1) {
                    val editScreenIntent = Intent(this@RobotList, MainActivity::class.java)
                    editScreenIntent.putExtra("robotName", robotName)
                    editScreenIntent.putExtra("robotDescription", robotDescription)
                    editScreenIntent.putExtra("robotHeight", robotHeight)
                    editScreenIntent.putExtra("robotId", robotId)

                    startActivity(editScreenIntent)
                }
            }
        }
    }
}
