package com.example.ema_projekt.putzplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ema_projekt.R
import androidx.appcompat.app.AppCompatDelegate

class Putzplan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_putzplan)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }
}