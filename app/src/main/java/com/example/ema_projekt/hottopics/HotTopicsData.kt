package com.example.ema_projekt.hottopics

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.ema_projekt.DatabaseManager
import com.example.ema_projekt.einkaufsliste.EinkaufslisteData
import com.example.ema_projekt.vorratskammer.VorratskammerData
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


data class HotTopicsData(
    var id: Int,
    var text: String,
    var kommentare: MutableList<HotTopicKommentarData>
)

data class HotTopicKommentarData(
    var id:Int,
    var text:String
)

class HotTopicDatabase{
    private val database: DatabaseReference = DatabaseManager().getDatabaseReference()

    fun writeDatabase(data: HotTopicsData, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("HotTopic").child(data.id.toString()).child("Text").setValue(data.text)
    }

    suspend fun readDatabase(context: Context):MutableList<HotTopicsData>{
        return suspendCoroutine { value ->
            val list = mutableListOf<HotTopicsData>()
            val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
            database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("HotTopic")) {
                        for (data in snapshot.child("HotTopic").children) {
                            val kommentarListe = mutableListOf<HotTopicKommentarData>()

                            if (snapshot.child("HotTopic").child(data.key.toString()).hasChild("Kommentare")) {
                                for (kommentar in snapshot.child("HotTopic")
                                    .child(data.key.toString()).child("Kommentare").children) {
                                    kommentarListe.add(
                                        HotTopicKommentarData(
                                            kommentar.key.toString().toInt(),
                                            kommentar.value.toString()
                                        )
                                    )
                                }
                            }

                            list.add(HotTopicsData(
                                data.key.toString().toInt(),
                                data.child("Text").value.toString(),
                                kommentarListe))
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
        database.child(wgName).child("HotTopic").child(id.toString()).removeValue()
    }

    fun writeKommentar(idTopic:Int, data:HotTopicKommentarData, context: Context){
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("HotTopic").child(idTopic.toString()).child("Kommentare").child(data.id.toString()).setValue(data.text)
    }

    fun deleteDatabaseItemKommentar(idTopic:Int,id: Int, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("HotTopic").child(idTopic.toString()).child("Kommentare").child(id.toString()).removeValue()
    }
}


class HotTopicsJSON(){
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    fun writeJSON(data: List<HotTopicsData>, context: Context) {
        val file = FileWriter("/data/data/" + context.packageName + "/" + "HotTopics.json")

        val arrayJson = JSONArray()

        for (i in data){
            val objJson = JSONObject()
            objJson.put("id", i.id)
            objJson.put("text", i.text)
            val arr = JSONArray()
            for (j in i.kommentare){
                val jasnonobj = JSONObject()
                jasnonobj.put("id",j.id)
                jasnonobj.put("text",j.text)
                arr.put(jasnonobj)
            }
            objJson.put("kommentare",arr)
            arrayJson.put(objJson)
        }

        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }

    fun addJSON(data: HotTopicsData, context: Context) {
        val existingJson = readJSON(context)
        val file = FileWriter("/data/data/" + context.packageName + "/" + "HotTopics.json")

        val arrayJson = JSONArray()

        for (i in 0 until existingJson.length()) {
            arrayJson.put(existingJson[i])
        }

        val objJson = JSONObject()
        objJson.put("id", data.id)
        objJson.put("text", data.text)
        val arr = JSONArray()
        for (i in data.kommentare){
            val jasnonobj = JSONObject()
            jasnonobj.put("id",i.id)
            jasnonobj.put("text",i.text)
            arr.put(jasnonobj)
        }
        objJson.put("kommentare",arr)
        arrayJson.put(objJson)
        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }

    //https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
    fun readJSON(context: Context): JSONArray {
        val file = File("/data/data/" + context.packageName + "/" + "HotTopics.json")
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
                objJson.put("id", jsonArray.getJSONObject(i).get("id"))
                objJson.put("text", jsonArray.getJSONObject(i).get("text"))
                objJson.put("kommentare",jsonArray.getJSONObject(i).getJSONArray("kommentare"))

                newJSONArray.put(objJson)
            }
        }
        val fileWrite = FileWriter("/data/data/" + context.packageName + "/" + "HotTopics.json")

        fileWrite.write(newJSONArray.toString())
        fileWrite.flush()
        fileWrite.close()
    }

    fun writeKommentar(idTopic:Int, data:HotTopicKommentarData, context: Context){
        val existingJson = readJSON(context)
        val file = FileWriter("/data/data/" + context.packageName + "/" + "HotTopics.json")

        val arrayJson = JSONArray()

        for (i in 0 until existingJson.length()) {
            if (existingJson.getJSONObject(i).get("id") == idTopic) {
                val objJson = JSONObject()
                objJson.put("id", existingJson.getJSONObject(i).get("id"))
                objJson.put("text", existingJson.getJSONObject(i).get("text"))

                val arr = existingJson.getJSONObject(i).getJSONArray("kommentare")
                val obj = JSONObject()
                obj.put("id",data.id)
                obj.put("text",data.text)
                arr.put(obj)

                objJson.put("kommentare",arr)

                arrayJson.put(objJson)
            } else {
                val objJson = JSONObject()
                objJson.put("id", existingJson.getJSONObject(i).get("id"))
                objJson.put("text", existingJson.getJSONObject(i).get("text"))
                objJson.put("kommentare",existingJson.getJSONObject(i).getJSONArray("kommentare"))

                arrayJson.put(objJson)
            }
        }

        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }

    fun deleteJSONItemKommentar(idTopic:Int,id: Int, context: Context) {
        val existingJson = readJSON(context)
        val file = FileWriter("/data/data/" + context.packageName + "/" + "HotTopics.json")

        val arrayJson = JSONArray()

        for (i in 0 until existingJson.length()) {
            if (existingJson.getJSONObject(i).get("id") == idTopic) {
                val objJson = JSONObject()
                objJson.put("id", existingJson.getJSONObject(i).get("id"))
                objJson.put("text", existingJson.getJSONObject(i).get("text"))


                val arr = existingJson.getJSONObject(i).getJSONArray("kommentare")
                val kommentare = JSONArray()

                for(j in 0 until arr.length()){
                    if (arr.getJSONObject(j).getInt("id") != id){
                        kommentare.put(arr.getJSONObject(j))
                    }
                }

                objJson.put("kommentare",kommentare)

                arrayJson.put(objJson)
            } else {
                val objJson = JSONObject()
                objJson.put("id", existingJson.getJSONObject(i).get("id"))
                objJson.put("text", existingJson.getJSONObject(i).get("text"))
                objJson.put("kommentare",existingJson.getJSONObject(i).getJSONArray("kommentare"))

                arrayJson.put(objJson)
            }
        }

        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }
}

