package com.example.ema_projekt.kalender

import android.widget.TextView

class KalenderEvent {
    //Liste mit allen Events
    companion object {
        var events: ArrayList<KalenderEventData> = ArrayList()
    }

    //Eventliste getter
    fun getEvents():MutableList<KalenderEventData>{
        return events
    }

    //Event in LIste hinzufuegen
    fun addEvents(event:KalenderEventData){
        events.add(event)
    }

    //ID fuer Event festlegen
    fun getNewId():Int{
        var newId = -1
        for (event in events){
            if (newId < event.id){
                newId = event.id
            }
        }
        return newId+1
    }

    //Pruefen ob Tag ein Event hat
    fun checkForEventByTextView(textView: TextView, month: Int, year: Int):Boolean{
        for (event in events) {
            if (event.day.toString() == textView.text && month == event.month && year == event.year) {
                return true
            }
        }
        return false
    }

    //Eventliste fuellen
    fun fillEventList(list:List<KalenderEventData>){
        events = list as ArrayList<KalenderEventData>
    }

    //Event in Liste entfernen
    fun deleteEventFromList(id:Int){
        for (event in events){
            if (event.id == id){
                events.remove(event)
                return
            }
        }
    }
}