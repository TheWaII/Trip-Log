package com.example.protokoll

import PdfGenerator
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val myCalendar: Calendar = Calendar.getInstance()
    private var editText: EditText? = null

    private var begleiterSignature: Bitmap? = null
    private var bewerberSignature: Bitmap? = null

    private var proList = ArrayList<LogItem>()
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: LogItemAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.dateValue)
        recyclerView = findViewById(R.id.recyclerView)

        loadData()
        populateList()
        populateSpinner()
        datePicker()

        adapter = LogItemAdapter(proList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

    }

    fun add(v: View) {

        val date: EditText = findViewById(R.id.dateValue)
        val kmInit: EditText = findViewById(R.id.kmInitialValue)
        val kmEnd: EditText = findViewById(R.id.kmEndValue)
        val kfz: EditText = findViewById(R.id.kfzValue)
        val timeOfDay: Spinner = findViewById(R.id.timeOfDayValue)
        val route: EditText = findViewById(R.id.routeValue)
        val condition: EditText = findViewById(R.id.conditionValue)

        val newLogItem = LogItem()

        if (date.text.toString().isEmpty() || kmInit.text.toString()
                .isEmpty() || kmEnd.text.toString().isEmpty() ||
            kfz.text.isEmpty() || timeOfDay.toString()
                .isEmpty() || route.text.isEmpty() || condition.text.isEmpty()
        ) {
            Toast.makeText(applicationContext, "Bitte alles ausfüllen!", Toast.LENGTH_SHORT).show()
        } else {
            newLogItem.date = date.text.toString()
            newLogItem.kmInit = kmInit.text.toString().toInt()
            newLogItem.kmEnd = kmEnd.text.toString().toInt()
            newLogItem.kfz = kfz.text.toString()
            newLogItem.timeOfDay = timeOfDay.selectedItem.toString()
            newLogItem.route = route.text.toString()
            newLogItem.condition = condition.text.toString()

            proList.add(newLogItem)

            kfz.text.clear()
            kmInit.text.clear()
            kmEnd.text.clear()
            route.text.clear()
            condition.text.clear()

            saveData()
            populateList()
        }

    }


    private fun populateList() {
        val proStringList = ArrayList<String>()
        for (logItem in proList) {
            proStringList.add(
                "${logItem.date} -- ${logItem.kmInit} km -- ${logItem.kmEnd} km -- ${logItem.kfz} -- ${logItem.timeOfDay} -- ${logItem.route} -- ${logItem.condition}"
            )
        }

        val adapter = LogItemAdapter(proList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    proList.removeAt(position)
                    saveData()
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, proList.size)
                    Toast.makeText(
                        applicationContext, "gelöscht", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    private fun populateSpinner() {
        val spinner: Spinner = findViewById(R.id.timeOfDayValue)
        ArrayAdapter.createFromResource(
            this, R.array.timeOfDay, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun datePicker() {
        val date = OnDateSetListener { _, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)
            updateLabel()
        }
        editText?.setOnClickListener {
            DatePickerDialog(
                this@MainActivity,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        updateLabel()
    }

    private fun updateLabel() {
        val dateFormat = SimpleDateFormat(DD_MM_YYYY, Locale.GERMAN)
        editText?.setText(dateFormat.format(myCalendar.time))
    }

    private fun saveData() {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(proList)
        editor.putString(TASK_LIST, json)
        editor.apply()
        println(Environment.getDataDirectory())
    }

    private fun loadData() {

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(TASK_LIST, gson.toJson(proList))

        val type = object : TypeToken<ArrayList<LogItem>>() {}.type

        proList = gson.fromJson(json, type)
    }


    private fun showSignatureDialog(
        title: String,
        listener: SignaturePad.OnSignedListener,
        onSaveCallback: (signatureBitmap: Bitmap) -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.signature_popup, null)
        val mSignaturePad: SignaturePad = dialogView.findViewById(R.id.signature_pad)

        mSignaturePad.setOnSignedListener(listener)

        val alertDialog =
            AlertDialog.Builder(this).setView(dialogView).setPositiveButton("Save") { _, _ ->
                val signatureBitmap = mSignaturePad.signatureBitmap
                onSaveCallback(signatureBitmap)
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.setTitle(title).create()

        alertDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                if (begleiterSignature == null || bewerberSignature == null) {
                    if (begleiterSignature == null) {
                        Toast.makeText(
                            this, "Begleiter signature is missing!", Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (bewerberSignature == null) {
                        Toast.makeText(
                            this, "Bewerber signature is missing!", Toast.LENGTH_SHORT
                        ).show()
                    }
                    false
                } else {
                    PdfGenerator.generatePdf(this, proList, begleiterSignature, bewerberSignature)
                    true
                }
            }

            R.id.menu_signature_begleiter -> {
                showSignatureDialog("Signature Begleiter", object : SignaturePad.OnSignedListener {
                    override fun onStartSigning() {}
                    override fun onSigned() {}
                    override fun onClear() {}
                }) { signatureBitmap ->
                    begleiterSignature = signatureBitmap
                }
                true
            }

            R.id.menu_signature_bewerber -> {
                showSignatureDialog("Signature Bewerber", object : SignaturePad.OnSignedListener {
                    override fun onStartSigning() {}
                    override fun onSigned() {}
                    override fun onClear() {}
                }) { signatureBitmap ->
                    bewerberSignature = signatureBitmap
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}