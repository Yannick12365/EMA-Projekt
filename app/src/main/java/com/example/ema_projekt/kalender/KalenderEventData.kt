package com.example.ema_projekt.kalender

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

data class KalenderEventData(
    val day:Int,
    val month:Int,
    val year:Int,
    var text:String,
    val dateStr:String,
    var id:Int)

class  KalenderEventJSON{
    fun writeJSON(data: KalenderEventData, context: Context) {
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
            if (jsonArray.getJSONObject(i).getInt("year") >= year - 1) {
                if (jsonArray.getJSONObject(i).getInt("month") >= month) {
                    if (jsonArray.getJSONObject(i).getInt("day") >= day + 1) {
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
            }
        }
        val fileWrite = FileWriter("/data/data/" + context.packageName + "/" + "kalenderevent.json")

        fileWrite.write(newJSONArray.toString())
        fileWrite.flush()
        fileWrite.close()
    }
}