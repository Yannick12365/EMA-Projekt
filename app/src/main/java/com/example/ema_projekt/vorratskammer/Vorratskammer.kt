package com.example.ema_projekt.vorratskammer

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ema_projekt.R

class Vorratskammer : AppCompatActivity() {
    private val itemsList = ArrayList<String>()
    private lateinit var customAdapter: CustomAdapter
    private lateinit var erstellen: com.google.android.material.floatingactionbutton.FloatingActionButton;

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vorratskammer)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        customAdapter = CustomAdapter(itemsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter
        prepareItems()

        erstellen = findViewById(R.id.add)

        erstellen.setOnClickListener{
           showEventAddPopUp()
        }

    }
   private fun showEventAddPopUp(){
        val eventPopUp = Dialog(this)
        var neu: String

        eventPopUp.setContentView(R.layout.popup_vorratskammer_add)
        eventPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))



        val zurueckAdd: ImageButton = eventPopUp.findViewById(R.id.imageButton_vorratskammer_zurueck)
        val abbrechen: Button = eventPopUp.findViewById(R.id.button_vorratskammer_abbrechen)
        val hinzufuegen: Button = eventPopUp.findViewById(R.id.button_vorratskammer_hinzufuegen)
        val eventText: EditText = eventPopUp.findViewById(R.id.editText_Neu)


        zurueckAdd.setOnClickListener {
            zurueckAdd.setBackgroundResource(R.drawable.zurueckklick)
            eventPopUp.dismiss()
        }
        abbrechen.setOnClickListener {
            eventPopUp.dismiss()
        }

        hinzufuegen.setOnClickListener {
            if (eventText.text.isNotEmpty()) {
                neu = eventText.text.toString()
                itemsList.add(neu)
                customAdapter.notifyDataSetChanged()
                eventPopUp.dismiss()
            } else{
                val toast = Toast.makeText(applicationContext, "Gebe einen Text ein!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }




        eventPopUp.show()
    }
    private fun prepareItems() {
        itemsList.add("Item 1")
        itemsList.add("Item 2")
        itemsList.add("Item 3")
        itemsList.add("Item 4")
        itemsList.add("Item 5")
        itemsList.add("Item 6")
        itemsList.add("Item 7")
        itemsList.add("Item 8")
        itemsList.add("Item 9")
        itemsList.add("Item 10")
        itemsList.add("Item 11")
        itemsList.add("Item 12")
        itemsList.add("Item 13")
        customAdapter.notifyDataSetChanged()
        itemsList.remove("Item 9")
        customAdapter.notifyDataSetChanged()
    }
}