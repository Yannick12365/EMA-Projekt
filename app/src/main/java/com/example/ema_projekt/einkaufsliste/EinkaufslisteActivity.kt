package com.example.ema_projekt.einkaufsliste

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ema_projekt.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter

class EinkaufslisteActivity : AppCompatActivity() {
    private lateinit var zurueck:ImageButton
    private lateinit var erstellen:Button
    private lateinit var einkaufbeenden:Button
    private lateinit var layout:LinearLayout
    private lateinit var editText:EditText

    private val produktListBox = mutableMapOf<Int,CheckBox>()
    private val produktListButton = mutableMapOf<Int,Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_einkaufsliste)

        zurueck = findViewById(R.id.imageButton_einkauf_zurueck)
        erstellen = findViewById(R.id.button)
        einkaufbeenden = findViewById(R.id.button2)
        layout = findViewById(R.id.linearItemList)
        editText = findViewById(R.id.editText)

        createExistingItems()

        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }

        erstellen.setOnClickListener {
            if (editText.text.isNotEmpty()) {
                val checkbox = createCheckBox(editText.text.toString())
                val buttonLoeschen = createButtonLoeschen()

                layout.addView(checkbox, 2)
                layout.addView(buttonLoeschen, 3)

                val id = nextId()

                produktListBox.put(id,checkbox)
                produktListButton.put(id,buttonLoeschen)

                EinkaufslisteJSON().writeJSON(EinkaufslisteData(id, editText.text.toString()),applicationContext)
                editText.setText("")
            } else{
                Toast.makeText(applicationContext, "Gebe einen Text ein!", Toast.LENGTH_SHORT).show()
            }
        }

        einkaufbeenden.setOnClickListener {
            val tmpListBox = mutableListOf<CheckBox>()
            val tmpListButton = mutableListOf<Button>()

            var counter = 0

            for (id: Int in produktListBox.keys) {
                if (produktListBox[id]?.isChecked == true) {
                    counter += 1
                    val button: Button? = produktListButton[id]

                    val checkBox = produktListBox[id]
                    if (checkBox != null) {
                        tmpListBox.add(checkBox)
                    }
                    if (button != null) {
                        tmpListButton.add(button)
                    }

                    EinkaufslisteJSON().deleteJSONItem(id, applicationContext)

                    layout.removeView(checkBox)
                    layout.removeView(button)
                }
            }
            if (counter == 0) {
                Toast.makeText(applicationContext, "Wähle zuerst die eingekauften Artikel aus, bevor du den Einkauf beendest!", Toast.LENGTH_SHORT).show()
            } else {

                val ids = mutableSetOf<Int>()
                ids.addAll(produktListBox.keys)

                for (checkbox in tmpListBox) {
                    for (id in ids) {
                        if (produktListBox[id] == checkbox) {
                            produktListBox.remove(id)
                        }
                    }
                }

                ids.addAll(produktListButton.keys)
                for (button in tmpListButton) {
                    for (id in ids) {
                        if (produktListButton[id] == button) {
                            produktListButton.remove(id)
                        }
                    }
                }
            }
        }
    }

    private fun createExistingItems(){
        val jsonData:JSONArray = EinkaufslisteJSON().readJSON(applicationContext)

        for (i in 0 until jsonData.length()){
            val checkBox = createCheckBox(jsonData.getJSONObject(i).get("itemText").toString())
            val button = createButtonLoeschen()

            produktListBox.put(jsonData.getJSONObject(i).get("itemId").toString().toInt(),checkBox)
            produktListButton.put(jsonData.getJSONObject(i).get("itemId").toString().toInt(),button)

            layout.addView(checkBox, 2)
            layout.addView(button, 3)
        }
    }

    private fun createCheckBox(text:String):CheckBox{
        val checkbox = CheckBox(this)
        checkbox.id = produktListBox.size
        checkbox.textSize = 30F
        checkbox.isChecked = false
        checkbox.text = text
        checkbox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#FF6200EE"))
        checkbox.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)

        return checkbox
    }

    private fun createButtonLoeschen():Button{
        val buttonLoeschen = Button(this)
        buttonLoeschen.id = produktListButton.size
        buttonLoeschen.text = "Löschen"
        buttonLoeschen.textSize = 18F
        buttonLoeschen.setBackgroundResource(R.drawable.einkaufsliste_button_background_loeschen)
        buttonLoeschen.setTextColor(Color.WHITE)
        buttonLoeschen.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)

        val params = LinearLayout.LayoutParams(
            400,
            120
        )
        params.setMargins(0,0,0, 40)
        buttonLoeschen.layoutParams = params

        buttonLoeschen.setOnClickListener {
            var idButton = -1
            for (id in produktListButton.keys){
                if (produktListButton[id] == buttonLoeschen){
                    idButton = id
                }
            }

            val checkbox: CheckBox? = produktListBox.get(idButton)
            layout.removeView(checkbox)
            layout.removeView(buttonLoeschen)

            EinkaufslisteJSON().deleteJSONItem(idButton, applicationContext)

            produktListButton.remove(idButton)
            produktListBox.remove(idButton)
        }
        return buttonLoeschen
    }

    private fun nextId():Int{
        var newId = 0
        for (i in produktListBox.keys){
            if (i >= newId){
                newId = i+1
            }
        }
        return newId
    }
}