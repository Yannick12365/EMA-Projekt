package com.example.ema_projekt.wgplaner

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ema_projekt.ConnectionManager
import com.example.ema_projekt.R
import com.example.ema_projekt.einkaufsliste.EinkaufslisteActivity
import com.example.ema_projekt.hottopics.HotTopicsActivity
import com.example.ema_projekt.kalender.KalenderActivity
import com.example.ema_projekt.putzplan.PutzplanActivity
import com.example.ema_projekt.vorratskammer.VorratskammerActivity
import com.example.ema_projekt.wginfo.WGInfoActivity

class WGPlanerActivity : AppCompatActivity() {
    private lateinit var vorratskammer:ImageButton
    private lateinit var kalender:ImageButton
    private lateinit var putzplan:ImageButton
    private lateinit var einkaufsliste:ImageButton
    private lateinit var hottopics:ImageButton
    private lateinit var wgInfo: ImageButton

    companion object {
        private lateinit var conManager: ConnectionManager
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_wgplaner)

        //Activity Felder holen
        vorratskammer = findViewById(R.id.imageButton_vorratskammer)
        kalender = findViewById(R.id.imageButton_kalender)
        putzplan = findViewById(R.id.imageButton_putzplan)
        einkaufsliste = findViewById(R.id.imageButton_einkaufsliste)
        hottopics = findViewById(R.id.imageButton_hottopics)
        wgInfo = findViewById(R.id.imageButto_wginfo)

        //Listener Für Netzwerk veränderungen erstellen und ConnectionManager einstellen
        conManager = ConnectionManager()
        conManager.setOjects(false, this)
        conManager.switchScreen(this)
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(conManager, filter)

        //Klick Eventlistener
        vorratskammer.setOnClickListener{
            vorratskammer.setBackgroundResource(R.drawable.vorratskammerklick)
            startActivity(Intent(this, VorratskammerActivity::class.java))
        }

        kalender.setOnClickListener{
            kalender.setBackgroundResource(R.drawable.kalenderklick)
            startActivity(Intent(this, KalenderActivity::class.java))
        }

        putzplan.setOnClickListener{
            putzplan.setBackgroundResource(R.drawable.putzplanklick)
            startActivity(Intent(this, PutzplanActivity::class.java))
        }

        einkaufsliste.setOnClickListener{
            einkaufsliste.setBackgroundResource(R.drawable.einkaufslisteklick)
            startActivity(Intent(this, EinkaufslisteActivity::class.java))
        }

        hottopics.setOnClickListener{
            hottopics.setBackgroundResource(R.drawable.hottopicsklick)
            startActivity(Intent(this, HotTopicsActivity::class.java))
        }

        wgInfo.setOnClickListener {
            wgInfo.setBackgroundResource(R.drawable.wginfoklick)
            startActivity(Intent(this, WGInfoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        //Bild mit weißen Hintergrund setzen
        vorratskammer.setBackgroundResource(R.drawable.vorratskammer)
        kalender.setBackgroundResource(R.drawable.kalender)
        putzplan.setBackgroundResource(R.drawable.putzplan)
        einkaufsliste.setBackgroundResource(R.drawable.einkaufsliste)
        hottopics.setBackgroundResource(R.drawable.hottopics)
        wgInfo.setBackgroundResource(R.drawable.wginfo)

        conManager.setOjects(false, this)
        conManager.switchScreen(this)
    }

    override fun onDestroy() {
        unregisterReceiver(conManager)
        super.onDestroy()
    }
}