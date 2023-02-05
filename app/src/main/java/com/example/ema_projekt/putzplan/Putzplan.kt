package com.example.ema_projekt.putzplan

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.MenuView.ItemView
import com.example.ema_projekt.R

class Putzplan : AppCompatActivity() {

    private lateinit var erstellen: com.google.android.material.floatingactionbutton.FloatingActionButton;
    private lateinit var layout: LinearLayout
    private lateinit var zurueck:ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_putzplan)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_putzplan)

        zurueck = findViewById(R.id.imageButton_putzplan_zurueck)
        layout = findViewById(R.id.putzplan_layout)
        erstellen = findViewById(R.id.putzplan_add)

        erstellen.setOnClickListener {
            showEventAddPopUpPerson()
        }

        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }

    }

    private fun showEventAddPopUpPerson() {
        val eventPopUpPerson1 = Dialog(this)


        eventPopUpPerson1.setContentView(R.layout.popup_putzplan_neue_person)
        eventPopUpPerson1.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val abbrechen: Button = eventPopUpPerson1.findViewById(R.id.button_vorratskammer_abbrechen)
        val hinzufuegen: Button =
            eventPopUpPerson1.findViewById(R.id.button_vorratskammer_hinzufuegen)
        val eventText: EditText = eventPopUpPerson1.findViewById(R.id.editText_Neuer_Eintrag)


        abbrechen.setOnClickListener {
            eventPopUpPerson1.dismiss()
        }

        hinzufuegen.setOnClickListener {
            if (eventText.text.isNotEmpty()) {
                val viewItem = View.inflate(this, R.layout.item_putzplan, null)
                val textViewPerson: TextView = viewItem.findViewById(R.id.putzplan_person)
                textViewPerson.text = eventText.text
                val textViewAufgabe: TextView = viewItem.findViewById(R.id.putzplan_aufgabe)
                val spinner: Spinner =
                    viewItem.findViewById(R.id.putzplan_spinner)      //from https://developer.android.com/develop/ui/views/components/spinner
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

                textViewAufgabe.setOnClickListener {
                    showEventAddPopUpAufgabe(textViewAufgabe)

                }

                textViewPerson.setOnClickListener{
                    showEventAddPopUpLoeschen(viewItem)
                }


                layout.addView(viewItem)




                eventPopUpPerson1.dismiss()
            } else {
                val toast =
                    Toast.makeText(applicationContext, "Gebe zuerst etwas ein!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }




        eventPopUpPerson1.show()
    }

    private fun showEventAddPopUpLoeschen(item: View) {

        val eventPopUpLoeschen = Dialog(this)

        eventPopUpLoeschen.setContentView(R.layout.popup_putzplan_person_loeschen)
        eventPopUpLoeschen.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val nein: Button = eventPopUpLoeschen.findViewById(R.id.button_putzplan_loeschen_nein)
        val ja: Button =
            eventPopUpLoeschen.findViewById(R.id.button_putzplan_loeschen_ja)

        nein.setOnClickListener {
            eventPopUpLoeschen.dismiss()
        }

        ja.setOnClickListener{
            eventPopUpLoeschen.dismiss()
            layout.removeView(item)
        }

        eventPopUpLoeschen.show()
    }

    private fun showEventAddPopUpAufgabe(view: TextView){

        val eventPopUpAufgabe = Dialog(this)

        eventPopUpAufgabe.setContentView(R.layout.popup_putzplan_neue_aufgabe)
        eventPopUpAufgabe.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val abbrechen: Button = eventPopUpAufgabe.findViewById(R.id.button_vorratskammer_abbrechen)
        val hinzufuegen: Button =
            eventPopUpAufgabe.findViewById(R.id.button_vorratskammer_hinzufuegen)
        val eventText: EditText = eventPopUpAufgabe.findViewById(R.id.editText_Neuer_Eintrag)

        abbrechen.setOnClickListener {
            eventPopUpAufgabe.dismiss()
        }

        hinzufuegen.setOnClickListener {
            if (eventText.text.isNotEmpty()) {

                view.text = eventText.text

                eventPopUpAufgabe.dismiss()

            } else {
                val toast =
                    Toast.makeText(applicationContext, "Gebe zuerst etwas ein!", Toast.LENGTH_SHORT)
                toast.show()
            }


        }
        eventPopUpAufgabe.show()

    }
}