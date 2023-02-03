package com.example.ema_projekt.wginfo

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.ema_projekt.einkaufsliste.EinkaufslisteData
import com.example.ema_projekt.wgplaner.LoginData
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.*
import org.json.JSONObject
import java.io.*

class WGInfoJSON {
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://ema-projekt-e036e-default-rtdb.europe-west1.firebasedatabase.app/").reference

    fun writeDatabase(text: String, context: Context){
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                database.child(wgName).child("WGInfo").setValue(text)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DEBUG", error.message)
                Toast.makeText(context, "Ups, da ist etwas schief gelaufen!",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }



    fun writeJSON(text:String, context: Context) {
        val objJson = JSONObject()
        val loginData = LoginDataSettingsJSON().readLoginDataJSON(context)

        objJson.put("wgName", loginData.wgName)
        objJson.put("wgToken", loginData.wgToken)
        objJson.put("wgInfo",text)

        val file = FileWriter("/data/data/" + context.packageName + "/" + "settings.json")

        file.write(objJson.toString())
        file.flush()
        file.close()
    }

    fun readJSON(context: Context): String {
        val file = File("/data/data/" + context.packageName + "/" + "settings.json")
        try {
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line = bufferedReader.readLine()

            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()
            val obj = JSONObject(stringBuilder.toString())

            return obj.getString("wgInfo")
        } catch (e: FileNotFoundException) {
            return ""
        }
    }
}