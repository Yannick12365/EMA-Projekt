package com.example.ema_projekt.einkaufsliste

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import com.example.ema_projekt.R
import org.json.JSONArray

class EinkaufslisteActivity : AppCompatActivity() {
    private lateinit var zurueck:ImageButton
    private lateinit var erstellen:Button
    private lateinit var einkaufbeenden:Button
    private lateinit var layout:LinearLayout
    private lateinit var editText:EditText

    private val itemList = mutableMapOf<Int,View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_einkaufsliste)

        zurueck = findViewById(R.id.imageButton_einkauf_zurueck)
        erstellen = findViewById(R.id.button)
        einkaufbeenden = findViewById(R.id.button2)
        layout = findViewById(R.id.einkaufItemLayout)
        editText = findViewById(R.id.editText)

        createExistingItems()

        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }

        erstellen.setOnClickListener {
            if (editText.text.isNotEmpty()) {
                val id = nextId()
                val itemView = createEinkaufItem(editText.text.toString())

                itemList[id] = itemView
                layout.addView(itemView)

                EinkaufslisteJSON().writeJSON(EinkaufslisteData(id, editText.text.toString()),applicationContext)
                editText.setText("")
            } else{
                Toast.makeText(applicationContext, "Gebe einen Text ein!", Toast.LENGTH_SHORT).show()
            }
        }

        einkaufbeenden.setOnClickListener {
            var counter = 0
            for (view:View in layout.children) {
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
                    layout.removeView(view)
                    itemList.remove(id)
                    EinkaufslisteJSON().deleteJSONItem(id, applicationContext)
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
            layout.addView(viewItem)
        }
    }

    private fun createEinkaufItem(text: String):View{
        val viewItem = View.inflate(this, R.layout.item_einkaufsliste,null)
        val checkBox:CheckBox = viewItem.findViewById(R.id.checkBoxEinkaufItem)
        checkBox.text = text

        val button:ImageButton = viewItem.findViewById(R.id.buttonEinkaufItemLoeschen)
        button.setOnClickListener {
            for (view:View in layout.children){
                val btnLoeschen:ImageButton = view.findViewById(R.id.buttonEinkaufItemLoeschen)
                if (btnLoeschen == button){
                    var id:Int = -1
                    for (i in itemList.keys){
                        if (itemList[i] == view){
                            id = i
                            break
                        }
                    }
                    layout.removeView(view)
                    itemList.remove(id)
                    EinkaufslisteJSON().deleteJSONItem(id, applicationContext)
                }
            }
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