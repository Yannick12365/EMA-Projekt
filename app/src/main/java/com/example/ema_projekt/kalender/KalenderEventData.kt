package com.example.ema_projekt.kalender

import android.widget.TextView

data class KalenderEventData(
    val day:Int,
    val month:Int,
    val year:Int,
    var text:String,
    val dateStr:String,
    val id:Int)

class KalenderEventJSON(){

}