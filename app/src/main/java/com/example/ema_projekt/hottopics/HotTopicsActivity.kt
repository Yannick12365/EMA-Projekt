package com.example.ema_projekt.hottopics

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ema_projekt.ConnectionManager
import com.example.ema_projekt.R
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

        //Activity Felder holen
        zurueck = findViewById(R.id.imageButton_hot_topics_zurueck)
        hinzufuegen = findViewById(R.id.button_hot_topics_hinzufügen)
        input = findViewById(R.id.editText_was_steht_an)
        itemLayout = findViewById(R.id.hotTopicItemLayout)

        //ConnectionManager einstellen
        val conManager = ConnectionManager()
        conManager.setOjects(false, this)
        conManager.switchScreen(this)

        //Warten auf das Auslesen der Datenbank
        var list = mutableListOf<HotTopicsData>()
        GlobalScope.launch(Dispatchers.Main) {
            list = HotTopicDatabase().readHotTopicDatabase(applicationContext)
            HotTopicsJSON().writeHotTopicJSON(list,applicationContext)

            for(data in list){
                val itemView = createHotTopic(data)
                itemList[data.id] = itemView
                itemLayout.addView(itemView)
            }
        }
        //Wenn kein Internet vorhanden Inhalt der JSON Datei nutzen
        if (!conManager.checkConnection(this)){
            val hottopicJSONarray = HotTopicsJSON().readHotTopicJSON(applicationContext)
            for (i in 0 until hottopicJSONarray.length()) {
                val jsonarr = hottopicJSONarray.getJSONObject(i).getJSONArray("kommentare")
                val list2 = mutableListOf<HotTopicKommentarData>()
                for (j in 0 until jsonarr.length()) {
                    list2.add(HotTopicKommentarData(
                        jsonarr.getJSONObject(j).getInt("id"),
                        jsonarr.getJSONObject(j).getString("text")))
                }
                list.add(HotTopicsData(
                    hottopicJSONarray.getJSONObject(i).getInt("id"),
                    hottopicJSONarray.getJSONObject(i).getString("text"),
                    list2))
            }
            for(data in list){
                val itemView = createHotTopic(data)
                itemList[data.id] = itemView
                itemLayout.addView(itemView)
            }
        }

        //Klick Eventlistener
        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }
        hinzufuegen.setOnClickListener {
            if (input.text.isNotEmpty()) {
                val id = nextId()
                val data = HotTopicsData(id,input.text.toString(), mutableListOf())
                HotTopicDatabase().writeHotTopicDatabase(data,applicationContext)
                HotTopicsJSON().addHotTopicJSON(data,applicationContext)
                val itemView = createHotTopic(data)

                itemList[id] = itemView
                itemLayout.addView(itemView)

                input.setText("")
            } else{
                Toast.makeText(applicationContext, "Gebe einen Text ein!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //HotTopic Item erstellen
    private fun createHotTopic(data:HotTopicsData):View {
        //Item Items holen
        val viewItem: View = View.inflate(this, R.layout.item_hot_topics, null)
        val textBox: TextView = viewItem.findViewById(R.id.hot_topic_text)
        textBox.text = data.text
        val button: ImageButton = viewItem.findViewById(R.id.hot_topic_loeschen)

        //Klick Eventlistener
        button.setOnClickListener {
            itemLayout.removeView(viewItem)
            itemList.remove(data.id)
            HotTopicDatabase().deleteHotTopicDatabaseItem(data.id,applicationContext)
            HotTopicsJSON().deleteHotTopicJSONItem(data.id,applicationContext)
        }

        viewItem.setOnClickListener {
            popUpKommentare(data)
        }

        return viewItem
    }

    //Kommentar Popup
    private fun popUpKommentare(data:HotTopicsData){
        val kommentarPopUp = Dialog(this)

        kommentarPopUp.setContentView(R.layout.popup_hottopickommentar)
        kommentarPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //PopUp Items holen
        val linearLayoutKommentare: LinearLayout = kommentarPopUp.findViewById(R.id.linearlayout_kommentare)
        val popupzurueck: ImageButton = kommentarPopUp.findViewById(R.id.imageButton_hottopic_kommentar_zurueck)
        val popupabbrechen: Button = kommentarPopUp.findViewById(R.id.button_hottopickommentare_abbrechen)
        val popuphinzufuegen: Button = kommentarPopUp.findViewById(R.id.button_hottopickommentare_hinzufuegen)
        val editTextKommentar: EditText = kommentarPopUp.findViewById(R.id.editText_kommentar)

        //Klick Eventlistener
        popupzurueck.setOnClickListener {
            kommentarPopUp.dismiss()
        }
        popupabbrechen.setOnClickListener {
            kommentarPopUp.dismiss()
        }

        popuphinzufuegen.setOnClickListener {
            if (editTextKommentar.text.isNotEmpty()) {
                val id: Int = nextIdKommentar(data)
                val kommentar = HotTopicKommentarData(id, editTextKommentar.text.toString())
                data.kommentare.add(kommentar)
                HotTopicDatabase().writeKommentar(data.id, kommentar, applicationContext)
                HotTopicsJSON().writeKommentarJSON(data.id, kommentar, applicationContext)
                linearLayoutKommentare.addView(createKommentar(linearLayoutKommentare, editTextKommentar.text.toString(), id, data))
                editTextKommentar.setText("")
            } else {
                Toast.makeText(applicationContext, "Gebe einen Kommentar Text ein!",
                    Toast.LENGTH_SHORT).show()
            }
        }

        for (i in data.kommentare){
            linearLayoutKommentare.addView(createKommentar(linearLayoutKommentare,i.text,i.id,data))
        }

        kommentarPopUp.show()
    }

    //Kommentar erstellen
    private fun createKommentar(linearLayout:LinearLayout, text:String, id:Int, topic:HotTopicsData):View{
        //Item Items holen
        val viewItem: View = View.inflate(this, R.layout.item_hottopic_kommentar, null)
        val loeschen:ImageButton = viewItem.findViewById(R.id.button_loeschen_hottopickommentar)
        val kommentartext: TextView = viewItem.findViewById(R.id.itemTextView_hottopic_kommentar)

        kommentartext.text = text

        //Klick Eventlistener
        loeschen.setOnClickListener {
            HotTopicDatabase().deleteKommentar(topic.id,id,applicationContext)
            HotTopicsJSON().deleteKommentarJSON(topic.id,id,applicationContext)
            linearLayout.removeView(viewItem)

            for (kommentar in topic.kommentare){
                if (kommentar.id == id){
                    topic.kommentare.remove(kommentar)
                }
            }
        }

        return viewItem
    }

    //ID für Item bestimmen
    private fun nextId():Int{
        var newId = 0
        for (i in itemList.keys){
            if (i >= newId){
                newId = i+1
            }
        }
        return newId
    }

    //ID für Kommentar bestimmen
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

