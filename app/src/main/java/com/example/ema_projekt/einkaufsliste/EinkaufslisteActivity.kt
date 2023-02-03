package com.example.ema_projekt.einkaufsliste

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import com.example.ema_projekt.R
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.*
import org.json.JSONArray

class EinkaufslisteActivity : AppCompatActivity() {
    private lateinit var zurueck:ImageButton
    private lateinit var erstellen:Button
    private lateinit var einkaufbeenden:Button
    private lateinit var einkaufItemLinearLayout:LinearLayout
    private lateinit var editText:EditText

    private val itemList = mutableMapOf<Int,View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_einkaufsliste)

        zurueck = findViewById(R.id.imageButton_einkauf_zurueck)
        erstellen = findViewById(R.id.button)
        einkaufbeenden = findViewById(R.id.button2)
        einkaufItemLinearLayout = findViewById(R.id.einkaufItemLayout)
        editText = findViewById(R.id.editText)

        //val t = EinkauflisteDataBase().readDatabase(applicationContext)

        val database: DatabaseReference = FirebaseDatabase.getInstance("https://ema-projekt-e036e-default-rtdb.europe-west1.firebasedatabase.app/").reference

        val wgName = LoginDataSettingsJSON().readLoginDataJSON(applicationContext).wgName
        database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("Einkaufsliste")) {
                    for (data in snapshot.child("Einkaufsliste").children) {
                        val id = data.key
                        val text = data.value.toString()

                        if (id != null) {
                            val viewItem = createEinkaufItem(text)
                            itemList[id.toInt()] = viewItem
                            einkaufItemLinearLayout.addView(viewItem)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Ups, da ist etwas schief gelaufen!",
                    Toast.LENGTH_SHORT).show()
            }
        })

        //createExistingItems()

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
                //EinkaufslisteJSON().writeJSON(EinkaufslisteData(id, editText.text.toString()),applicationContext)
                editText.setText("")
            } else{
                Toast.makeText(applicationContext, "Gebe einen Text ein!", Toast.LENGTH_SHORT).show()
            }
        }

        einkaufbeenden.setOnClickListener {
            var counter = 0
            for (view:View in einkaufItemLinearLayout.children) {
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
                    einkaufItemLinearLayout.removeView(view)
                    itemList.remove(id)
                    //EinkaufslisteJSON().deleteJSONItem(id, applicationContext)
                }
            }
            if (counter == 0){
                Toast.makeText(applicationContext, "Wähle zuerst die eingekauften Artikel aus, bevor du den Einkauf beendest!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createExistingItems(){
        val jsonData: JSONArray = EinkaufslisteJSON().readJSON(applicationContext)

        for (i in 0 until jsonData.length()){
            val viewItem = createEinkaufItem(jsonData.getJSONObject(i).get("itemText").toString())
            itemList[jsonData.getJSONObject(i).get("itemId").toString().toInt()] = viewItem
            einkaufItemLinearLayout.addView(viewItem)
        }
    }

    private fun createEinkaufItem(text: String):View{
        val viewItem:View = View.inflate(this, R.layout.item_einkaufsliste,null)
        val checkBox:CheckBox = viewItem.findViewById(R.id.checkBoxEinkaufItem)
        checkBox.text = text

        val button:ImageButton = viewItem.findViewById(R.id.hot_topic_loeschen)
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
            //EinkaufslisteJSON().deleteJSONItem(id, applicationContext)
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