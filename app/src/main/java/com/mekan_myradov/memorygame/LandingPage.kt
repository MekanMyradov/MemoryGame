package com.mekan_myradov.memorygame

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.mekan_myradov.memorygame.adapters.ItemAdapter
import kotlinx.android.synthetic.main.activity_landing_page.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LandingPage : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        val intent = getIntent()
        if(intent != null){
            val current = LocalDateTime.now()
            val date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = current.format(date_formatter)

            val time_formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val time = current.format(time_formatter)
            var score = intent.getIntExtra("Score", 0)
            var level = intent.getIntExtra("Level", 0)
            if(level != 0) {
                addRecord(date.toString(), time.toString(), score, level)
            }
        }

        setUpListOfDataIntoRecyclerView()

    /*
        // Set the layout manager so the Recycler view can use
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize new adapter
        val itemAdapter = ItemAdapter(this, getItemsList())

        // Initialize Recycler View's adapter
        recyclerView.adapter = itemAdapter
    */
    }

    private fun addRecord(date: String, time: String, score: Int, level: Int) {
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val status = databaseHandler.addRecord(ModelClass(date, time, score, level))
        if(status > -1){
            setUpListOfDataIntoRecyclerView()
        }
    }

    private fun setUpListOfDataIntoRecyclerView() {
        if(getItemsList().size > 0){
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(this)
            val itemAdapter = ItemAdapter(this, getItemsList())
            recyclerView.adapter = itemAdapter
        }
        else{
            recyclerView.visibility = View.GONE
        }
    }

    fun clickLevel01(view: View){
        val intent = Intent(this, Level01::class.java)
        startActivity(intent)
    }

    fun clickLevel02(view: View){
        val intent = Intent(this, Level02::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_about){
            showInfo()
        }
        return true
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    private fun getItemsList(): ArrayList<ModelClass>{
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        return databaseHandler.readRecord()
    }
}