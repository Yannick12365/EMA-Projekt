package com.example.ema_projekt.einkaufsliste

import android.content.Context
import android.widget.Toast
import com.example.ema_projekt.DatabaseManager
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


data class EinkaufslisteData(
    var itemId: Int,
    var itemText: String,
)

class EinkauflisteDataBase {
    private val database: DatabaseReference = DatabaseManager().getDatabaseReference()

    fun writeDatabase(data: EinkaufslisteData, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                database.child(wgName).child("Einkaufsliste")
                    .child(data.itemId.toString()).setValue(data.itemText)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context, "Ups, da ist etwas schief gelaufen!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    suspend fun readDatabase(context: Context):List<EinkaufslisteData>{
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

    fun deleteDatabaseItem(id: Int?, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("Einkaufsliste").child(id.toString()).removeValue()
    }
}
