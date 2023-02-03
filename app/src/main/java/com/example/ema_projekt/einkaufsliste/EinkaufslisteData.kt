package com.example.ema_projekt.einkaufsliste

import android.content.Context
import android.widget.Toast
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.concurrent.CountDownLatch


data class EinkaufslisteData(
    var itemId: Int?,
    var itemText: String,
)

class EinkauflisteDataBase{
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://ema-projekt-e036e-default-rtdb.europe-west1.firebasedatabase.app/").reference

    fun writeDatabase(data: EinkaufslisteData, context: Context){
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(wgName)) {
                    database.child(wgName).child("Einkaufsliste")
                        .child(data.itemId.toString()).setValue(data.itemText)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Ups, da ist etwas schief gelaufen!",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun readDatabase(context: Context) {

    }
}

class EinkaufslisteJSON() {
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    fun writeJSON(data: EinkaufslisteData, context: Context) {
        val existingJson = readJSON(context)
        val file = FileWriter("/data/data/" + context.packageName + "/" + "einkaufsliste.json")

        val arrayJson = JSONArray()

        for (i in 0 until existingJson.length()) {
            arrayJson.put(existingJson[i])
        }

        val objJson = JSONObject()
        objJson.put("itemId", data.itemId)
        objJson.put("itemText", data.itemText)

        arrayJson.put(objJson)
        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }

    //https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
    fun readJSON(context: Context): JSONArray {
        val file = File("/data/data/" + context.packageName + "/" + "einkaufsliste.json")
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

            return JSONArray(stringBuilder.toString())
        } catch (e: FileNotFoundException) {
            return JSONArray()
        }
    }

    fun deleteJSONItem(nr: Int, context: Context) {
        val jsonArray = readJSON(context)
        val newJSONArray = JSONArray()

        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getJSONObject(i).get("itemId") != nr){
                val objJson = JSONObject()
                objJson.put("itemId", jsonArray.getJSONObject(i).get("itemId"));
                objJson.put("itemText", jsonArray.getJSONObject(i).get("itemText"));

                newJSONArray.put(objJson)
            }
        }
        val fileWrite = FileWriter("/data/data/" + context.packageName + "/" + "einkaufsliste.json")

        fileWrite.write(newJSONArray.toString())
        fileWrite.flush()
        fileWrite.close()
    }
}
