package com.example.ema_projekt.kalender

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

data class KalenderEventData(
    val day:Int,
    val month:Int,
    val year:Int,
    var text:String,
    val dateStr:String,
    var id:Int)

class KalenderEventDatabase {
    private val database: DatabaseReference = DatabaseManager().getDatabaseReference()

    fun writeDatabase(data: KalenderEventData, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Kalender").child(data.id.toString()).child("Day")
            .setValue(data.day)
        database.child(wgName).child("Kalender").child(data.id.toString()).child("Month")
            .setValue(data.month)
        database.child(wgName).child("Kalender").child(data.id.toString()).child("Year")
            .setValue(data.year)
        database.child(wgName).child("Kalender").child(data.id.toString()).child("Text")
            .setValue(data.text)
        database.child(wgName).child("Kalender").child(data.id.toString()).child("DateStr")
            .setValue(data.dateStr)
    }

    fun deleteDatabaseItem(id: Int, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Kalender").child(id.toString()).removeValue()
    }

    suspend fun readDatabase(context: Context): MutableList<KalenderEventData> {
        return suspendCoroutine { value ->
            val list = mutableListOf<KalenderEventData>()
            val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
            database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.child("Kalender").children) {
                        val day: Int = data.child("Day").value.toString().toInt()
                        val month: Int = data.child("Month").value.toString().toInt()
                        val year: Int = data.child("Year").value.toString().toInt()
                        val text: String = data.child("Text").value.toString()
                        val dateStr: String = data.child("DateStr").value.toString()

                        list.add(
                            KalenderEventData(
                                day,
                                month,
                                year,
                                text,
                                dateStr,
                                data.key.toString().toInt()
                            )
                        )
                    }
                    value.resume(list)
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

    suspend fun removeOneYearOldEvents(day: Int, month: Int, year: Int, context: Context) {
        val listEvents = readDatabase(context)

        for (event in listEvents) {
            if (event.year <= year - 1) {
                if (event.month <= month) {
                    if (event.day <= day + 1) {
                        deleteDatabaseItem(event.id,context)
                    }
                }
            }
        }
    }

    fun editDatabaseEvent(data: KalenderEventData, context: Context){
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Kalender").child(data.id.toString()).child("Text").setValue(data.text)
    }
}

class  KalenderEventJSON{
    fun writeJSON(data: MutableList<KalenderEventData>, context: Context) {
        val file = FileWriter("/data/data/" + context.packageName + "/" + "kalenderevent.json")
        val arrayJson = JSONArray()

        for (i in data) {
            val objJson = JSONObject()
            objJson.put("day", i.day)
            objJson.put("month", i.month)
            objJson.put("year", i.year)
            objJson.put("text", i.text)
            objJson.put("datestr", i.dateStr)
            objJson.put("id", i.id)
            arrayJson.put(objJson)
        }
        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }
    fun addJSON(data: KalenderEventData, context: Context) {
        val existingJson = readJSON(context)
        val file = FileWriter("/data/data/" + context.packageName + "/" + "kalenderevent.json")
        val arrayJson = JSONArray()
        for (i in 0 until existingJson.length()) {
            arrayJson.put(existingJson[i])
        }
        val objJson = JSONObject()
        objJson.put("day", data.day)
        objJson.put("month", data.month)
        objJson.put("year", data.year)
        objJson.put("text", data.text)
        objJson.put("datestr", data.dateStr)
        objJson.put("id", data.id)
        arrayJson.put(objJson)
        file.write(arrayJson.toString())
        file.flush()
        file.close()
    }
    fun readJSON(context: Context): JSONArray {
        val file = File("/data/data/" + context.packageName + "/" + "kalenderevent.json")
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
    fun editJSONItem(data: KalenderEventData, context: Context) {
        val jsonArray = readJSON(context)
        val newJSONArray = JSONArray()
        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getJSONObject(i).getInt("id") == data.id) {
                val objJson = JSONObject()
                objJson.put("day", data.day)
                objJson.put("month", data.month)
                objJson.put("year", data.year)
                objJson.put("text", data.text)
                objJson.put("datestr", data.dateStr)
                objJson.put("id", data.id)
                newJSONArray.put(objJson)
            } else {
                val objJson = JSONObject()
                objJson.put("day", jsonArray.getJSONObject(i).get("day"))
                objJson.put("month", jsonArray.getJSONObject(i).get("month"))
                objJson.put("year", jsonArray.getJSONObject(i).get("year"))
                objJson.put("text", jsonArray.getJSONObject(i).get("text"))
                objJson.put("datestr", jsonArray.getJSONObject(i).get("datestr"))
                objJson.put("id", jsonArray.getJSONObject(i).get("id"))
                newJSONArray.put(objJson)
            }
        }
        val fileWrite = FileWriter("/data/data/" + context.packageName + "/" + "kalenderevent.json")
        fileWrite.write(newJSONArray.toString())
        fileWrite.flush()
        fileWrite.close()
    }
    fun deleteJSONItem(nr: Int, context: Context) {
        val jsonArray = readJSON(context)
        val newJSONArray = JSONArray()
        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getJSONObject(i).getInt("id") != nr){
                val objJson = JSONObject()
                objJson.put("day", jsonArray.getJSONObject(i).get("day"))
                objJson.put("month", jsonArray.getJSONObject(i).get("month"))
                objJson.put("year", jsonArray.getJSONObject(i).get("year"))
                objJson.put("text", jsonArray.getJSONObject(i).get("text"))
                objJson.put("datestr", jsonArray.getJSONObject(i).get("datestr"))
                objJson.put("id", jsonArray.getJSONObject(i).get("id"))
                newJSONArray.put(objJson)
            }
        }
        val fileWrite = FileWriter("/data/data/" + context.packageName + "/" + "kalenderevent.json")
        fileWrite.write(newJSONArray.toString())
        fileWrite.flush()
        fileWrite.close()
    }
    fun removeOneYearOldEvents(day: Int, month: Int, year: Int ,context: Context){
        val jsonArray = readJSON(context)
        val newJSONArray = JSONArray()
        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getJSONObject(i).getInt("year") <= year - 1) {
                if (jsonArray.getJSONObject(i).getInt("month") <= month) {
                    if (jsonArray.getJSONObject(i).getInt("day") <= day + 1) {
                        continue
                    }
                }
            }
            val objJson = JSONObject()
            objJson.put("day", jsonArray.getJSONObject(i).get("day"))
            objJson.put("month", jsonArray.getJSONObject(i).get("month"))
            objJson.put("year", jsonArray.getJSONObject(i).get("year"))
            objJson.put("text", jsonArray.getJSONObject(i).get("text"))
            objJson.put("datestr", jsonArray.getJSONObject(i).get("datestr"))
            objJson.put("id", jsonArray.getJSONObject(i).get("id"))
            newJSONArray.put(objJson)
        }
        val fileWrite = FileWriter("/data/data/" + context.packageName + "/" + "kalenderevent.json")
        fileWrite.write(newJSONArray.toString())
        fileWrite.flush()
        fileWrite.close()
    }
}