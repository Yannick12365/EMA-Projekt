package com.example.ema_projekt.putzplan

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_putzplan)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Activity Felder holen
        zurueck = findViewById(R.id.imageButton_putzplan_zurueck)
        layout = findViewById(R.id.putzplan_layout)
        erstellen = findViewById(R.id.putzplan_add)

        //ConnectionManager einstellen
        val conManager = ConnectionManager()
        conManager.setOjects(false, this)
        conManager.switchScreen(this)

        //Warten auf das Auslesen der Datenbank
        var list = mutableListOf<PutzPlanData>()
        GlobalScope.launch(Dispatchers.Main) {
            list = PutzPlanDatabase().readPutzPlanDatabase(applicationContext)
            PutzPlanJSON().writePutzPlanJSON(list,applicationContext)

            for (data in list) {
                dataList.add(data)
                createPutzPlanEintrag(data)
            }
        }

        //Wenn kein Internet vorhanden Inhalt der JSON Datei nutzen
        if (!conManager.checkConnection(this)){
            val putzJSONarray = PutzPlanJSON().readPutzPlanJSON(applicationContext)
            for (i in 0 until putzJSONarray.length()) {
                list.add(PutzPlanData(
                    putzJSONarray.getJSONObject(i).getInt("id"),
                    putzJSONarray.getJSONObject(i).getString("person"),
                    putzJSONarray.getJSONObject(i).getString("aufgabe"),
                    putzJSONarray.getJSONObject(i).getString("zeitInterval"))
                )
            }
            for (data in list) {
                dataList.add(data)
                createPutzPlanEintrag(data)
            }
        }

        //Klick Eventlistener
        erstellen.setOnClickListener {
            showEventAddPopUpPerson()
        }

        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }
    }

    //PutzPlan Item erstellen PopUp
    private fun showEventAddPopUpPerson() {
        val eventPopUpPerson1 = Dialog(this)

        eventPopUpPerson1.setContentView(R.layout.popup_putzplan_neue_person)
        eventPopUpPerson1.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //PopUp Items holen
        val abbrechen: Button = eventPopUpPerson1.findViewById(R.id.button_putzplan_person_abbrechen)
        val hinzufuegen: Button = eventPopUpPerson1.findViewById(R.id.button_putzplan_person_hinzufügen)
        val eventText: EditText = eventPopUpPerson1.findViewById(R.id.editText_Neuer_Eintrag_Putzplan_Add_Person)
        val popupzureuck: ImageButton = eventPopUpPerson1.findViewById(R.id.imageButton_putzplanperson_zurueck)

        //Klick Eventlistener
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
                PutzPlanDatabase().writePutzPlanDatabase(data,applicationContext)
                PutzPlanJSON().addPutzPlanJSON(data,applicationContext)

                eventPopUpPerson1.dismiss()
            } else {
                val toast =
                    Toast.makeText(applicationContext, "Gebe zuerst etwas ein!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        eventPopUpPerson1.show()
    }

    //PutzPlan Item erstellen
    private fun createPutzPlanEintrag(data:PutzPlanData){
        val viewItem = View.inflate(this, R.layout.item_putzplan, null)
        //Item Items holen
        val textViewPerson: TextView = viewItem.findViewById(R.id.putzplan_person)

        textViewPerson.text = data.person

        val textViewAufgabe: TextView = viewItem.findViewById(R.id.putzplan_aufgabe)
        textViewAufgabe.text = data.aufgabe

        //----------------------------------------------------------
        //from https://developer.android.com/develop/ui/views/components/spinner
        //Spinner Code aus dem Internet siehe Link
        val spinner: Spinner = viewItem.findViewById(R.id.putzplan_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.putzplan_spinner_strings,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.setSelection(adapter.getPosition(data.zeitInterval))
        }
        //----------------------------------------------------------

        //Event um Spinner Auswahlaenderungen abzufangen
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val interval: String = parent?.getItemAtPosition(position).toString()
                val dataNew = getDataById(data.id)
                dataNew.zeitInterval = interval
                PutzPlanDatabase().editPutzPlanDatabaseZeitInterval(dataNew,applicationContext)
                PutzPlanJSON().editZeitIntervalPutzPlanJSONItem(dataNew.id,applicationContext,dataNew.zeitInterval)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //Klick Eventlistener
        textViewAufgabe.setOnClickListener {
            showEventAddPopUpAufgabe(textViewAufgabe,data.id)
        }

        viewItem.setOnClickListener{
            showEventAddPopUpLoeschen(viewItem,data.id)
        }

        itemList[data.id] = viewItem
        layout.addView(viewItem)
    }

    //PopUp zum PutzPlan Item loeschen
    private fun showEventAddPopUpLoeschen(item: View, id:Int) {
        val popUpLoeschen = Dialog(this)

        popUpLoeschen.setContentView(R.layout.popup_putzplan_person_loeschen)
        popUpLoeschen.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //PopUp Items holen
        val nein: Button = popUpLoeschen.findViewById(R.id.button_putzplan_loeschen_nein)
        val ja: Button = popUpLoeschen.findViewById(R.id.button_putzplan_loeschen_ja)
        val popupzurucke: ImageButton = popUpLoeschen.findViewById(R.id.imageButton_putzplan_loeschen_zurueck)

        //Klick Eventlistener
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
            PutzPlanDatabase().deletePutzPlanDatabaseItem(id,applicationContext)
            PutzPlanJSON().deletePutzPlanJSONItem(id,applicationContext)
        }

        popUpLoeschen.show()
    }

    //PopUp zum Aufgabe aendern
    private fun showEventAddPopUpAufgabe(view: TextView, id:Int){
        val popUpAufgabe = Dialog(this)

        popUpAufgabe.setContentView(R.layout.popup_putzplan_neue_aufgabe)
        popUpAufgabe.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //PopUp Items holen
        val abbrechen: Button = popUpAufgabe.findViewById(R.id.button_putzplan_abbrechen)
        val aufgabeÄndern: Button = popUpAufgabe.findViewById(R.id.button_putzplan_ändern)
        val eventText: EditText = popUpAufgabe.findViewById(R.id.editText_Neue_Aufgabe_Putzplan)
        val popupzurueck: ImageButton = popUpAufgabe.findViewById(R.id.imageButton_putzplanaufgabe_neu_zurueck)

        //Klick Eventlistener
        abbrechen.setOnClickListener {
            popUpAufgabe.dismiss()
        }

        popupzurueck.setOnClickListener {
            popUpAufgabe.dismiss()
        }

        aufgabeÄndern.setOnClickListener {
            if (eventText.text.isNotEmpty()) {
                view.text = eventText.text
                val data = getDataById(id)
                data.aufgabe = eventText.text.toString()
                PutzPlanDatabase().editPutzPlanDatabaseAufgabe(data,applicationContext)
                PutzPlanJSON().editAufgabePutzPlanJSONItem(data.id,applicationContext,data.aufgabe)

                popUpAufgabe.dismiss()
            } else {
                val toast =
                    Toast.makeText(applicationContext, "Gebe zuerst etwas ein!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        popUpAufgabe.show()
    }

    //ID für Item bestimmen
    private fun nextId():Int{
        var newId = 0
        for (i in itemList.keys){
            if (i >= newId){
                newId = i+1
            }
        }
        return newId
    }

    //PutzPlaneintrag ueber ID bekommen
    private fun getDataById(id:Int):PutzPlanData{
        for(i in dataList){
            if (id == i.id){
                return i
            }
        }
        return PutzPlanData(-1,"Person","Aufgabe","Montags")
    }
}