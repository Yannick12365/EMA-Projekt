package com.example.ema_projekt.wginfo

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ema_projekt.ConnectionManager
import com.example.ema_projekt.MainActivity
import com.example.ema_projekt.R
import com.example.ema_projekt.wgplaner.LoginData
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class WGInfoActivity : AppCompatActivity() {
    private lateinit var zurueck: ImageButton
    private lateinit var textViewToken:TextView
    private lateinit var wgverlassenButton:Button
    private lateinit var textViewwginfo:TextView
    private lateinit var buttonhinzufuegen:Button
    private lateinit var buttonbearbeiten:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wginfo)

        //Activity Felder holen
        zurueck = findViewById(R.id.imageButton_wginfo_zurueck)
        textViewToken = findViewById(R.id.textView_wgtoken)
        wgverlassenButton = findViewById(R.id.button_wgverlassen)
        textViewwginfo = findViewById(R.id.textView_wginfotext)
        buttonhinzufuegen = findViewById(R.id.button_hinzufuegen)
        buttonbearbeiten = findViewById(R.id.button_bearbeiten)

        //ConnectionManager einstellen
        val conManager = ConnectionManager()
        conManager.setOjects(false, this)
        conManager.switchScreen(this)

        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }

        textViewToken.text = "WG Token = " + LoginDataSettingsJSON().readLoginDataJSON(applicationContext).wgToken

        //Warten auf das Auslesen der Datenbank
        GlobalScope.launch(Dispatchers.Main) {
            val text = WGInfoData().readWGInfoDatabase(applicationContext)
            textViewwginfo.text = text
            WGInfoJSON().writeWGInfoJSON(text,applicationContext)
        }
        //Wenn kein Internet vorhanden Inhalt der JSON Datei nutzen
        if (!conManager.checkConnection(this)){
            textViewwginfo.text = WGInfoJSON().readWGInfoJSON(applicationContext)
        }

        //Klick Eventlistener
        wgverlassenButton.setOnClickListener {
            LoginDataSettingsJSON().writeLoginDataJSON(LoginData("", ""), applicationContext)

            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        buttonbearbeiten.setOnClickListener {
            wgInfoPopUp(textViewwginfo.text.toString())
        }

        buttonhinzufuegen.setOnClickListener {
            wgInfoPopUp("")
        }
    }

    //PopUp zu WG Info Text schreiben
    private fun wgInfoPopUp(text:String){
        val popup = Dialog(this)

        popup.setContentView(R.layout.popup_wg_info_text)
        popup.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //PopUp Items holen
        val zurueckpopup:ImageButton = popup.findViewById(R.id.imageButton_wginfo_text_zurueck)
        val editText:EditText = popup.findViewById(R.id.editText_wginfotext)
        val bestaetigen:Button = popup.findViewById(R.id.button_wginfotext_bestaetigen)
        val abbrechen:Button = popup.findViewById(R.id.button_wginfotext_abbrechen)

        editText.setText(text)

        //Klick Eventlistener
        zurueckpopup.setOnClickListener {
            popup.dismiss()
        }

        abbrechen.setOnClickListener {
            popup.dismiss()
        }

        bestaetigen.setOnClickListener {
            textViewwginfo.text = editText.text
            WGInfoData().writeWGInfoDatabase(editText.text.toString(),applicationContext)
            WGInfoJSON().writeWGInfoJSON(editText.text.toString(),applicationContext)
            popup.dismiss()
        }

        popup.show()
    }
}