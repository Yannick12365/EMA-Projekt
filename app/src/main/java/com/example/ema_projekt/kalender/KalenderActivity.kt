package com.example.ema_projekt.kalender

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.ema_projekt.ConnectionManager
import com.example.ema_projekt.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

class KalenderActivity : AppCompatActivity() {
    private lateinit var zurueck: ImageButton
    private lateinit var eintragErstellen: Button
    private lateinit var gridLayout: GridLayout
    private lateinit var textViewMonth: TextView
    private lateinit var buttonMonthLeft: Button
    private lateinit var buttonMonthRight: Button
    private lateinit var showEvents: Button
    private lateinit var linearlayout: LinearLayout

    private lateinit var eventPopUpShow: Dialog

    //Variablen fuer Kalender
    private val monatList = mutableListOf<TextView>()
    private val showMonatList = mutableListOf<TextView>()

    private var aktuellerTag:Int = 0
    private var aktuellerMonat:Int = 0
    private var aktuellesJahr:Int = 0

    private var yearShow:Int = 0
    private var monthShow:Int = 0

    private lateinit var textViewDayFocus:TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_kalender)

        //Activity Felder holen
        zurueck = findViewById(R.id.imageButton_kalender_zurueck)
        eintragErstellen = findViewById(R.id.button_erstellen)
        gridLayout = findViewById(R.id.dayGrid)
        textViewMonth = findViewById(R.id.textViewShowMonth)
        buttonMonthLeft = findViewById(R.id.buttonChangeMonthLeft)
        buttonMonthRight = findViewById(R.id.buttonChangeMonthRight)
        showEvents = findViewById(R.id.button_ShowEvent)
        linearlayout = findViewById(R.id.linearlayout)

        //ConnectionManager einstellen
        val conManager = ConnectionManager()
        conManager.setOjects(false, this)
        conManager.switchScreen(this)

        //Kalender Variablen mit Inhalt fuellen
        textViewDayFocus = TextView(this)

        val c = Calendar.getInstance()

        yearShow = c.get(Calendar.YEAR)
        monthShow = c.get(Calendar.MONTH)+1

        aktuellerTag = SimpleDateFormat("d").format(Date()).toInt()-1
        aktuellerMonat = monthShow
        aktuellesJahr = yearShow

        createCalenderDays()

        val tagStart = YearMonth.of( yearShow , monthShow ).atDay(1).dayOfWeek.value-1
        val yearMonth: YearMonth = YearMonth.of(yearShow, monthShow)

        createMonth(tagStart,yearMonth.lengthOfMonth()+tagStart-1)

        textViewMonth.text = getMonthByInt(monthShow) + yearShow

        //Warten auf das Auslesen der Datenbank
        var list = mutableListOf<KalenderEventData>()
        GlobalScope.launch(Dispatchers.Main) {
            KalenderEventDatabase().removeOneYearOldEvents(aktuellerTag,aktuellerMonat, aktuellesJahr,applicationContext)
            list = KalenderEventDatabase().readKalenderEventDatabase(applicationContext)
            KalenderEventJSON().writeKalenderEventJSON(list,applicationContext)
            KalenderEvent().fillEventList(list)
            markEvents()
        }

        //Wenn kein Internet vorhanden Inhalt der JSON Datei nutzen
        if (!conManager.checkConnection(this)){
            val kalenderJSONarray = KalenderEventJSON().readKalenderEventJSON(applicationContext)
            for (i in 0 until kalenderJSONarray.length()) {
                list.add(
                    KalenderEventData(
                    kalenderJSONarray.getJSONObject(i).getInt("day"),
                        kalenderJSONarray.getJSONObject(i).getInt("month"),
                        kalenderJSONarray.getJSONObject(i).getInt("year"),
                        kalenderJSONarray.getJSONObject(i).getString("text"),
                        kalenderJSONarray.getJSONObject(i).getString("datestr"),
                        kalenderJSONarray.getJSONObject(i).getInt("id")
                ))
            }
            KalenderEvent().fillEventList(list)
            markEvents()
        }

        //Klick Eventlistener
        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }

        buttonMonthRight.setOnClickListener {
            //Naechsten Monat einzeigen
            monthShow+=1

            if (monthShow > 12){
                monthShow = 1
                yearShow+=1
            }
            switchMonth()
            markEvents()
            showEvents.visibility = View.INVISIBLE
        }

        buttonMonthLeft.setOnClickListener {
            //Vorheaerigen Monat anzeigen
            monthShow-=1

            if (monthShow < 1){
                monthShow = 12
                yearShow-=1
            }
            switchMonth()
            markEvents()
            showEvents.visibility = View.INVISIBLE
        }

        //Event Eintrag erstellen
        eintragErstellen.setOnClickListener {
            if (textViewDayFocus in showMonatList) {
                var month:String = monthShow.toString()
                var day:String = textViewDayFocus.text.toString()

                if(monthShow < 10){
                    month = "0" + month
                }
                if (day.length == 1){
                    day = "0" + day
                }

                val dateStr = day +"."+month+"."+yearShow
                val event = KalenderEventData(day.toInt(),monthShow,yearShow,"",dateStr,KalenderEvent().getNewId())

                if (!checkOneYearOld(event)) {
                    showEventAddPopUp(event)
                } else{
                    val toast =
                        Toast.makeText(applicationContext, "Der Tag liegt zu lange in der Vergangenheit!", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }else {
                val toast =
                    Toast.makeText(applicationContext, "Wähle einen Tag aus!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        showEvents.setOnClickListener {
            showEvents.visibility = View.INVISIBLE

            showEventShowPopUp()
        }
    }

    //Events hinzufuegen PopUp
    private fun showEventAddPopUp(event: KalenderEventData){
        val eventPopUp = Dialog(this)

        eventPopUp.setContentView(R.layout.popup_add_kalender_event)
        eventPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //PopUp Items holen
        val zurueckAdd:ImageButton = eventPopUp.findViewById(R.id.imageButton_kalenderevent_zurueck)
        val showDate:TextView = eventPopUp.findViewById(R.id.textView_show_date)
        val abbrechen:Button = eventPopUp.findViewById(R.id.button_kalenderevent_abbrechen)
        val bestaetigen:Button = eventPopUp.findViewById(R.id.button_kalenderevent_bestaetigen)
        val eventText:EditText = eventPopUp.findViewById(R.id.editText_eventtext)

        showDate.text = event.dateStr

        //Klick Eventlistener
        zurueckAdd.setOnClickListener {
            zurueckAdd.setBackgroundResource(R.drawable.zurueckklick)
            eventPopUp.dismiss()
        }
        abbrechen.setOnClickListener {
            eventPopUp.dismiss()
        }

        bestaetigen.setOnClickListener {
            if (eventText.text.isNotEmpty()) {
                event.text = eventText.text.toString()
                KalenderEvent().addEvents(event)
                KalenderEventDatabase().writeKalendereventDatabase(event,applicationContext)
                KalenderEventJSON().addKalenderEventJSON(event,applicationContext)
                markEvents()
                eventPopUp.dismiss()
            } else{
                val toast = Toast.makeText(applicationContext, "Gebe einen Text ein!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        showEvents.visibility = View.INVISIBLE
        textViewDayFocus.setBackgroundResource(R.drawable.calender_day_background)
        textViewDayFocus = TextView(this)
        markEvents()

        eventPopUp.show()
    }

    //Events von einem Tag anzeigen PopUp
    private fun showEventShowPopUp(){
        eventPopUpShow = Dialog(this)

        eventPopUpShow.setContentView(R.layout.popup_show_kalender_event)
        eventPopUpShow.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //PopUp Items holen
        val zureuckShow: ImageButton = eventPopUpShow.findViewById(R.id.imageButton_kalendershowevent_zurueck)
        val linearLayoutEvents: LinearLayout = eventPopUpShow.findViewById(R.id.linearlayoutEvents)

        //Klick Eventlistener
        zureuckShow.setOnClickListener {
            markEvents()
            zureuckShow.setBackgroundResource(R.drawable.zurueckklick)
            eventPopUpShow.dismiss()
        }

        createEventView(linearLayoutEvents)

        showEvents.visibility = View.INVISIBLE
        textViewDayFocus.setBackgroundResource(R.drawable.calender_day_background)
        textViewDayFocus = TextView(this)
        markEvents()

        eventPopUpShow.show()
    }

    //Event bearbeiten PopUp
    private fun showEventEditPopUp(event: KalenderEventData){
        val eventPopUp = Dialog(this)

        eventPopUp.setContentView(R.layout.popup_edit_kalender_event)
        eventPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //PopUp Items holen
        val zurueckEdit:ImageButton = eventPopUp.findViewById(R.id.imageButton_kalender_edit_event_zurueck)
        val showDate:TextView = eventPopUp.findViewById(R.id.textView_kalender_edit_show_date)
        val abbrechen:Button = eventPopUp.findViewById(R.id.button_kalender_edit_event_abbrechen)
        val bestaetigen:Button = eventPopUp.findViewById(R.id.button_kalender_edit_event_bestaetigen)
        val loeschen:Button = eventPopUp.findViewById(R.id.button_kalender_edit_event_loeschen)
        val eventText:EditText = eventPopUp.findViewById(R.id.editText_kalender_edit_eventText)

        showDate.text = event.dateStr

        eventText.setText(event.text)

        //Klick Eventlistener
        zurueckEdit.setOnClickListener {
            zurueckEdit.setBackgroundResource(R.drawable.zurueckklick)
            eventPopUp.dismiss()
        }

        abbrechen.setOnClickListener {
            eventPopUp.dismiss()
        }

        loeschen.setOnClickListener {
            //Event loeschen
            KalenderEvent().getEvents().remove(event)
            markEvents()

            var counter = 0
            for (eventCheck in KalenderEvent().getEvents()){
                if (eventCheck.day == event.day && eventCheck.year == event.year && eventCheck.month == event.month){
                    counter += 1;
                }
            }

            if (counter < 1) {
                for (textView in showMonatList){
                    if (textView.text == event.day.toString()){
                        textView.setBackgroundResource(R.drawable.calender_day_background)
                        break
                    }
                }
            }

            KalenderEventDatabase().deleteKalendereventDatabaseItem(event.id,applicationContext)
            KalenderEventJSON().deleteKalenderEventJSONItem(event.id,applicationContext)
            KalenderEvent().deleteEventFromList(event.id)

            eventPopUp.dismiss()
            eventPopUpShow.dismiss()
        }

        bestaetigen.setOnClickListener {
            //Event bearbeiten bestaetigen
            if (eventText.text.isNotEmpty()) {
                event.text = eventText.text.toString()
                KalenderEventDatabase().editKalendereventDatabaseEvent(event,applicationContext)
                KalenderEventJSON().editKalenderEventJSONItem(event,applicationContext)
                eventPopUp.dismiss()
                eventPopUpShow.dismiss()
            }else{
                val toast = Toast.makeText(applicationContext, "Gebe einen Text ein!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        eventPopUp.show()
    }

    //Monat wechsel Monat erstellen
    @RequiresApi(Build.VERSION_CODES.O)
    private fun switchMonth(){
        textViewMonth.text = getMonthByInt(monthShow) + yearShow

        resetMonth()

        val tagStart = YearMonth.of( yearShow , monthShow ).atDay(1).dayOfWeek.value-1
        val yearMonth: YearMonth = YearMonth.of(yearShow, monthShow)

        textViewDayFocus.setBackgroundResource(R.drawable.calender_day_background)
        textViewDayFocus = TextView(this)
        createMonth(tagStart,yearMonth.lengthOfMonth()+tagStart-1)
    }

    //Kalender Tage erstellen und anzeigen
    private fun createCalenderDays(){
        for (i in 1..37) {
            val textView = TextView(this)
            textView.textSize = 20F
            textView.setTextColor(ContextCompat.getColor(this,R.color.black))
            textView.setBackgroundResource(R.drawable.calender_day_background)
            textView.gravity = Gravity.CENTER
            textView.width = 135
            textView.height = 135
            textView.visibility = View.INVISIBLE

            textView.setOnClickListener {
                if (textView != textViewDayFocus) {
                    textViewDayFocus.setBackgroundResource(R.drawable.calender_day_background)

                    if (KalenderEvent().checkForEventByTextView(textViewDayFocus,monthShow,yearShow)){
                        textViewDayFocus.setBackgroundResource(R.drawable.calender_event_day_background)
                    }
                    if (KalenderEvent().checkForEventByTextView(textView,monthShow,yearShow)){
                        showEvents.visibility = View.VISIBLE
                    }else{
                        showEvents.visibility = View.INVISIBLE
                    }
                    textView.setBackgroundResource(R.drawable.calender_day_backgroung_focus)
                    textViewDayFocus = textView
                }
            }
            monatList.add(textView)
            gridLayout.addView(textView)
        }
    }

    //Monatsname bekommen
    private fun getMonthByInt(nr:Int) :String{
        when (nr) {
            1 -> {
                return "Januar "
            }
            2 -> {
                return "Februar "
            }
            3 -> {
                return "März "
            }
            4 -> {
                return "April "
            }
            5 -> {
                return "Mai "
            }
            6 -> {
                return "Juni "
            }
            7 -> {
                return "Juli "
            }
            8 -> {
                return "August "
            }
            9 -> {
                return "September "
            }
            10 -> {
                return "Oktober "
            }
            11 -> {
                return "November "
            }
            12 -> {
                return "Dezember "
            }
            else -> return "ERROR"
        }
    }

    //neuen Monat erstellen
    private fun createMonth(nrStart:Int, nrEnde:Int){
        for (i in nrStart..nrEnde){
            showMonatList.add(monatList[i])
        }

        markCurrentDay()

        var count = 1
        for (j in showMonatList){
            j.text = count.toString()
            j.visibility = View.VISIBLE
            count+=1
        }
    }

    //alten Monat entfernen
    private fun resetMonth(){
        for (i in monatList){
            i.text = ""
            i.setBackgroundResource(R.drawable.calender_day_background)
            i.visibility = View.INVISIBLE
            i.setTextColor(ContextCompat.getColor(this,R.color.black))
        }

        showMonatList.clear()
    }

    //aktuellen Tag makieren
    private fun markCurrentDay(){
        if (monthShow == aktuellerMonat && yearShow == aktuellesJahr){
            val textviewAktuellerTag: TextView = showMonatList[aktuellerTag]
            textviewAktuellerTag.setTextColor(ContextCompat.getColor(this,R.color.calender_Today))
        }
    }

    //Events makieren
    private fun markEvents(){
        val events = KalenderEvent().getEvents()

        for (event in events){
            if (event.month == monthShow && event.year == yearShow){
                showMonatList[event.day-1].setBackgroundResource(R.drawable.calender_event_day_background)
            }
        }
    }

    //Event anzeigen Item erstellen
    private fun createEventView(linearLayoutEvents: LinearLayout){
        val events = KalenderEvent().getEvents()

        for (event in events){
            if (event.month == monthShow && event.year == yearShow && event.day.toString() == textViewDayFocus.text){
                //Item Items holen
                val viewItem: View = View.inflate(this, R.layout.item_kalender_event, null)
                val textView:TextView = viewItem.findViewById(R.id.kalender_event_text)
                textView.text = "["+event.dateStr+"] "+event.text

                //Klick Eventlistener
                textView.setOnClickListener {
                    showEventEditPopUp(event)
                }
                linearLayoutEvents.addView(viewItem)
            }
        }
    }

    //Pruefen ob Event 1 Jahr alt ist
    private fun checkOneYearOld(event: KalenderEventData): Boolean{
        if (event.year <= aktuellesJahr-1){
            if (event.month <= aktuellerMonat){
                if (event.day <= aktuellerTag+1){
                    return true
                }
            }
        }
        return false
    }
}