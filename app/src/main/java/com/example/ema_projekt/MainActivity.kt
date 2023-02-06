package com.example.ema_projekt

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.example.ema_projekt.wgplaner.LoginData
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.example.ema_projekt.wgplaner.WGPlanerActivity
import com.google.firebase.database.*

//Offline
//https://youtu.be/Et8njU58OTs

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var editTextName:EditText
    private lateinit var editTextToken:EditText

    private lateinit var conManager:ConnectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        conManager = ConnectionManager()

        val dbManager = DatabaseManager()
        dbManager.setUpDatabase()
        database = dbManager.getDatabaseReference()

        val loginbutton:Button = findViewById(R.id.button_einloggen)
        val erstellenTextview:TextView = findViewById(R.id.textView_wgerstellen)

        editTextName = findViewById(R.id.wgnameinput)
        editTextToken = findViewById(R.id.wgtokeninput)

        existingLogin()

        loginbutton.setOnClickListener {
            var wgName: String = editTextName.text.toString()
            var wgToken: String = editTextToken.text.toString()

            wgName = wgName.replace(" ","")
            wgToken = wgToken.replace(" ","")

            if (editTextName.text.isNotEmpty() && editTextToken.text.isNotEmpty()) {
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.hasChild(wgName)) {
                            if (dataSnapshot.child(wgName).child("token").value.toString() == wgToken) {
                                editTextName.setText("")
                                editTextToken.setText("")
                                LoginDataSettingsJSON().writeLoginDataJSON(LoginData(wgName,
                                    wgToken),
                                    applicationContext)
                                startActivity(Intent(this@MainActivity,
                                    WGPlanerActivity::class.java))
                            } else {
                                Toast.makeText(applicationContext, "Angegebener Token ist falsch!",
                                    Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(applicationContext, "Diese WG existiert nicht!",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(applicationContext, "Ups, da ist etwas schief gelaufen!",
                            Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        erstellenTextview.setOnClickListener {
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
        val textfield: EditText = popup.findViewById(R.id.editText_wgname)

        zurueck.setOnClickListener {
            popup.dismiss()
        }

        abbrechen.setOnClickListener {
            popup.dismiss()
        }

        bestaetigen.setOnClickListener {
            if (!textfield.text.toString().contains(" ")){
                if (textfield.text.toString().isNotEmpty()) {
                    val wgname = textfield.text.toString().lowercase()

                    database.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.hasChild(wgname)) {
                                val zeichen = ('a'..'z') + ('A'..'Z') + ('0'..'9')

                                var token = "$wgname-"
                                for (i in 1..5) {
                                    token += zeichen.random()
                                }
                                database.child(wgname).child("token").setValue(token)
                                Toast.makeText(applicationContext, "WG erfolgreich erstellt!",
                                    Toast.LENGTH_SHORT).show()

                                editTextName.setText(wgname)
                                editTextToken.setText(token)
                                popup.dismiss()
                            } else {
                                Toast.makeText(applicationContext, "Der angegebene WG Name existiert schon!",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext, "Ups, da ist etwas schief gelaufen!",
                                Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(applicationContext, "Gebe einen WG Namen ein!",
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Bitte verwende keine Leerzeichen!",
                    Toast.LENGTH_SHORT).show()
            }
        }
        popup.show()
    }

    private fun existingLogin(){
        val loginData = LoginDataSettingsJSON().readLoginDataJSON(applicationContext)
        if (loginData.wgName.isNotEmpty() && loginData.wgToken.isNotEmpty()){
            startActivity(Intent(this, WGPlanerActivity::class.java))
        } else {
            conManager.setOjects(this, true)
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(conManager, filter)
        }
    }
}