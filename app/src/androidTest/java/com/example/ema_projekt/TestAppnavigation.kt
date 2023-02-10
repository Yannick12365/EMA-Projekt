package com.example.ema_projekt

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ema_projekt.wginfo.WGInfoActivity
import com.example.ema_projekt.wgplaner.WGPlanerActivity
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
}