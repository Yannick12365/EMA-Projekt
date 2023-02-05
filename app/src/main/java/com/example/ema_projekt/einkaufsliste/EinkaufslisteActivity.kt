package com.example.ema_projekt.einkaufsliste

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import androidx.core.view.size
import com.example.ema_projekt.ConnectionManager
import com.example.ema_projekt.R
import com.example.ema_projekt.vorratskammer.Vorratskammer
import com.example.ema_projekt.vorratskammer.VorratskammerData
import com.example.ema_projekt.vorratskammer.VorratskammerDatabase
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray

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

        zurueck = findViewById(R.id.imageButton_einkauf_zurueck)
        erstellen = findViewById(R.id.button)
        einkaufbeenden = findViewById(R.id.button2)
        einkaufItemLinearLayout = findViewById(R.id.einkaufItemLayout)
        editText = findViewById(R.id.editText)

        GlobalScope.launch(Dispatchers.Main) {
            val list = EinkauflisteDataBase().readDatabase(applicationContext)
            for(data in list){
                val id = data.itemId
                val text = data.itemText

                val viewItem = createEinkaufItem(text)
                itemList[id] = viewItem
                einkaufItemLinearLayout.addView(viewItem)
            }
            vorratList = VorratskammerDatabase().readDatabase(applicationContext).toMutableList()
        }

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

                EinkauflisteDataBase().writeDatabase(EinkaufslisteData(id, editText.text.toString()),applicationContext)
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

    private fun einkaufBeendenPopup(items:List<EinkaufslisteData>){
        val einkaufPopUp = Dialog(this)

        einkaufPopUp.setContentView(R.layout.popup_einkaufbeendet)
        einkaufPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val abbrechen: Button = einkaufPopUp.findViewById(R.id.button_einkaufbeenden_abbrechen)
        val bestaetigen: Button = einkaufPopUp.findViewById(R.id.button_einkaufbeenden_bestaetigen)
        val layoutEinkaufItems: LinearLayout = einkaufPopUp.findViewById(R.id.linearLayoutItems)
        val zurueck: ImageButton = einkaufPopUp.findViewById(R.id.imageButton_einkaufbeenden_zurueck)

        for (i in items){
            val viewItem:View = View.inflate(this, R.layout.item_einkaufsliste_beendet,null)
            val checkBox:CheckBox = viewItem.findViewById(R.id.checkBoxEinkaufItemBeendet)
            checkBox.text = i.itemText

            layoutEinkaufItems.addView(viewItem)
        }

        abbrechen.setOnClickListener {
            einkaufPopUp.dismiss()
        }

        bestaetigen.setOnClickListener{
            for(i in items) {
                itemList.remove(i.itemId)
                EinkauflisteDataBase().deleteDatabaseItem(i.itemId, applicationContext)
            }

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
                    VorratskammerDatabase().writeDatabase(vorratItem,applicationContext)
                    vorratList.add(vorratItem)
                }
            }

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

    private fun createEinkaufItem(text: String):View{
        val viewItem:View = View.inflate(this, R.layout.item_einkaufsliste,null)
        val checkBox:CheckBox = viewItem.findViewById(R.id.checkBoxEinkaufItem)
        checkBox.text = text

        val button:ImageButton = viewItem.findViewById(R.id.einkaufItem_loeschen)
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
            EinkauflisteDataBase().deleteDatabaseItem(id, applicationContext)
        }
        return viewItem
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
}