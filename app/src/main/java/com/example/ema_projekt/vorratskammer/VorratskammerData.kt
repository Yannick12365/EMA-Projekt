package com.example.ema_projekt.vorratskammer

import android.content.Context
import android.widget.Toast
import com.example.ema_projekt.DatabaseManager
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class VorratskammerData(
    var text:String,
    var id:Int
)

class VorratskammerDatabase(){
    private val database: DatabaseReference = DatabaseManager().getDatabaseReference()

    fun writeDatabase(data: VorratskammerData, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                database.child(wgName).child("Vorratskammer")
                    .child(data.id.toString()).setValue(data.text)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context, "Ups, da ist etwas schief gelaufen!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    suspend fun readDatabase(context: Context):List<VorratskammerData>{
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

    fun deleteDatabaseItem(id: Int, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Vorratskammer").child(id.toString()).removeValue()
    }
}