package com.example.ema_projekt.wgplaner

import android.content.Context
import org.json.JSONObject
import java.io.*

data class LoginData(
    val wgName:String,
    val wgToken:String
)

class LoginDataSettingsJSON() {
    //https://stackoverflow.com/questions/14219253/writing-json-file-and-read-that-file-in-android
    fun writeLoginDataJSON(data:LoginData, context:Context) {
        val file = FileWriter("/data/data/" + context.packageName + "/" + "settings.json")

        val objJson = JSONObject()
        objJson.put("wgName", data.wgName)
        objJson.put("wgToken", data.wgToken)

        file.write(objJson.toString())
        file.flush()
        file.close()
    }

    //https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
    fun readLoginDataJSON(context:Context):LoginData {
        val file = File("/data/data/" + context.packageName + "/" + "settings.json")
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
            return LoginData(jsonObject.getString("wgName"),jsonObject.getString("wgToken"))
        } catch (e: FileNotFoundException) {
            return LoginData("","")
        }
    }
}
