package com.example.ema_projekt

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ema_projekt.kalender.KalenderActivity
import com.example.ema_projekt.vorratskammer.VorratskammerActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KalenderTest {
    @Before
    fun setUp(){
        DatabaseManager().setUpDatabase()
    }

    @Test
    fun monatWechselCheck(){
        ActivityScenario.launch(KalenderActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.buttonChangeMonthLeft)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.buttonChangeMonthRight)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun eventHinzufuegenCheck(){
        ActivityScenario.launch(KalenderActivity::class.java)
        Thread.sleep(500)
        onView(withId(10)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.button_erstellen)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.editText_eventtext)).perform(typeText("Unit Event"))
        Thread.sleep(500)
        onView(withId(R.id.button_kalenderevent_bestaetigen)).perform(click())
    }

    @Test
    fun eventLoeschen(){ //geht nur, wenn genau ein einzelnes Event in dem KalenderS ist!!!
        ActivityScenario.launch(KalenderActivity::class.java)
        Thread.sleep(500)
        onView(withId(10)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.button_ShowEvent)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.kalender_event_text)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.button_kalender_edit_event_loeschen)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun eventBearbeiten(){ //geht nur, wenn genau ein einzelnes Event in dem KalenderS ist!!!
        ActivityScenario.launch(KalenderActivity::class.java)
        Thread.sleep(500)
        onView(withId(10)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.button_ShowEvent)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.kalender_event_text)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.editText_kalender_edit_eventText)).perform(replaceText("Unit Event Edit"))
        Thread.sleep(500)
        onView(withId(R.id.button_kalender_edit_event_bestaetigen)).perform(click())
        Thread.sleep(500)
    }
}