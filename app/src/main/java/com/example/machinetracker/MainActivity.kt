package com.example.machinetracker

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    internal var myDb: RobotDatabase? = null
    internal lateinit var aweImageView: ImageView

    internal var editRobotName: EditText? = null
    internal var editDescription: EditText? = null
    internal var editHeight: EditText? = null
    internal var editSearchId: EditText? = null

    private var selectedName: String? = null
    private var selectedDescription: String? = null
    private var selectedHeight: String? = null
    private var selectedId: String? = null

    internal var btnAddData: Button? = null
    internal var btnViewAll: Button? = null
    internal var btnDelete: Button? = null
    internal var btnViewUpdate: Button? = null
    internal var btnList: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myDb = RobotDatabase(this)

        editRobotName = findViewById(R.id.robot_name) as EditText
        editDescription = findViewById(R.id.description) as EditText
        editHeight = findViewById(R.id.height) as EditText
        editSearchId = findViewById(R.id.search_id) as EditText
        btnAddData = findViewById(R.id.button_add) as Button
        btnViewAll = findViewById(R.id.button_viewAll) as Button
        btnViewUpdate = findViewById(R.id.button_update) as Button
        btnDelete = findViewById(R.id.button_delete) as Button
        btnList = findViewById(R.id.button_list) as Button

        //creates the receive intent to get "extra" from the listActivity
        val receivedIntent = intent

        val aweImageButton = findViewById(R.id.imageButton) as Button
        aweImageView = findViewById(R.id.theImageView) as ImageView

        //Disable the button if the device has no built-in camera
        if (!hasCamera())
            aweImageButton.isEnabled = false

        //now get the attributes that we passed as an extra from the listActivity
        selectedName = receivedIntent.getStringExtra("robotName")
        selectedDescription = receivedIntent.getStringExtra("robotDescription")
        selectedHeight = receivedIntent.getStringExtra("robotHeight")
        selectedId = receivedIntent.getStringExtra("robotId")

        //set the text to show the current selected attributes in the editText XML
        robot_name!!.setText(selectedName)
        description!!.setText(selectedDescription)
        height!!.setText(selectedHeight)
        search_id!!.setText(selectedId)

        // Allow the functionality of different methods by onClick
        AddData()
        viewAll()
        UpdateData()
        DeleteData()
        ListData()
    }

    //opens dataList.listActivity
    fun ListData() {
        btnList!!.setOnClickListener {
            val intent = Intent(this@MainActivity, RobotList::class.java)
            startActivity(intent)
        }
    }

    fun DeleteData() {
        btnDelete!!.setOnClickListener {
            val deletedRows = myDb!!.deleteData(editSearchId!!.text.toString())
            if (deletedRows!! > 0)
                Toast.makeText(this@MainActivity, "Data Deleted", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this@MainActivity, "Data not Deleted", Toast.LENGTH_LONG).show()
        }
    }

    fun UpdateData() {
        btnViewUpdate!!.setOnClickListener {
            val isUpdate = myDb!!.updateData(
                    editSearchId!!.text.toString(),
                    editRobotName!!.text.toString(),
                    editDescription!!.text.toString(),
                    editHeight!!.text.toString()
            )
            if (isUpdate == true)
                Toast.makeText(this@MainActivity, "Data Update", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this@MainActivity, "Data not Updated", Toast.LENGTH_LONG).show()
        }
    }

    fun AddData() {
        btnAddData!!.setOnClickListener {
            val isInserted = myDb!!.insertData(
                    editRobotName!!.text.toString(),
                    editDescription!!.text.toString(),
                    editHeight!!.text.toString()
            )
            if (isInserted == true)
                Toast.makeText(this@MainActivity, "Data Inserted", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this@MainActivity, "Data not Inserted", Toast.LENGTH_LONG).show()
        }
    }

    fun viewAll() {
        btnViewAll!!.setOnClickListener(
                View.OnClickListener {
                    val res = myDb!!.allData
                    if (res.getCount() == 0) {
                        // show message
                        showMessage("Error", "Nothing found")
                        return@OnClickListener
                    }

                    val buffer = StringBuffer()
                    while (res.moveToNext()) {
                        buffer.append("Special ID :    " + res.getString(0) + "\n")
                        buffer.append("Robot ID :       " + res.getString(1) + "\n")
                        buffer.append("Description :  " + res.getString(2) + "\n")
                        buffer.append("Height :           " + res.getString(3) + "\n\n")
                    }

                    // Show all data
                    showMessage("Data", buffer.toString())
                }
        )
    }

    fun showMessage(title: String, Message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(Message)
        builder.show()
    }
    //Checks if the device has a camera
    private fun hasCamera(): Boolean {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    //Launches the camera
    fun launchCamera(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Take a picture and pass results along to onActivityResult
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    //To return the photo taken
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Get the photo
            val extras = data?.extras
            val photo = extras!!.get("data") as Bitmap
            aweImageView.setImageBitmap(photo)
        }
    }

    //Mere object for camera function
    companion object {
        internal val REQUEST_IMAGE_CAPTURE = 1
    }
}