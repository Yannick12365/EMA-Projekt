package com.example.ema_projekt.vorratskammer

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.ema_projekt.DatabaseManager
import com.example.ema_projekt.einkaufsliste.EinkaufslisteData
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class VorratskammerData(
    var text:String,
    var id:Int
)

class VorratskammerDatabase(){
    private val database: DatabaseReference = DatabaseManager().getDatabaseReference()

    fun writeDatabase(data: VorratskammerData, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Vorratskammer")
            .child(data.id.toString()).setValue(data.text)
    }

    suspend fun readDatabase(context: Context):MutableList<VorratskammerData>{
        return suspendCoroutine { value ->
            val list = mutableListOf<VorratskammerData>()
            val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
            database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("Vorratskammer")) {
                        for (data in snapshot.child("Vorratskammer").children) {
                            list.add(VorratskammerData(data.value.toString(),data.key.toString().toInt()))
                        }
                        value.resume(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context, "Ups, da ist etwas schief gelaufen!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    fun deleteDatabaseItem(id: Int, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Vorratskammer").child(id.toString()).removeValue()
    }
}


class VorratskammerJSON(){
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    fun writeJSON(data: List<VorratskammerData>, context: Context) {
        val file = FileWriter("/data/data/" + context.packageName + "/" + "Vorratskammer.json")

        val arrayJson = JSONArray()
        for (i in data){
            val objJson = JSONObject()
            objJson.put("id", i.id)
            objJson.put("text", i.text)
            arrayJson.put(objJson)
        }

        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }

    fun addJSON(data: VorratskammerData, context: Context) {
        val existingJson = readJSON(context)
        val file = FileWriter("/data/data/" + context.packageName + "/" + "Vorratskammer.json")

        val arrayJson = JSONArray()

        for (i in 0 until existingJson.length()) {
            arrayJson.put(existingJson[i])
        }

        val objJson = JSONObject()
        objJson.put("id", data.id)
        objJson.put("text", data.text)

        arrayJson.put(objJson)
        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }

    //https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
    fun readJSON(context: Context): JSONArray {
        val file = File("/data/data/" + context.packageName + "/" + "Vorratskammer.json")
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
            if (jsonArray.getJSONObject(i).get("id") != nr) {
                val objJson = JSONObject()
                objJson.put("id", jsonArray.getJSONObject(i).get("id"));
                objJson.put("text", jsonArray.getJSONObject(i).get("text"));

                newJSONArray.put(objJson)
            }
        }
        val fileWrite =
            FileWriter("/data/data/" + context.packageName + "/" + "Vorratskammer.json")

        fileWrite.write(newJSONArray.toString())
        fileWrite.flush()
        fileWrite.close()
    }
}
