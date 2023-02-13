package com.example.ema_projekt

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ema_projekt.hottopics.HotTopicsActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HotTopicTest {
    @Before
    fun setUp(){
        DatabaseManager().setUpDatabase()
    }

    @Test
    fun checkHotTopicsHinzufuegen(){
        ActivityScenario.launch(HotTopicsActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.editText_was_steht_an)).perform(typeText("Wir muessen noch Unit Tests machen!"))
        Thread.sleep(500)
        onView(withId(R.id.button_hot_topics_hinzufügen)).perform(click())
    }

    @Test
    fun checkHotTopicsTopicLoeschen(){ //geht nur, wenn genau ein HotTopic zur Verfügung steht!!!
        ActivityScenario.launch(HotTopicsActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.hot_topic_loeschen)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkHotTopicKommentarSchreiben(){
        ActivityScenario.launch(HotTopicsActivity::class.java)
        Thread.sleep(500)
        onView(withText("Wir muessen noch Unit Tests machen!")).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.editText_kommentar)).perform(typeText("Das ist richtig!!"))
        Thread.sleep(500)
        onView(withId(R.id.button_hottopickommentare_hinzufuegen)).perform(click())
        Thread.sleep(500)

    }

    @Test
    fun checkHotTopicKommentarLoeschen(){ //geht nur, wenn genau ein Kommentar zur Verfügung steht!!!
        ActivityScenario.launch(HotTopicsActivity::class.java)
        Thread.sleep(500)
        onView(withText("Wir muessen noch Unit Tests machen!")).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.button_loeschen_hottopickommentar)).perform(click())
        Thread.sleep(500)

    }
}