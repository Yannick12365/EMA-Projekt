package com.example.ema_projekt

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ema_projekt.einkaufsliste.EinkaufslisteActivity
import com.example.ema_projekt.kalender.KalenderActivity

class MainActivity : AppCompatActivity() {
    private lateinit var vorratskammer:ImageButton
    private lateinit var kalender:ImageButton
    private lateinit var putzplan:ImageButton
    private lateinit var einkaufsliste:ImageButton
    private lateinit var hottopics:ImageButton
    private lateinit var schuldenradar:ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        vorratskammer = findViewById(R.id.imageButton2)
        kalender = findViewById(R.id.imageButton3)
        putzplan = findViewById(R.id.imageButton4)
        einkaufsliste = findViewById(R.id.imageButton5)
        hottopics = findViewById(R.id.imageButton6)
        schuldenradar = findViewById(R.id.imageButton7)

        vorratskammer.setOnClickListener{
            vorratskammer.setBackgroundResource(R.drawable.vorratskammerklick)
            startActivity(Intent(this, Vorratskammer::class.java))
        }

        kalender.setOnClickListener{
            kalender.setBackgroundResource(R.drawable.kalenderklick)
            startActivity(Intent(this, KalenderActivity::class.java))
        }

        putzplan.setOnClickListener{
            putzplan.setBackgroundResource(R.drawable.putzplanklick)
            startActivity(Intent(this, Putzplan::class.java))
        }

        einkaufsliste.setOnClickListener{
            einkaufsliste.setBackgroundResource(R.drawable.einkaufslisteklick)
            startActivity(Intent(this, EinkaufslisteActivity::class.java))
        }

        hottopics.setOnClickListener{
            hottopics.setBackgroundResource(R.drawable.hottopicsklick)
            startActivity(Intent(this, HotTopics::class.java))
        }

        schuldenradar.setOnClickListener{
            schuldenradar.setBackgroundResource(R.drawable.schuldenradarklick)
            startActivity(Intent(this, Schuldenradar::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        vorratskammer.setBackgroundResource(R.drawable.vorratskammer)
        kalender.setBackgroundResource(R.drawable.kalender)
        putzplan.setBackgroundResource(R.drawable.putzplan)
        einkaufsliste.setBackgroundResource(R.drawable.einkaufsliste)
        hottopics.setBackgroundResource(R.drawable.hottopics)
        schuldenradar.setBackgroundResource(R.drawable.schuldenradar)
    }
}