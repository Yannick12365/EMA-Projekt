package com.example.ema_projekt.hottopics

import android.content.Context
import android.widget.Toast
import com.example.ema_projekt.DatabaseManager
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

    //HotTopic in Datenbank schreiben
    fun writeHotTopicDatabase(data: HotTopicsData, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("HotTopic").child(data.id.toString()).child("Text").setValue(data.text)
    }

    //HotTopics aus Datenbank auslesen
    suspend fun readHotTopicDatabase(context: Context):MutableList<HotTopicsData>{
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
                                        HotTopicKommentarData(kommentar.key.toString().toInt(), kommentar.value.toString())
                                    )
                                }
                            }
                            list.add(HotTopicsData(data.key.toString().toInt(), data.child("Text").value.toString(), kommentarListe))
                        }
                        value.resume(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Ups, da ist etwas schief gelaufen!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    //HotTopic aus Datenbank entfernen
    fun deleteHotTopicDatabaseItem(id: Int, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("HotTopic").child(id.toString()).removeValue()
    }

    //HotTopic Kommentar zu HotTopic in Datenbak hinzufuegen
    fun writeKommentar(idTopic:Int, data:HotTopicKommentarData, context: Context){
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("HotTopic").child(idTopic.toString()).child("Kommentare").child(data.id.toString()).setValue(data.text)
    }

    //HotTopic Kommentar bei HotTopic in Datenbak entfernen
    fun deleteKommentar(idTopic:Int, id: Int, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("HotTopic").child(idTopic.toString()).child("Kommentare").child(id.toString()).removeValue()
    }
}


class HotTopicsJSON{
    //---------------------------------------------------------
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    //Teil zum reinschreiben der Funktion writeHotTopicJSON von StackOverflow siehe Link

    //JSON Datei Inhalt reinschreiben
    fun writeHotTopicJSON(data: List<HotTopicsData>, context: Context) {
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
    //---------------------------------------------------------

    //---------------------------------------------------------
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    //Teil zum reinschreiben der Funktion addHotTopicJSON von StackOverflow sieh Link

    //Inhalt in JSON Datei hinzufuegen
    fun addHotTopicJSON(data: HotTopicsData, context: Context) {
        val existingJson = readHotTopicJSON(context)
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
    //---------------------------------------------------------

    //---------------------------------------------------------
    //https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
    //Code der Funktion readHotTopicJSON von aus dem Internet siehe Link (AUf der Seite zu finden unter "2. Read Data From JSON FIle :-")

    //JSON Datei Inhalt auslesen
    fun readHotTopicJSON(context: Context): JSONArray {
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
    //---------------------------------------------------------

    //---------------------------------------------------------
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    //Teil zum reinschreiben der Funktion deleteHotTopicJSONItem von StackOverflow sieh Link

    //Item in JSON Datei entfernen
    fun deleteHotTopicJSONItem(nr: Int, context: Context) {
        val jsonArray = readHotTopicJSON(context)
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
    //---------------------------------------------------------

    //---------------------------------------------------------
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    //Teil zum reinschreiben der Funktion writeKommentarJSON von StackOverflow sieh Link

    //Kommentar zu Item in JSON Datei hinzufuegen
    fun writeKommentarJSON(idTopic:Int, data:HotTopicKommentarData, context: Context){
        val existingJson = readHotTopicJSON(context)
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
    //---------------------------------------------------------

    //---------------------------------------------------------
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    //Teil zum reinschreiben der Funktion deleteKommentarJSON von StackOverflow sieh Link

    //Kommentar bei Item in JSON Datei entfernen
    fun deleteKommentarJSON(idTopic:Int, id: Int, context: Context) {
        val existingJson = readHotTopicJSON(context)
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
    //---------------------------------------------------------
}

