package com.example.latihanapi

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private fun showRecords(){
        records_recycler.layoutManager = LinearLayoutManager(this@MainActivity)
        val apiInterface : ApiInterface = ApiClient().getApiClient()!!.create(ApiInterface::class.java)
        apiInterface.getData().enqueue(object : Callback<ArrayList<RecordModel>>{
            override fun onResponse(call: Call<ArrayList<RecordModel>>?, response: Response<ArrayList<RecordModel>>?) {
                val outputArray = response?.body()!!
                if(outputArray.size > 0){
                    txt_noRecords.visibility = View.GONE
                    records_recycler.visibility = View.VISIBLE
                    records_recycler.adapter = RecordAdapter(outputArray, this@MainActivity)
                }
                else{
                    txt_noRecords.visibility = View.VISIBLE
                    records_recycler.visibility = View.GONE
                }
                Toast.makeText(this@MainActivity, "Refresh", Toast.LENGTH_SHORT).show()
                Log.d("showRecord onResponse", response.toString())
            }

            override fun onFailure(call: Call<ArrayList<RecordModel>>?, t: Throwable) {
                Toast.makeText(this@MainActivity, "Data downloading is failed.", Toast.LENGTH_SHORT).show()
                Log.d("showRecord onFailure", t.message.toString())
            }
        })
    }

    private fun addRecord(){
        val name    = input_name.text.toString()
        val company_name = input_company_name.text.toString()
        val newRecord = RecordModel(0, name, company_name)

        if(name == "" || company_name == ""){
            Toast.makeText(this@MainActivity, "Name and Company Name cannot be empty.", Toast.LENGTH_SHORT).show()
        }
        else
        {
            val apiInterface : ApiInterface = ApiClient().getApiClient()!!.create(ApiInterface::class.java)
            apiInterface.setData(newRecord).enqueue(object : Callback<RecordModel>{
                override fun onResponse(call: Call<RecordModel>, response: Response<RecordModel>) {
                    if(response.isSuccessful){
                        Toast.makeText(this@MainActivity, "Added new record!", Toast.LENGTH_SHORT).show()
                        input_name.text.clear()
                        input_company_name.text.clear()
                        showRecords()
                    }
                    Log.d("addRecord onResponse", response.toString())
                }

                override fun onFailure(call: Call<RecordModel>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Uploading a new record is failed.", Toast.LENGTH_SHORT).show()
                    Log.d("addRecord onFailure", t.message.toString())
                }
            })
        }
    }

    fun updateRecord(rcd:RecordModel){
        val builder = Dialog(this, R.style.Theme_Dialog)
        builder.setCancelable(false)
        builder.setContentView(R.layout.dialog_record_edit)
        val editName = builder.findViewById<EditText>(R.id.edit_dialog_inputName)
        val editCompanyName = builder.findViewById<EditText>(R.id.edit_dialog_inputCompanyName)
        val btnCancel = builder.findViewById<TextView>(R.id.edit_dialog_btnCancel)
        val btnConfirm = builder.findViewById<TextView>(R.id.edit_dialog_btnConfirm)

        editName.setText(rcd.name)
        editCompanyName.setText(rcd.company_name)

        btnConfirm.setOnClickListener {
            val fixName = editName.text.toString()
            val fixCompanyName = editCompanyName.text.toString()

            if(fixName == "" || fixCompanyName == "")
            {
                Toast.makeText(this@MainActivity, "Name and Company Name cannot be empty.", Toast.LENGTH_SHORT).show()
            }
            else{
                val apiInterface : ApiInterface = ApiClient().getApiClient()!!.create(ApiInterface::class.java)
//                val requestCall = apiInterface.updateData(rcd.id, fixName, fixCompanyName) //pake PUT
                val requestCall = apiInterface.updateData(RecordModel(rcd.id, fixName, fixCompanyName), rcd.id!!) //pake PATCH
                requestCall.enqueue(object : Callback<RecordModel>{
                    override fun onResponse(call: Call<RecordModel>, response: Response<RecordModel>) {
                        if(response.isSuccessful){
                            Toast.makeText(this@MainActivity, "Edited record saved!", Toast.LENGTH_SHORT).show()
                            showRecords()
                        }
                        Log.d("updateRecord onResponse", response.toString())
                    }

                    override fun onFailure(call: Call<RecordModel>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Saving an edited record is failed.", Toast.LENGTH_SHORT).show()
                        Log.d("updateRecord onFailure", t.message.toString())
                    }
                })
                builder.dismiss()
            }
        }

        btnCancel.setOnClickListener{
            builder.dismiss()
        }

        builder.show()
    }

    fun deleteRecord(rcd: RecordModel){
        val builder = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
        builder.setTitle("Delete Record")
        builder.setMessage("Are you sure?")
        builder.setCancelable(false)
        builder.setIcon(R.drawable.ic_warning)

        builder.setPositiveButton("Yes"){ dialog: DialogInterface, which ->
            val apiInterface : ApiInterface = ApiClient().getApiClient()!!.create(ApiInterface::class.java)
            apiInterface.deleteData(rcd.id!!).enqueue(object : Callback<RecordModel>{
                override fun onResponse(call: Call<RecordModel>, response: Response<RecordModel>) {
                    if(response.isSuccessful){
                        Toast.makeText(this@MainActivity, "Record Deleted!", Toast.LENGTH_SHORT).show()
                        showRecords()
                    }
                    Log.d("deleteRecord onResponse", response.toString())
                }

                override fun onFailure(call: Call<RecordModel>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Deleting an existing record is failed.", Toast.LENGTH_SHORT).show()
                    Log.d("deleteRecord onFailure", t.message.toString())
                }
            })
            dialog.dismiss()
        }

        builder.setNegativeButton("No"){ dialog: DialogInterface, which ->
            dialog.dismiss()
        }

        builder.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showRecords()

        btn_add_record.setOnClickListener {
            addRecord()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.topbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.topbar_refresh -> {
                showRecords()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}