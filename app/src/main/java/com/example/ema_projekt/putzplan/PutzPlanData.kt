package com.example.ema_projekt.putzplan

import android.content.Context
import android.widget.Toast
import com.example.ema_projekt.DatabaseManager
import com.example.ema_projekt.kalender.KalenderEventData
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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

    suspend fun readDatabase(context: Context):List<PutzPlanData>{
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

