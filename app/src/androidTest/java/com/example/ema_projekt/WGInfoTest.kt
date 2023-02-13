package com.example.ema_projekt

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ema_projekt.vorratskammer.VorratskammerActivity
import com.example.ema_projekt.wginfo.WGInfoActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WGInfoTest {
    @Before
    fun setUp(){
        DatabaseManager().setUpDatabase()
    }

    @Test
    fun addWGInfoTextCheck(){
        ActivityScenario.launch(WGInfoActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.button_hinzufuegen)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.editText_wginfotext)).perform(typeText("Ich bin ein WG Info Text aus dem Unit Test :)"))
        Thread.sleep(500)
        onView(withId(R.id.button_wginfotext_bestaetigen)).perform(click())
        Thread.sleep(500)
    }

    @Test
    fun editWGInfoTextCheck(){
        ActivityScenario.launch(WGInfoActivity::class.java)
        Thread.sleep(500)
        onView(withId(R.id.button_bearbeiten)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.editText_wginfotext)).perform(replaceText("Ich bin ein bearbeiteter WG Info Text aus dem Unit Test :)"))
        Thread.sleep(500)
        onView(withId(R.id.button_wginfotext_bestaetigen)).perform(click())
        Thread.sleep(500)
    }
}