package com.example.ema_projekt.kalender

import android.widget.TextView
import org.json.JSONArray

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

    fun fillEventList(array:JSONArray){
        events.clear()
        for (i in 0 until array.length()) {
            val event = array.getJSONObject(i)
            val eventData = KalenderEventData(event.getInt("day"),event.getInt("month"),event.getInt("year"),event.getString("text"),event.getString("datestr"),event.getInt("id"))
            events.add(eventData)
        }
    }

    fun deleteEventFromList(id:Int){
        for (event in events){
            if (event.id == id){
                events.remove(event)
                return
            }
        }
    }
}