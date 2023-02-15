package com.example.ema_projekt.vorratskammer


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.example.ema_projekt.ConnectionManager
import com.example.ema_projekt.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VorratskammerActivity : AppCompatActivity() {

    private lateinit var erstellen: FloatingActionButton
    private lateinit var layout: LinearLayout
    private lateinit var zurueck: ImageButton

    private val itemList = mutableListOf<Int>()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vorratskammer)

        //Activity Felder holen
        zurueck = findViewById(R.id.imageButton_vorratskammer_zurueck)
        layout = findViewById(R.id.vorratskammerItemLayout)
        erstellen = findViewById(R.id.add_vorratskammer_new_item)

        //ConnectionManager einstellen
        val conManager = ConnectionManager()
        conManager.setOjects(false, this)
        conManager.switchScreen(this)

        //Warten auf das Auslesen der Datenbank
        var list = mutableListOf<VorratskammerData>()
        GlobalScope.launch(Dispatchers.Main) {
            list = VorratskammerDatabase().readVorratskammerDatabase(applicationContext)
            VorratskammerJSON().writeVorratskammerJSON(list, applicationContext)

            for (data in list) {
                createEinkaufItem(data.text, data.id)
            }
        }
        //Wenn kein Internet vorhanden Inhalt der JSON Datei nutzen
        if (!conManager.checkConnection(this)) {
            val vorratJSONarray = VorratskammerJSON().readVorratskammerJSON(applicationContext)
            for (i in 0 until vorratJSONarray.length()) {
                list.add(
                    VorratskammerData(
                        vorratJSONarray.getJSONObject(i).getString("text"),
                        vorratJSONarray.getJSONObject(i).getInt("id"),
                    )
                )
            }
            for (data in list) {
                createEinkaufItem(data.text, data.id)
            }
        }

        //Klick Eventlistener
        erstellen.setOnClickListener {
            showEventAddPopUp()
        }

        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }

    }

    //PopUp zum Vorratskammer Item erstellen
    private fun showEventAddPopUp() {
        val eventPopUp = Dialog(this)

        eventPopUp.setContentView(R.layout.popup_vorratskammer_add)
        eventPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //PopUp Items holen
        val abbrechen: Button = eventPopUp.findViewById(R.id.button_vorratskammer_abbrechen)
        val hinzufuegen: Button = eventPopUp.findViewById(R.id.button_vorratskammer_hinzufuegen)
        val eventText: EditText = eventPopUp.findViewById(R.id.editText_Neuer_Eintrag_Vorratskammer)
        val popupzurueck: ImageButton = eventPopUp.findViewById(R.id.imageButton_vorratskammer_eintrag_zurueck)

        //Klick Eventlistener
        popupzurueck.setOnClickListener {
            eventPopUp.dismiss()
        }

        abbrechen.setOnClickListener {
            eventPopUp.dismiss()
        }

        hinzufuegen.setOnClickListener {
            if (eventText.text.isNotEmpty()) {
                createEinkaufItem(eventText.text.toString(), nextId())
                VorratskammerJSON().addVorratskammerJSON(
                    VorratskammerData(
                        eventText.text.toString(),
                        nextId()
                    ), applicationContext
                )

                eventPopUp.dismiss()
            } else {
                val toast =
                    Toast.makeText(applicationContext, "Gebe zuerst etwas ein!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        eventPopUp.show()
    }

    //Funktion zum Item erstellen
    private fun createEinkaufItem(text: String, id: Int) {
        //Item Items holen
        val viewItem = View.inflate(this, R.layout.item_vorratskammer, null)
        val textView: TextView = viewItem.findViewById(R.id.itemTextView)
        textView.text = text
        val button: ImageButton = viewItem.findViewById(R.id.button_loeschen_vorratskammer)

        val vorratItem = VorratskammerData(text, id)

        //Klick Eventlistener
        button.setOnClickListener {
            VorratskammerDatabase().deleteVorratskammerDatabaseItem(
                vorratItem.id,
                applicationContext
            )
            VorratskammerJSON().deleteJSONVorratskammerItem(vorratItem.id, applicationContext)
            layout.removeView(viewItem)
            itemList.remove(vorratItem.id)
        }
        layout.addView(viewItem)
        VorratskammerDatabase().writeVorratskammerDatabase(vorratItem, applicationContext)
        itemList.add(vorratItem.id)
    }

    //ID für Item bestimmen
    private fun nextId(): Int {
        var newId = 0
        for (i in itemList) {
            if (i >= newId) {
                newId = i + 1
            }
        }
        return newId
    }
}
