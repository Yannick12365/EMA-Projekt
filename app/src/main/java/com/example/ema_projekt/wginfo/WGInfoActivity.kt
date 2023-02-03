package com.example.ema_projekt.wginfo

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ema_projekt.MainActivity
import com.example.ema_projekt.R
import com.example.ema_projekt.wgplaner.LoginData
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.*
import org.json.JSONObject


class WGInfoActivity : AppCompatActivity() {
    private lateinit var zurueck: ImageButton
    private lateinit var textViewToken:TextView
    private lateinit var wgverlassenButton:Button
    private lateinit var textViewwginfo:TextView
    private lateinit var buttonhinzufuegen:Button
    private lateinit var buttonbearbeiten:Button

    val database: DatabaseReference = FirebaseDatabase.getInstance("https://ema-projekt-e036e-default-rtdb.europe-west1.firebasedatabase.app/").reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wginfo)

        zurueck = findViewById(R.id.imageButton_wginfo_zurueck)
        textViewToken = findViewById(R.id.textView_wgtoken)
        wgverlassenButton = findViewById(R.id.button_wgverlassen)
        textViewwginfo = findViewById(R.id.textView_wginfotext)
        buttonhinzufuegen = findViewById(R.id.button_hinzufuegen)
        buttonbearbeiten = findViewById(R.id.button_bearbeiten)

        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }

        textViewToken.text = "WG Token = " + LoginDataSettingsJSON().readLoginDataJSON(applicationContext).wgToken

        //textViewwginfo.text = WGInfoJSON().readJSON(applicationContext)
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(applicationContext).wgName
        database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("WGInfo")) {
                    textViewwginfo.text = snapshot.child("WGInfo").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Ups, da ist etwas schief gelaufen!",
                    Toast.LENGTH_SHORT).show()
            }
        })

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

    private fun wgInfoPopUp(text:String){
        val popup = Dialog(this)

        popup.setContentView(R.layout.popup_wg_info_text)
        popup.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val zurueckpopup:ImageButton = popup.findViewById(R.id.imageButton_wginfo_text_zurueck)
        val editText:EditText = popup.findViewById(R.id.editText_wginfotext)
        val bestaetigen:Button = popup.findViewById(R.id.button_wginfotext_bestaetigen)
        val abbrechen:Button = popup.findViewById(R.id.button_wginfotext_abbrechen)

        editText.setText(text)

        zurueckpopup.setOnClickListener {
            popup.dismiss()
        }

        abbrechen.setOnClickListener {
            popup.dismiss()
        }

        bestaetigen.setOnClickListener {
            textViewwginfo.text = editText.text
            //WGInfoJSON().writeJSON(editText.text.toString(),applicationContext)
            WGInfoJSON().writeDatabase(editText.text.toString(),applicationContext)
            popup.dismiss()
        }

        popup.show()
    }
}