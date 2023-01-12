package com.example.ema_projekt.wgplaner

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

data class LoginData(
    val wgName:String,
    val wgToken:String
)

class LoginDataSettingsJSON() {
    fun writeLoginDataJSON(data:LoginData, context:Context) {
        val file = FileWriter("/data/data/" + context.packageName + "/" + "settings.json")

        val objJson = JSONObject()
        objJson.put("wgName", data.wgName)
        objJson.put("wgToken", data.wgToken)
        objJson.put("wgInfo", "")

        file.write(objJson.toString())
        file.flush()
        file.close()
    }

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
