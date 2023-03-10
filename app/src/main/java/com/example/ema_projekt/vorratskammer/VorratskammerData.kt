package com.example.ema_projekt.vorratskammer

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

data class VorratskammerData(
    var text:String,
    var id:Int
)

class VorratskammerDatabase{
    private val database: DatabaseReference = DatabaseManager().getDatabaseReference()

    //Vorratskammer Eintrag aus Datenbank auslesen
    fun writeVorratskammerDatabase(data: VorratskammerData, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Vorratskammer")
            .child(data.id.toString()).setValue(data.text)
    }

    //Vorratskammer Eintrag in Datenbank schreiben
    suspend fun readVorratskammerDatabase(context: Context):MutableList<VorratskammerData>{
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

    //Vorratskammer Eintrag aus Datenbank entfernen
    fun deleteVorratskammerDatabaseItem(id: Int, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Vorratskammer").child(id.toString()).removeValue()
    }
}


class VorratskammerJSON{
    //---------------------------------------------------------
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    //Teil zum reinschreiben der Funktion writeVorratskammerJSON von StackOverflow siehe Link

    //JSON Datei Inhalt reinschreiben
    fun writeVorratskammerJSON(data: List<VorratskammerData>, context: Context) {
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
    //---------------------------------------------------------

    //---------------------------------------------------------
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    //Teil zum reinschreiben der Funktion addVorratskammerJSON von StackOverflow sieh Link

    //Inhalt in JSON Datei hinzufuegen
    fun addVorratskammerJSON(data: VorratskammerData, context: Context) {
        val existingJson = readVorratskammerJSON(context)
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
    //---------------------------------------------------------

    //---------------------------------------------------------
    //https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
    //Code der Funktion readVorratskammerJSON von aus dem Internet siehe Link (AUf der Seite zu finden unter "2. Read Data From JSON FIle :-")

    //JSON Datei Inhalt auslesen
    fun readVorratskammerJSON(context: Context): JSONArray {
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
    //---------------------------------------------------------

    //---------------------------------------------------------
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    //Teil zum reinschreiben der Funktion deleteJSONVorratskammerItem von StackOverflow sieh Link

    //Item aus JSON Datei entfernen
    fun deleteJSONVorratskammerItem(nr: Int, context: Context) {
        val jsonArray = readVorratskammerJSON(context)
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
    //---------------------------------------------------------
}
