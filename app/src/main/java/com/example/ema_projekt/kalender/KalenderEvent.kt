package com.example.ema_projekt.kalender

import android.widget.TextView

class KalenderEvent {
    companion object {
        var events: ArrayList<KalenderEventData> = ArrayList()
    }

    fun getEvents():MutableList<KalenderEventData>{
        return events
    }

    fun addEvents(event:KalenderEventData){
        events.add(event)
    }

    fun getNewId():Int{
        var newId = -1
        for (event in events){
            if (newId < event.id){
                newId = event.id
            }
        }
        return newId+1
    }

    fun checkForEventByTextView(textView: TextView, month: Int, year: Int):Boolean{
        for (event in events) {
            if (event.day.toString() == textView.text && month == event.month && year == event.year) {
                return true
            }
        }
        return false
    }
}