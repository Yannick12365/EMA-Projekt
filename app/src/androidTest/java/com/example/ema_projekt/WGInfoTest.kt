package com.example.ema_projekt

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
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

    }

    @Test
    fun editWGInfoTextCheck(){

    }
}