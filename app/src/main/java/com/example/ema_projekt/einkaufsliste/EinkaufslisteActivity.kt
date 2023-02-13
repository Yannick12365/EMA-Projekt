package com.example.ema_projekt.einkaufsliste

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import com.example.ema_projekt.ConnectionManager
import com.example.ema_projekt.R
import com.example.ema_projekt.vorratskammer.VorratskammerData
import com.example.ema_projekt.vorratskammer.VorratskammerDatabase
import com.example.ema_projekt.vorratskammer.VorratskammerJSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EinkaufslisteActivity : AppCompatActivity() {
    private lateinit var zurueck:ImageButton
    private lateinit var erstellen:Button
    private lateinit var einkaufbeenden:Button
    private lateinit var einkaufItemLinearLayout:LinearLayout
    private lateinit var editText:EditText

    private val itemList = mutableMapOf<Int,View>()
    private var vorratList = mutableListOf<VorratskammerData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_einkaufsliste)

        //Activity Felder holen
        zurueck = findViewById(R.id.imageButton_einkauf_zurueck)
        erstellen = findViewById(R.id.button_einkauf_erstellen)
        einkaufbeenden = findViewById(R.id.button_einkauf_beenden)
        einkaufItemLinearLayout = findViewById(R.id.einkaufItemLayout)
        editText = findViewById(R.id.editText_einkaufitem)

        //ConnectionManager einstellen
        val conManager = ConnectionManager()
        conManager.setOjects(false, this)
        conManager.switchScreen(this)

        //Warten auf das Auslesen der Datenbank
        var list = mutableListOf<EinkaufslisteData>()
        GlobalScope.launch(Dispatchers.Main) {
            list = EinkauflisteDataBase().readEinkaufslisteDatabase(applicationContext)
            vorratList = VorratskammerDatabase().readVorratskammerDatabase(applicationContext).toMutableList()
            EinkaufslisteJSON().writeEinkaufslisteJSON(list, applicationContext)

            for(data in list){
                val id = data.itemId
                val text = data.itemText

                val viewItem = createEinkaufItem(text)
                itemList[id] = viewItem
                einkaufItemLinearLayout.addView(viewItem)
            }
        }
        //Wenn kein Internet vorhanden Inhalt der JSON Datei nutzen
        if (!conManager.checkConnection(this)) {
            val einkaufJSONarray = EinkaufslisteJSON().readEinkaufslisteJSON(applicationContext)
            for (i in 0 until einkaufJSONarray.length()) {
                list.add(EinkaufslisteData(einkaufJSONarray.getJSONObject(i).getInt("itemId"),einkaufJSONarray.getJSONObject(i).getString("itemText")))
            }
            val vorratJSONarray = VorratskammerJSON().readVorratskammerJSON(applicationContext)
            for (i in 0 until vorratJSONarray.length()) {
                vorratList.add(VorratskammerData(vorratJSONarray.getJSONObject(i).getString("text"), vorratJSONarray.getJSONObject(i).getInt("id")))
            }
            for(data in list){
                val id = data.itemId
                val text = data.itemText

                val viewItem = createEinkaufItem(text)
                itemList[id] = viewItem
                einkaufItemLinearLayout.addView(viewItem)
            }
        }

        //Klick Eventlistener
        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }

        erstellen.setOnClickListener {
            if (editText.text.isNotEmpty()) {
                val id = nextId()
                val itemView = createEinkaufItem(editText.text.toString())

                itemList[id] = itemView
                einkaufItemLinearLayout.addView(itemView)

                EinkauflisteDataBase().writeEinkaufslisteDatabase(EinkaufslisteData(id, editText.text.toString()), applicationContext)
                EinkaufslisteJSON().addEinkaufslisteJSON(EinkaufslisteData(id, editText.text.toString()), applicationContext)

                editText.setText("")
            } else{
                Toast.makeText(applicationContext, "Gebe einen Text ein!", Toast.LENGTH_SHORT).show()
            }
        }

        einkaufbeenden.setOnClickListener {
            var counter = 0
            val itemEingekauft = mutableListOf<EinkaufslisteData>()
            for (view:View in einkaufItemLinearLayout.children.toList()) {
                val checkbox: CheckBox = view.findViewById(R.id.checkBoxEinkaufItem)
                if (checkbox.isChecked) {
                    counter += 1
                    var id: Int = -1
                    for (i in itemList.keys) {
                        if (itemList[i] == view) {
                            id = i
                            break
                        }
                    }
                    itemEingekauft.add(EinkaufslisteData(id,checkbox.text.toString()))
                }
            }
            if (counter == 0){
                Toast.makeText(applicationContext, "Wähle zuerst die eingekauften Artikel aus, bevor du den Einkauf beendest!",
                    Toast.LENGTH_SHORT).show()
            } else {
                einkaufBeendenPopup(itemEingekauft)
            }
        }
    }

    //PopUp zum Einkauf beenden
    private fun einkaufBeendenPopup(items:List<EinkaufslisteData>){
        val einkaufPopUp = Dialog(this)

        einkaufPopUp.setContentView(R.layout.popup_einkaufbeendet)
        einkaufPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //PopUp Items holen
        val abbrechen: Button = einkaufPopUp.findViewById(R.id.button_einkaufbeenden_abbrechen)
        val bestaetigen: Button = einkaufPopUp.findViewById(R.id.button_einkaufbeenden_bestaetigen)
        val layoutEinkaufItems: LinearLayout = einkaufPopUp.findViewById(R.id.linearLayoutItems)
        val zurueck: ImageButton = einkaufPopUp.findViewById(R.id.imageButton_einkaufbeenden_zurueck)

        //Einkauf Item erstellen
        for (i in items){
            val viewItem:View = View.inflate(this, R.layout.item_einkaufsliste_beendet,null)
            val checkBox:CheckBox = viewItem.findViewById(R.id.checkBoxEinkaufItemBeendet)
            checkBox.text = i.itemText

            layoutEinkaufItems.addView(viewItem)
        }

        //Klick Eventlistener
        abbrechen.setOnClickListener {
            einkaufPopUp.dismiss()
        }

        bestaetigen.setOnClickListener{
            for(i in items) {
                itemList.remove(i.itemId)
                EinkauflisteDataBase().deleteEinkaufslisteDatabaseItem(i.itemId, applicationContext)
                EinkaufslisteJSON().deleteEinkaufslisteJSONItem(i.itemId, applicationContext)
            }

            //Ausgewaehlte Items in Vorratskammer einfuegen
            for(view in layoutEinkaufItems.children.toList()){
                val checkbox: CheckBox = view.findViewById(R.id.checkBoxEinkaufItemBeendet)
                if (checkbox.isChecked) {

                    var newId = 0
                    for (i in vorratList){
                        if (i.id >= newId){
                            newId = i.id+1
                        }
                    }
                    val vorratItem = VorratskammerData(checkbox.text.toString(), newId)
                    VorratskammerDatabase().writeVorratskammerDatabase(vorratItem, applicationContext)
                    VorratskammerJSON().addVorratskammerJSON(vorratItem, applicationContext)

                    vorratList.add(vorratItem)
                }
            }

            //Ausgewaehlte Items aus Einkaufsliste entfernen
            for (view:View in einkaufItemLinearLayout.children.toList()) {
                for(view2:View in layoutEinkaufItems.children.toList()){
                    val checkbox: CheckBox = view.findViewById(R.id.checkBoxEinkaufItem)
                    val checkbox2: CheckBox = view2.findViewById(R.id.checkBoxEinkaufItemBeendet)

                    if (checkbox.text.toString() == checkbox2.text.toString()){
                        einkaufItemLinearLayout.removeView(view)
                    }
                }
            }
            einkaufPopUp.dismiss()
        }

        zurueck.setOnClickListener {
            einkaufPopUp.dismiss()
        }

        einkaufPopUp.show()
    }

    //Einkaufitem erstellen
    private fun createEinkaufItem(text: String):View{
        //Item Items holen
        val viewItem:View = View.inflate(this, R.layout.item_einkaufsliste,null)
        val checkBox:CheckBox = viewItem.findViewById(R.id.checkBoxEinkaufItem)
        checkBox.text = text
        val button:ImageButton = viewItem.findViewById(R.id.einkaufItem_loeschen)

        //Klick Eventlistener
        button.setOnClickListener {
            var id: Int = -1
            for (i in itemList.keys) {
                if (itemList[i] == viewItem) {
                    id = i
                    break
                }
            }
            einkaufItemLinearLayout.removeView(viewItem)
            itemList.remove(id)
            EinkauflisteDataBase().deleteEinkaufslisteDatabaseItem(id, applicationContext)
            EinkaufslisteJSON().deleteEinkaufslisteJSONItem(id, applicationContext)
        }
        return viewItem
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
}