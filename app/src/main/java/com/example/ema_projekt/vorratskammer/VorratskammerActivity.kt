package com.example.ema_projekt.vorratskammer


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.example.ema_projekt.ConnectionManager
import com.example.ema_projekt.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VorratskammerActivity : AppCompatActivity() {

    private lateinit var erstellen: FloatingActionButton
    private lateinit var layout:LinearLayout
    private lateinit var zurueck:ImageButton

    private val itemList = mutableListOf<Int>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vorratskammer)

        zurueck = findViewById(R.id.imageButton_vorratskammer_zurueck)
        layout = findViewById(R.id.vorratskammerItemLayout)
        erstellen = findViewById(R.id.add_vorratskammer_new_item)

        val conManager = ConnectionManager()
        conManager.setOjects(this, false, findViewById(R.id.textViewInternetError))

        erstellen.setOnClickListener{
           showEventAddPopUp()
        }

        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }

        GlobalScope.launch(Dispatchers.Main){
            val list = VorratskammerDatabase().readDatabase(applicationContext)
            for(item in list){
                createEinkaufItem(item.text,item.id)
            }
        }
    }
   private fun showEventAddPopUp(){
        val eventPopUp = Dialog(this)

        eventPopUp.setContentView(R.layout.popup_vorratskammer_add)
        eventPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val abbrechen: Button = eventPopUp.findViewById(R.id.button_vorratskammer_abbrechen)
        val hinzufuegen: Button = eventPopUp.findViewById(R.id.button_vorratskammer_hinzufuegen)
        val eventText: EditText = eventPopUp.findViewById(R.id.editText_Neuer_Eintrag)
       val popupzurueck: ImageButton = eventPopUp.findViewById(R.id.imageButton_vorratskammer_eintrag_zurueck)

       popupzurueck.setOnClickListener {
           eventPopUp.dismiss()
       }

        abbrechen.setOnClickListener {
            eventPopUp.dismiss()
        }

        hinzufuegen.setOnClickListener {
            if (eventText.text.isNotEmpty()) {
                createEinkaufItem(eventText.text.toString(),nextId())

                eventPopUp.dismiss()
            } else{
                val toast = Toast.makeText(applicationContext, "Gebe zuerst etwas ein!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        eventPopUp.show()
    }

    private fun createEinkaufItem(text:String, id:Int){
        val viewItem = View.inflate(this, R.layout.item_vorratskammer,null)
        val textView:TextView = viewItem.findViewById(R.id.itemTextView)
        textView.text = text
        val button:ImageButton = viewItem.findViewById(R.id.button_loeschen_vorratskammer)

        val vorratItem = VorratskammerData(text, id)

        button.setOnClickListener{
            VorratskammerDatabase().deleteDatabaseItem(vorratItem.id, applicationContext)
            layout.removeView(viewItem)
            itemList.remove(vorratItem.id)
        }
        layout.addView(viewItem)
        VorratskammerDatabase().writeDatabase(vorratItem, applicationContext)
        itemList.add(vorratItem.id)
    }

    private fun nextId():Int{
        var newId = 0
        for (i in itemList){
            if (i >= newId){
                newId = i+1
            }
        }
        return newId
    }
}
