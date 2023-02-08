package com.example.ema_projekt.putzplan

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

data class PutzPlanData(
    var id:Int,
    var person:String,
    var aufgabe:String,
    var zeitInterval:String
)


class PutzPlanDatabase {
    private val database: DatabaseReference = DatabaseManager().getDatabaseReference()

    fun writeDatabase(data: PutzPlanData, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("PutzPlan").child(data.id.toString()).child("Person").setValue(data.person)
        database.child(wgName).child("PutzPlan").child(data.id.toString()).child("Aufgabe").setValue(data.aufgabe)
        database.child(wgName).child("PutzPlan").child(data.id.toString()).child("ZeitInterval").setValue(data.zeitInterval)
    }

    suspend fun readDatabase(context: Context):MutableList<PutzPlanData>{
        return suspendCoroutine { value ->
            val list = mutableListOf<PutzPlanData>()
            val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
            database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("PutzPlan")) {
                        for (data in snapshot.child("PutzPlan").children) {
                            list.add(
                                PutzPlanData(data.key.toString().toInt(),
                                data.child("Person").value.toString(),
                                data.child("Aufgabe").value.toString(),
                                data.child("ZeitInterval").value.toString()))
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

    fun deleteDatabaseItem(id: Int?, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("PutzPlan").child(id.toString()).removeValue()
    }


    fun editDatabaseAufgabe(data: PutzPlanData, context: Context){
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("PutzPlan").child(data.id.toString()).child("Aufgabe").setValue(data.aufgabe)
    }


    fun editDatabaseZeitInterval(data: PutzPlanData, context: Context){
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("PutzPlan").child(data.id.toString()).child("ZeitInterval").setValue(data.zeitInterval)
    }
}


class PutzPlanJSON(){
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    fun writePutzPlanJSON(data: List<PutzPlanData>, context: Context) {
        val file = FileWriter("/data/data/" + context.packageName + "/" + "PutzPlan.json")

        val arrayJson = JSONArray()

        for (i in data){
            val objJson = JSONObject()
            objJson.put("id", i.id)
            objJson.put("person", i.person)
            objJson.put("aufgabe", i.aufgabe)
            objJson.put("zeitInterval",i.zeitInterval)
            arrayJson.put(objJson)
        }

        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }

    fun addPutzPlanJSON(data: PutzPlanData, context: Context) {
        val existingJson = readPutzPlanJSON(context)
        val file = FileWriter("/data/data/" + context.packageName + "/" + "PutzPlan.json")

        val arrayJson = JSONArray()

        for (i in 0 until existingJson.length()) {
            arrayJson.put(existingJson[i])
        }

        val objJson = JSONObject()
        objJson.put("id", data.id)
        objJson.put("person", data.person)
        objJson.put("aufgabe", data.aufgabe)
        objJson.put("zeitInterval",data.zeitInterval)

        arrayJson.put(objJson)
        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }

    //https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
    fun readPutzPlanJSON(context: Context): JSONArray {
        val file = File("/data/data/" + context.packageName + "/" + "PutzPlan.json")
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


    fun deletePutzPlanJSONItem(nr: Int, context: Context) {
        val jsonArray = readPutzPlanJSON(context)
        val newJSONArray = JSONArray()

        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getJSONObject(i).get("id") != nr) {
                val objJson = JSONObject()
                objJson.put("id", jsonArray.getJSONObject(i).get("id"))
                objJson.put("person", jsonArray.getJSONObject(i).get("person"))
                objJson.put("aufgabe", jsonArray.getJSONObject(i).get("aufgabe"))
                objJson.put("zeitInterval",jsonArray.getJSONObject(i).get("zeitInterval"))

                newJSONArray.put(objJson)
            }
        }
        val fileWrite =
            FileWriter("/data/data/" + context.packageName + "/" + "PutzPlan.json")

        fileWrite.write(newJSONArray.toString())
        fileWrite.flush()
        fileWrite.close()
    }

    fun editAufgabePutzPlanJSONItem(nr: Int, context: Context, aufgabe:String) {
        val jsonArray = readPutzPlanJSON(context)
        val newJSONArray = JSONArray()

        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getJSONObject(i).get("id") == nr) {
                val objJson = JSONObject()
                objJson.put("id", jsonArray.getJSONObject(i).get("id"))
                objJson.put("person", jsonArray.getJSONObject(i).get("person"))
                objJson.put("aufgabe", aufgabe)
                objJson.put("zeitInterval",jsonArray.getJSONObject(i).get("zeitInterval"))

                newJSONArray.put(objJson)
            } else {
                newJSONArray.put(jsonArray.getJSONObject(i))
            }
        }
        val fileWrite =
            FileWriter("/data/data/" + context.packageName + "/" + "PutzPlan.json")

        fileWrite.write(newJSONArray.toString())
        fileWrite.flush()
        fileWrite.close()
    }

    fun editZeitIntervalPutzPlanJSONItem(nr: Int, context: Context, zeitInterval:String) {
        val jsonArray = readPutzPlanJSON(context)
        val newJSONArray = JSONArray()

        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getJSONObject(i).get("id") == nr) {
                val objJson = JSONObject()
                objJson.put("id", jsonArray.getJSONObject(i).get("id"))
                objJson.put("person", jsonArray.getJSONObject(i).get("person"))
                objJson.put("aufgabe", jsonArray.getJSONObject(i).get("aufgabe"))
                objJson.put("zeitInterval",zeitInterval)

                newJSONArray.put(objJson)
            } else {
                newJSONArray.put(jsonArray.getJSONObject(i))
            }
        }
        val fileWrite =
            FileWriter("/data/data/" + context.packageName + "/" + "PutzPlan.json")

        fileWrite.write(newJSONArray.toString())
        fileWrite.flush()
        fileWrite.close()
    }
}


