package com.example.ema_projekt.putzplan

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.example.ema_projekt.R

class Putzplan : AppCompatActivity() {

    private lateinit var erstellen: com.google.android.material.floatingactionbutton.FloatingActionButton;
    private lateinit var layout:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_putzplan)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_putzplan)

        layout = findViewById(R.id.putzplan_layout)
        erstellen = findViewById(R.id.putzplan_add)

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
                val viewItem = View.inflate(this, R.layout.item_putzplan,null)
                val textViewPerson:TextView = viewItem.findViewById(R.id.putzplan_person)
                textViewPerson.text = eventText.text
                val spinner:Spinner = viewItem.findViewById(R.id.putzplan_spinner)      //from https://developer.android.com/develop/ui/views/components/spinner
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