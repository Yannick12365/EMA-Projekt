package com.example.ema_projekt.vorratskammer


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.example.ema_projekt.R

class Vorratskammer : AppCompatActivity() {

    private lateinit var erstellen: com.google.android.material.floatingactionbutton.FloatingActionButton;
    private lateinit var layout:LinearLayout

    private val itemList = mutableMapOf<Int, View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vorratskammer)

        layout = findViewById(R.id.vorratskammerItemLayout)
        erstellen = findViewById(R.id.add_vorratskammer_new_item)

        erstellen.setOnClickListener{
           showEventAddPopUp()
        }

    }
   private fun showEventAddPopUp(){
        val eventPopUp = Dialog(this)


        eventPopUp.setContentView(R.layout.popup_vorratskammer_add)
        eventPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))



        val abbrechen: Button = eventPopUp.findViewById(R.id.button_vorratskammer_abbrechen)
        val hinzufuegen: Button = eventPopUp.findViewById(R.id.button_vorratskammer_hinzufuegen)
        val eventText: EditText = eventPopUp.findViewById(R.id.editText_Neuer_Eintrag)


        abbrechen.setOnClickListener {
            eventPopUp.dismiss()
        }

        hinzufuegen.setOnClickListener {
            if (eventText.text.isNotEmpty()) {
                val viewItem = View.inflate(this, R.layout.item_vorratskammer,null)
                val textView:TextView = viewItem.findViewById(R.id.itemTextView)
                textView.text = eventText.text
                val button:ImageButton = viewItem.findViewById(R.id.button_loeschen_vorratskammer)
                button.setOnClickListener{

                    layout.removeView(viewItem)
                }
                layout.addView(viewItem)



                eventPopUp.dismiss()
            } else{
                val toast = Toast.makeText(applicationContext, "Gebe zuerst etwas ein!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }




        eventPopUp.show()
    }
}
