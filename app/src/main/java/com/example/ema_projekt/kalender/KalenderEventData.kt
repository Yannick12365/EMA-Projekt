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
        database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context, "Ups, da ist etwas schief gelaufen!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    fun deleteDatabaseItem(id: Int, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Kalender").child(id.toString()).removeValue()
    }

    suspend fun readDatabase(context: Context): List<KalenderEventData> {
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