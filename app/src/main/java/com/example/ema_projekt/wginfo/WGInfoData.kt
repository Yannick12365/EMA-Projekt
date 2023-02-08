package com.example.ema_projekt.wginfo

import android.content.Context
import android.widget.Toast
import com.example.ema_projekt.DatabaseManager
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.*
import org.json.JSONObject
import java.io.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WGInfoData {
    private val database: DatabaseReference = DatabaseManager().getDatabaseReference()

    //WG Info Text in Datenbank abspeichern
    fun writeWGInfoDatabase(text: String, context: Context){
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("WGInfo").setValue(text)
    }

    //WG Info Text aus Datenbank auslesen
    suspend fun readWGInfoDatabase(context: Context):String{
        return suspendCoroutine { value ->
            val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
            database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("WGInfo")) {
                        value.resume(snapshot.child("WGInfo").value.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Ups, da ist etwas schief gelaufen!",
                        Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

class WGInfoJSON{
    //---------------------------------------------------------
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    //Teil zum reinschreiben der Funktion writeWGInfoJSON von StackOverflow siehe Link

    //JSON Datei Inhalt reinschreiben
    fun writeWGInfoJSON(text: String, context: Context) {
        val file = FileWriter("/data/data/" + context.packageName + "/" + "WGInfo.json")

        val jsonObj = JSONObject()
        jsonObj.put("text",text)

        file.write(jsonObj.toString())
        file.flush()
        file.close()
    }
    //---------------------------------------------------------

    //---------------------------------------------------------
    //https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
    //Code der Funktion readWGInfoJSON von aus dem Internet siehe Link (AUf der Seite zu finden unter "2. Read Data From JSON FIle :-")

    //JSON Datei Inhalt auslesen
    fun readWGInfoJSON(context: Context): String {
        val file = File("/data/data/" + context.packageName + "/" + "WGInfo.json")
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

            val jsonObject = JSONObject(stringBuilder.toString())

            return jsonObject.getString("text")
        } catch (e: FileNotFoundException) {
            return ""
        }
    }
    //---------------------------------------------------------
}
