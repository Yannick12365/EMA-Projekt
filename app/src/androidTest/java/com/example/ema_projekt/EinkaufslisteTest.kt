package com.example.ema_projekt

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ema_projekt.einkaufsliste.EinkaufslisteActivity
import com.example.ema_projekt.vorratskammer.VorratskammerActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EinkaufslisteTest {
    @Before
    fun setUp(){
        DatabaseManager().setUpDatabase()
    }

    @Test
    fun itemErstellenTest(){
        ActivityScenario.launch(EinkaufslisteActivity::class.java)
        onView(withId(R.id.editText)).perform(typeText("Einkauf Unit"))
        Thread.sleep(500)
        onView(withId(R.id.button)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun itemLoeschenTest(){ //geht nur, wenn genau ein einzelnes Item in der Einkaufsliste ist!!!
        ActivityScenario.launch(EinkaufslisteActivity::class.java)
        onView(withId(R.id.einkaufItem_loeschen)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun einkaufBeendenTest(){
        ActivityScenario.launch(EinkaufslisteActivity::class.java)
        onView(withText("Einkauf Unit")).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.button2)).perform(click())
        Thread.sleep(500)
        onView(withText("Einkauf Unit")).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.button_einkaufbeenden_bestaetigen)).perform(click())
        Thread.sleep(500)
    }
}