package com.example.ema_projekt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.ema_projekt.kalender.KalenderActivity
import com.example.ema_projekt.wgplaner.WGPlanerActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button:Button = findViewById(R.id.button_einlogen)

        button.setOnClickListener {
            startActivity(Intent(this, WGPlanerActivity::class.java))
        }
    }
}