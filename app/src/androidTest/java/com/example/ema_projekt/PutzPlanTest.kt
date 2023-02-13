package com.example.ema_projekt

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ema_projekt.putzplan.PutzplanActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PutzPlanTest {
    @Before
    fun setUp(){
        DatabaseManager().setUpDatabase()
    }

    @Test
    fun checkPutzplanAddButton(){
        ActivityScenario.launch(PutzplanActivity::class.java)
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.putzplan_add)).perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.button_putzplan_person_abbrechen))
            .perform(ViewActions.click())
        Thread.sleep(500)
    }

    @Test
    fun checkPutzplanNeuePersonHinzufuegen(){
        ActivityScenario.launch(PutzplanActivity::class.java)
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.putzplan_add)).perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.editText_Neuer_Eintrag_Putzplan_Add_Person))
            .perform(ViewActions.typeText("Simeon"))
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.button_putzplan_person_hinzufügen))
            .perform(ViewActions.click())
        Thread.sleep(500)
    }

    @Test
    fun checkPutzplanAufgabeAendern(){
        ActivityScenario.launch(PutzplanActivity::class.java)
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.putzplan_aufgabe)).perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.editText_Neue_Aufgabe_Putzplan))
            .perform(ViewActions.typeText("Badezimmer"))
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.button_putzplan_ändern))
            .perform(ViewActions.click())
        Thread.sleep(500)
    }

    @Test
    fun checkPutzplanPersonLoeschen(){ //geht auch nur, wenn genau eine Person vorhanden ist!!!
        ActivityScenario.launch(PutzplanActivity::class.java)
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.putzplan_person)).perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.button_putzplan_loeschen_ja))
            .perform(ViewActions.click())
        Thread.sleep(500)
    }
}