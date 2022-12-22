package com.example.ema_projekt.kalender

import android.widget.TextView

class KalenderEvent(day:Int = 0, month:Int = 0, year:Int = 0, text:String = "", dateStr:String = "", id:Int = -1) {

    private var day:Int = day
    private var month:Int = month
    private var year:Int = year
    private var text:String = text
    private var dateStr:String = dateStr
    private var id:Int = id

    companion object {
        var events: ArrayList<KalenderEvent> = ArrayList()
    }

    fun getDay():Int{
        return day
    }

    fun getDateStr():String{
        return dateStr
    }

    fun getId():Int{
        return id
    }

    fun getMonth():Int{
        return month
    }

    fun getYear():Int{
        return year
    }

    fun getText():String{
        return text
    }

    fun setText(text_in:String){
        text = text_in
    }

    fun getEvents():MutableList<KalenderEvent>{
        return events
    }

    fun addEvents(event:KalenderEvent){
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

    fun checkForEventByTextView(textView:TextView, month: Int, year: Int):Boolean{
        for (event in events) {
            if (event.getDay().toString() == textView.text && month == event.getMonth() && year == event.getYear()) {
                return true
            }
        }
        return false
    }
}