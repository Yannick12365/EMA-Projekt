package com.example.ema_projekt

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ema_projekt.vorratskammer.VorratskammerActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VorratsKammerTest {
    @Before
    fun setUp(){
        DatabaseManager().setUpDatabase()
    }

    @Test
    fun checkVorratskammerAddButton(){
        ActivityScenario.launch(VorratskammerActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.add_vorratskammer_new_item)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.button_vorratskammer_abbrechen)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkVorratskammerNeuesItemHinzufuegen(){
        ActivityScenario.launch(VorratskammerActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.add_vorratskammer_new_item)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.editText_Neuer_Eintrag_Vorratskammer)).perform(typeText("VKTestItem"))
        Thread.sleep(500)
        onView(withId(R.id.button_vorratskammer_hinzufuegen)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkVorratskammerItemLoeschen(){  //geht nur, wenn genau ein einzelnes Item in der Vorratskammer ist!!!
        ActivityScenario.launch(VorratskammerActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.button_loeschen_vorratskammer)).perform(click())
        Thread.sleep(500)
    }
}