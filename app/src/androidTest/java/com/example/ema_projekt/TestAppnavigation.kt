package com.example.ema_projekt

import androidx.appcompat.view.menu.ActionMenuItem
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ema_projekt.wginfo.WGInfoActivity
import com.example.ema_projekt.wgplaner.WGPlanerActivity
import com.example.ema_projekt.vorratskammer.VorratskammerActivity
import com.example.ema_projekt.putzplan.PutzplanActivity
import com.example.ema_projekt.hottopics.HotTopicsActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TestAppnavigation {

    @Before
    fun setUp(){
        DatabaseManager().setUpDatabase()
    }

    @Test
    fun checkRightLogin() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.wgnameinput)).perform(typeText("test"))
        onView(withId(R.id.wgtokeninput)).perform(typeText("test"))
        onView(withId(R.id.button_einloggen)).perform(click())
        onView(withId(R.id.imageButton_vorratskammer)).check(matches(isDisplayed()))
    }

    @Test
    fun checkWrongLogin() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.wgnameinput)).perform(typeText(" "))
        onView(withId(R.id.wgtokeninput)).perform(typeText(" "))
        onView(withId(R.id.button_einloggen)).perform(click())
        onView(withId(R.id.wgnameinput)).check(matches(isDisplayed()))
    }

    @Test
    fun checkClickVorratskammer() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButton_vorratskammer)).perform(click())
        onView(withId(R.id.imageButton_vorratskammer_zurueck)).perform(click())
    }
    @Test
    fun checkClickKalender() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButton_kalender)).perform(click())
        onView(withId(R.id.imageButton_kalender_zurueck)).perform(click())
    }
    @Test
    fun checkClickPutzplan() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButton_putzplan)).perform(click())
        onView(withId(R.id.imageButton_putzplan_zurueck)).perform(click())
    }

    @Test
    fun checkClickEinkaufsliste() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButton_einkaufsliste)).perform(click())
        onView(withId(R.id.imageButton_einkauf_zurueck)).perform(click())
    }

    @Test
    fun checkClickHotTopics() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButton_hottopics)).perform(scrollTo(),click())
        onView(withId(R.id.imageButton_hot_topics_zurueck)).perform(click())
    }

    @Test
    fun checkClickWGInfo() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButto_wginfo)).perform(scrollTo(),click())
        onView(withId(R.id.imageButton_wginfo_zurueck)).perform(click())
    }

    @Test
    fun checkWGVerlassen() {
        ActivityScenario.launch(WGInfoActivity::class.java)
        onView(withId(R.id.button_wgverlassen)).perform(click())
        onView(withId(R.id.wgnameinput)).check(matches(isDisplayed()))
    }

    @Test
    fun checkVorratskammerAddButton(){
        ActivityScenario.launch(VorratskammerActivity::class.java)
        Thread.sleep(500) //sleep hier immer benutzt damit man besser sieht, was bei dem Test genau passiert
        onView(withId(R.id.add_vorratskammer_new_item)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.button_vorratskammer_abbrechen)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkVorratskammerNeuesItemHinzufügen(){
        ActivityScenario.launch(VorratskammerActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.add_vorratskammer_new_item)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.editText_Neuer_Eintrag_Vorratskammer)).perform(typeText("Apfel"))
        Thread.sleep(500)
        onView(withId(R.id.button_vorratskammer_hinzufuegen)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkVorratskammerItemLöschen(){  //geht nur, wenn genau ein einzelnes Item in der Vorratskammer ist!!!
        ActivityScenario.launch(VorratskammerActivity::class.java)
        Thread.sleep(500)
        onView(withId(com.example.ema_projekt.R.id.button_loeschen_vorratskammer)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkPutzplanAddButton(){
        ActivityScenario.launch(PutzplanActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.putzplan_add)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.button_putzplan_person_abbrechen)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkPutzplanNeuePersonHinzufuegen(){
        ActivityScenario.launch(PutzplanActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.putzplan_add)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.editText_Neuer_Eintrag_Putzplan_Add_Person)).perform(typeText("Simeon"))
        Thread.sleep(500)
        onView(withId(R.id.button_putzplan_person_hinzufügen)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkPutzplanAufgabeÄndern(){
        ActivityScenario.launch(PutzplanActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.putzplan_aufgabe)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.editText_Neue_Aufgabe_Putzplan)).perform(typeText("Badezimmer"))
        Thread.sleep(500)
        onView(withId(R.id.button_putzplan_ändern)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkPutzplanPersonLöschen(){ //geht auch nur, wenn genau eine Person vorhanden ist!!!
        ActivityScenario.launch(PutzplanActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.putzplan_person)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.button_putzplan_loeschen_ja)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkHotTopicsHinzufügen(){
        ActivityScenario.launch(HotTopicsActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.editText_was_steht_an)).perform(typeText("Wir muessen noch Unit Tests machen!"))
        Thread.sleep(500)
        onView(withId(R.id.button_hot_topics_hinzufügen)).perform(click())
    }

    @Test
    fun checkHotTopicsTopicLöschen(){ //geht nur, wenn genau ein HotTopic zur Verfügung steht!!!
        ActivityScenario.launch(HotTopicsActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.hot_topic_loeschen)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkHotTopicKommentarSchreiben(){ //geht noch nicht!!
        ActivityScenario.launch(HotTopicsActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.hot_topic_text)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.linearlayout_kommentare)).perform(typeText("Das ist richtig!!"))
        Thread.sleep(500)
        onView(withId(R.id.button_hottopickommentare_hinzufuegen)).perform(click())
        Thread.sleep(500)

    }

}