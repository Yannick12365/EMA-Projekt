package com.example.ema_projekt.einkaufsliste

import android.content.Context
import android.widget.Toast
import com.example.ema_projekt.DatabaseManager
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


data class EinkaufslisteData(
    var itemId: Int,
    var itemText: String,
)

class EinkauflisteDataBase {
    private val database: DatabaseReference = DatabaseManager().getDatabaseReference()

    //Einkaufslisten Item in Datenbank schreiben
    fun writeEinkaufslisteDatabase(data: EinkaufslisteData, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Einkaufsliste").child(data.itemId.toString()).setValue(data.itemText)
    }

    //Einkaufslistenitems aus Datenbank auslesen
    suspend fun readEinkaufslisteDatabase(context: Context):MutableList<EinkaufslisteData>{
        return suspendCoroutine { value ->
            val list = mutableListOf<EinkaufslisteData>()
            val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
            database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("Einkaufsliste")) {
                        for (data in snapshot.child("Einkaufsliste").children) {
                            list.add(EinkaufslisteData(data.key.toString().toInt(), data.value.toString()))
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

    //Einkaufslistenitem in Datenbank entfernen
    fun deleteEinkaufslisteDatabaseItem(id: Int?, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Einkaufsliste").child(id.toString()).removeValue()
    }
}

class EinkaufslisteJSON{
    //---------------------------------------------------------
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    //Teil zum reinschreiben der Funktion writeEinkaufslisteJSON von StackOverflow siehe Link

    //JSON Datei Inhalt reinschreiben
    fun writeEinkaufslisteJSON(data: List<EinkaufslisteData>, context: Context) {
        val file = FileWriter("/data/data/" + context.packageName + "/" + "Einkaufsliste.json")

        val arrayJson = JSONArray()

        for (i in data){
            val objJson = JSONObject()
            objJson.put("itemId", i.itemId)
            objJson.put("itemText", i.itemText)
            arrayJson.put(objJson)
        }

        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }
    //---------------------------------------------------------

    //---------------------------------------------------------
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    //Teil zum reinschreiben der Funktion addEinkaufslisteJSON von StackOverflow siehe Link

    //JSON Datei Item hinzufuegen
    fun addEinkaufslisteJSON(data: EinkaufslisteData, context: Context) {
        val existingJson = readEinkaufslisteJSON(context)
        val file = FileWriter("/data/data/" + context.packageName + "/" + "Einkaufsliste.json")

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
    //---------------------------------------------------------

    //---------------------------------------------------------
    //https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
    //Code der Funktion readEinkaufslisteJSON von aus dem Internet siehe Link (AUf der Seite zu finden unter "2. Read Data From JSON FIle :-")

    //JSON Datei Inhalt auslesen
    fun readEinkaufslisteJSON(context: Context): JSONArray {
        val file = File("/data/data/" + context.packageName + "/" + "Einkaufsliste.json")
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
    //Teil zum reinschreiben der Funktion deleteEinkaufslisteJSONItem von StackOverflow siehe Link

    //JSON Datei Item hinzufuegen
    fun deleteEinkaufslisteJSONItem(nr: Int, context: Context) {
        val jsonArray = readEinkaufslisteJSON(context)
        val newJSONArray = JSONArray()

        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getJSONObject(i).get("itemId") != nr) {
                val objJson = JSONObject()
                objJson.put("itemId", jsonArray.getJSONObject(i).get("itemId"));
                objJson.put("itemText", jsonArray.getJSONObject(i).get("itemText"));

                newJSONArray.put(objJson)
            }
        }
        val fileWrite =
            FileWriter("/data/data/" + context.packageName + "/" + "Einkaufsliste.json")

        fileWrite.write(newJSONArray.toString())
        fileWrite.flush()
        fileWrite.close()
    }
    //---------------------------------------------------------
}
