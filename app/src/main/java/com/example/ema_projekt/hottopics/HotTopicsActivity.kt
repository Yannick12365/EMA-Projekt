package com.example.ema_projekt.hottopics

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import com.example.ema_projekt.R

class HotTopicsActivity : AppCompatActivity() {
    private lateinit var zurueck: ImageButton
    private lateinit var hinzufuegen: Button
    private lateinit var input: EditText
    private lateinit var itemLayout: LinearLayout
    lateinit var imageView: ImageView

    private val itemList = mutableMapOf<Int, View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_hot_topics)

        zurueck = findViewById(R.id.imageButton_hot_topics_zurueck)
        hinzufuegen = findViewById(R.id.button)
        input = findViewById(R.id.editText)
        itemLayout = findViewById(R.id.hotTopicItemLayout)

        zurueck.setOnClickListener {
            zurueck.setBackgroundResource(R.drawable.zurueckklick)
            this.finish()
        }
        hinzufuegen.setOnClickListener {
            if (input.text.isNotEmpty()) {
                val id = nextId()
                val itemView = createHotTopic(input.text.toString())

                itemList[id] = itemView
                itemLayout.addView(itemView)

                input.setText("")
            } else{
                Toast.makeText(applicationContext, "Gebe einen Text ein!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createHotTopic(text: String):View {
        val viewItem: View = View.inflate(this, R.layout.item_hot_topics, null)
        val textBox: TextView = viewItem.findViewById(R.id.hot_topic_text)
        textBox.text = text

        val button: ImageButton = viewItem.findViewById(R.id.hot_topic_loeschen)
        button.setOnClickListener {
            var id: Int = -1
            for (i in itemList.keys) {
                if (itemList[i] == viewItem) {
                    id = i
                    break
                }
            }
            itemLayout.removeView(viewItem)
            itemList.remove(id)
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
}