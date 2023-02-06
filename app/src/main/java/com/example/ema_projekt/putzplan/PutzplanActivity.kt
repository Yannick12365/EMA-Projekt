package com.example.ema_projekt.putzplan

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ema_projekt.ConnectionManager
import com.example.ema_projekt.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PutzplanActivity : AppCompatActivity() {
    private lateinit var erstellen: com.google.android.material.floatingactionbutton.FloatingActionButton;
    private lateinit var layout: LinearLayout
    private lateinit var zurueck:ImageButton

    private var itemList = mutableMapOf<Int,View>()
    private var dataList = mutableListOf<PutzPlanData>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_putzplan)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_putzplan)

        zurueck = findViewById(R.id.imageButton_putzplan_zurueck)
        layout = findViewById(R.id.putzplan_layout)
        erstellen = findViewById(R.id.putzplan_add)

        val conManager = ConnectionManager()
        conManager.setOjects(this, false, findViewById(R.id.textViewInternetError))

        GlobalScope.launch(Dispatchers.Main) {
            val list = PutzPlanDatabase().readDatabase(applicationContext)
            for (data in list) {
                dataList.add(data)
                createPutzPlanEintrag(data)
            }
        }

        erstellen.setOnClickListener {
            showEventAddPopUpPerson()
        }

        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }
    }

    private fun showEventAddPopUpPerson() {
        val eventPopUpPerson1 = Dialog(this)

        eventPopUpPerson1.setContentView(R.layout.popup_putzplan_neue_person)
        eventPopUpPerson1.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val abbrechen: Button = eventPopUpPerson1.findViewById(R.id.button_vorratskammer_abbrechen)
        val hinzufuegen: Button = eventPopUpPerson1.findViewById(R.id.button_vorratskammer_hinzufuegen)
        val eventText: EditText = eventPopUpPerson1.findViewById(R.id.editText_Neuer_Eintrag)
        val popupzureuck: ImageButton = eventPopUpPerson1.findViewById(R.id.imageButton_putzplanperson_zurueck)


        abbrechen.setOnClickListener {
            eventPopUpPerson1.dismiss()
        }

        popupzureuck.setOnClickListener {
            eventPopUpPerson1.dismiss()
        }

        hinzufuegen.setOnClickListener {
            if (eventText.text.isNotEmpty()) {
                val id = nextId()

                val data = PutzPlanData(id,eventText.text.toString(),"Aufgabe","Montags")
                dataList.add(data)
                createPutzPlanEintrag(data)
                PutzPlanDatabase().writeDatabase(data,applicationContext)

                eventPopUpPerson1.dismiss()
            } else {
                val toast =
                    Toast.makeText(applicationContext, "Gebe zuerst etwas ein!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        eventPopUpPerson1.show()
    }

    private fun createPutzPlanEintrag(data:PutzPlanData){
        val viewItem = View.inflate(this, R.layout.item_putzplan, null)
        val textViewPerson: TextView = viewItem.findViewById(R.id.putzplan_person)

        textViewPerson.text = data.person

        val textViewAufgabe: TextView = viewItem.findViewById(R.id.putzplan_aufgabe)
        textViewAufgabe.text = data.aufgabe

        val spinner: Spinner = viewItem.findViewById(R.id.putzplan_spinner)      //from https://developer.android.com/develop/ui/views/components/spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.putzplan_spinner_strings,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            spinner.setSelection(adapter.getPosition(data.zeitInterval))
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val interval: String = parent?.getItemAtPosition(position).toString()
                val dataNew = getDataById(data.id)
                dataNew.zeitInterval = interval
                PutzPlanDatabase().editDatabaseZeitInterval(dataNew,applicationContext)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        textViewAufgabe.setOnClickListener {
            showEventAddPopUpAufgabe(textViewAufgabe,data.id)
        }

        viewItem.setOnClickListener{
            showEventAddPopUpLoeschen(viewItem,data.id)
        }

        itemList[data.id] = viewItem
        layout.addView(viewItem)
    }

    private fun showEventAddPopUpLoeschen(item: View, id:Int) {

        val popUpLoeschen = Dialog(this)

        popUpLoeschen.setContentView(R.layout.popup_putzplan_person_loeschen)
        popUpLoeschen.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val nein: Button = popUpLoeschen.findViewById(R.id.button_putzplan_loeschen_nein)
        val ja: Button = popUpLoeschen.findViewById(R.id.button_putzplan_loeschen_ja)
        val popupzurucke: ImageButton = popUpLoeschen.findViewById(R.id.imageButton_putzplan_loeschen_zurueck)

        popupzurucke.setOnClickListener {
            popUpLoeschen.dismiss()
        }

        nein.setOnClickListener {
            popUpLoeschen.dismiss()
        }

        ja.setOnClickListener{
            popUpLoeschen.dismiss()
            layout.removeView(item)
            itemList.remove(id)
            dataList.remove(getDataById(id))
            PutzPlanDatabase().deleteDatabaseItem(id,applicationContext)
        }

        popUpLoeschen.show()
    }

    private fun showEventAddPopUpAufgabe(view: TextView, id:Int){
        val popUpAufgabe = Dialog(this)

        popUpAufgabe.setContentView(R.layout.popup_putzplan_neue_aufgabe)
        popUpAufgabe.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val abbrechen: Button = popUpAufgabe.findViewById(R.id.button_vorratskammer_abbrechen)
        val hinzufuegen: Button = popUpAufgabe.findViewById(R.id.button_vorratskammer_hinzufuegen)
        val eventText: EditText = popUpAufgabe.findViewById(R.id.editText_Neuer_Eintrag)
        val popupzurueck: ImageButton = popUpAufgabe.findViewById(R.id.imageButton_putzplanaufgabe_neu_zurueck)

        abbrechen.setOnClickListener {
            popUpAufgabe.dismiss()
        }

        popupzurueck.setOnClickListener {
            popUpAufgabe.dismiss()
        }

        hinzufuegen.setOnClickListener {
            if (eventText.text.isNotEmpty()) {
                view.text = eventText.text
                val data = getDataById(id)
                data.aufgabe = eventText.text.toString()
                PutzPlanDatabase().editDatabaseAufgabe(data,applicationContext)

                popUpAufgabe.dismiss()
            } else {
                val toast =
                    Toast.makeText(applicationContext, "Gebe zuerst etwas ein!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        popUpAufgabe.show()
    }


    private fun nextId():Int{
        var newId = 0
        for (i in itemList.keys){
            if (i >= newId){
                newId = i+1
            }
        }
        return newId
    }

    private fun getDataById(id:Int):PutzPlanData{
        for(i in dataList){
            if (id == i.id){
                return i
            }
        }
        return PutzPlanData(-1,"Person","Aufgabe","Montags")
    }
}