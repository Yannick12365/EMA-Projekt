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
import com.example.ema_projekt.wginfo.WGInfoData
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
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
        Thread.sleep(500)
    }

    @Test
    fun checkWrongLogin() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.wgnameinput)).perform(typeText(" "))
        onView(withId(R.id.wgtokeninput)).perform(typeText(" "))
        onView(withId(R.id.button_einloggen)).perform(click())
        onView(withId(R.id.wgnameinput)).check(matches(isDisplayed()))
        Thread.sleep(500)
    }

    @Test
    fun checkClickVorratskammer() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButton_vorratskammer)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.imageButton_vorratskammer_zurueck)).perform(click())
        Thread.sleep(500)
    }
    @Test
    fun checkClickKalender() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButton_kalender)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.imageButton_kalender_zurueck)).perform(click())
        Thread.sleep(500)
    }
    @Test
    fun checkClickPutzplan() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButton_putzplan)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.imageButton_putzplan_zurueck)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkClickEinkaufsliste() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButton_einkaufsliste)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.imageButton_einkauf_zurueck)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkClickHotTopics() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButton_hottopics)).perform(scrollTo(),click())
        Thread.sleep(500)
        onView(withId(R.id.imageButton_hot_topics_zurueck)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkClickWGInfo() {
        ActivityScenario.launch(WGPlanerActivity::class.java)
        onView(withId(R.id.imageButto_wginfo)).perform(scrollTo(),click())
        Thread.sleep(500)
        onView(withId(R.id.imageButton_wginfo_zurueck)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun checkWGVerlassen() {
        ActivityScenario.launch(WGInfoActivity::class.java)
        onView(withId(R.id.button_wgverlassen)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.wgnameinput)).check(matches(isDisplayed()))
        Thread.sleep(500)
    }
}