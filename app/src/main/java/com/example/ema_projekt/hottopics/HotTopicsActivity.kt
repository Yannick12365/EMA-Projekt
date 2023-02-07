package com.example.ema_projekt.hottopics

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import com.example.ema_projekt.ConnectionManager
import com.example.ema_projekt.R
import com.example.ema_projekt.einkaufsliste.EinkauflisteDataBase
import com.example.ema_projekt.vorratskammer.VorratskammerDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HotTopicsActivity : AppCompatActivity() {
    private lateinit var zurueck: ImageButton
    private lateinit var hinzufuegen: Button
    private lateinit var input: EditText
    private lateinit var itemLayout: LinearLayout

    private val itemList = mutableMapOf<Int, View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_hot_topics)

        zurueck = findViewById(R.id.imageButton_hot_topics_zurueck)
        hinzufuegen = findViewById(R.id.button)
        input = findViewById(R.id.editText)
        itemLayout = findViewById(R.id.hotTopicItemLayout)

        val conManager = ConnectionManager()
        conManager.setOjects(false, this)
        conManager.switchScreen(this)

        GlobalScope.launch(Dispatchers.Main) {
            val list = HotTopicDatabase().readDatabase(applicationContext)
            for(data in list){
                val itemView = createHotTopic(data)
                itemList[data.id] = itemView
                itemLayout.addView(itemView)
            }
        }

        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }
        hinzufuegen.setOnClickListener {
            if (input.text.isNotEmpty()) {
                val id = nextId()
                val data = HotTopicsData(id,input.text.toString(), mutableListOf())
                HotTopicDatabase().writeDatabase(data,applicationContext)
                val itemView = createHotTopic(data)

                itemList[id] = itemView
                itemLayout.addView(itemView)

                input.setText("")
            } else{
                Toast.makeText(applicationContext, "Gebe einen Text ein!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createHotTopic(data:HotTopicsData):View {
        val viewItem: View = View.inflate(this, R.layout.item_hot_topics, null)
        val textBox: TextView = viewItem.findViewById(R.id.hot_topic_text)
        textBox.text = data.text

        val button: ImageButton = viewItem.findViewById(R.id.hot_topic_loeschen)
        button.setOnClickListener {
            itemLayout.removeView(viewItem)
            itemList.remove(data.id)
            HotTopicDatabase().deleteDatabaseItem(data.id,applicationContext)
        }

        viewItem.setOnClickListener {
            popUpKommentare(data)
        }

        return viewItem
    }

    private fun popUpKommentare(data:HotTopicsData){
        val kommentarPopUp = Dialog(this)

        kommentarPopUp.setContentView(R.layout.popup_hottopickommentar)
        kommentarPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val linearLayoutKommentare: LinearLayout = kommentarPopUp.findViewById(R.id.linearlayout_kommentare)
        val popupzurueck: ImageButton = kommentarPopUp.findViewById(R.id.imageButton_hottopic_kommentar_zurueck)
        val popupabbrechen: Button = kommentarPopUp.findViewById(R.id.button_hottopickommentare_abbrechen)
        val popuphinzufuegen: Button = kommentarPopUp.findViewById(R.id.button_hottopickommentare_hinzufuegen)
        val editTextKommentar: EditText = kommentarPopUp.findViewById(R.id.editText_kommentar)

        popupzurueck.setOnClickListener {
            kommentarPopUp.dismiss()
        }
        popupabbrechen.setOnClickListener {
            kommentarPopUp.dismiss()
        }

        popuphinzufuegen.setOnClickListener {
            val id:Int = nextIdKommentar(data)
            val kommentar = HotTopicKommentarData(id,editTextKommentar.text.toString())
            data.kommentare.add(kommentar)
            HotTopicDatabase().writeKommentar(data.id,kommentar,applicationContext)
            linearLayoutKommentare.addView(createKommentar(linearLayoutKommentare,editTextKommentar.text.toString(),id,data.id))
            editTextKommentar.setText("")
        }

        for (i in data.kommentare){
            linearLayoutKommentare.addView(createKommentar(linearLayoutKommentare,i.text,i.id,data.id))
        }

        kommentarPopUp.show()
    }

    private fun createKommentar(linearLayout:LinearLayout, text:String, id:Int, topicId:Int):View{
        val viewItem: View = View.inflate(this, R.layout.item_hottopic_kommentar, null)
        val loeschen:ImageButton = viewItem.findViewById(R.id.button_loeschen_hottopickommentar)
        val kommentartext: TextView = viewItem.findViewById(R.id.itemTextView_hottopic_kommentar)

        kommentartext.text = text

        loeschen.setOnClickListener {
            HotTopicDatabase().deleteDatabaseItemKommentar(topicId,id,applicationContext)
            linearLayout.removeView(viewItem)
        }

        return viewItem
    }

    private fun nextId():Int{
        var newId = 0
        for (i in itemList.keys){
            if (i >= newId){
                newId = i+1
            }
        }
        return newId
    }
    private fun nextIdKommentar(data: HotTopicsData):Int{
        var newId = 0
        for (i in data.kommentare){
            if (i.id >= newId){
                newId = i.id+1
            }
        }
        return newId
    }
}