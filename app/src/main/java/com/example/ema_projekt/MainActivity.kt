package com.example.ema_projekt

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.ema_projekt.wgplaner.WGPlanerActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        val loginbutton:Button = findViewById(R.id.button_einloggen)
        val erstellbutton:Button = findViewById(R.id.button_erstellen)

        loginbutton.setOnClickListener {
            startActivity(Intent(this, WGPlanerActivity::class.java))
        }

        erstellbutton.setOnClickListener {
            showWGErstellPopUp()
        }
    }

    private fun showWGErstellPopUp(){
        val popup = Dialog(this)

        popup.setContentView(R.layout.popup_wg_erstellen)
        popup.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val zurueck: ImageButton = popup.findViewById(R.id.imageButton_wgerstellen_zurueck)
        val bestaetigen: Button = popup.findViewById(R.id.button_wgerstellen_bestaetigen)
        val abbrechen:Button = popup.findViewById(R.id.button_wgerstellen_abbrechen)
        val textfield:EditText = popup.findViewById(R.id.editText_wgname)
        val errorfield:TextView = popup.findViewById(R.id.textView_error)

        textfield.setOnClickListener {
            errorfield.text = ""
        }

        zurueck.setOnClickListener {
            popup.dismiss()
        }

        abbrechen.setOnClickListener {
            popup.dismiss()
        }

        bestaetigen.setOnClickListener {
            if (!textfield.text.toString().contains(" ")){
                popup.dismiss()
            } else {
                errorfield.text = "Bitte verwende keine Leerzeichen!"
            }
        }

        popup.show()
    }
}