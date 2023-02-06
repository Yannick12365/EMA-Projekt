package com.example.ema_projekt.wginfo

import android.content.Context
import android.widget.Toast
import com.example.ema_projekt.DatabaseManager
import com.example.ema_projekt.wgplaner.LoginDataSettingsJSON
import com.google.firebase.database.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WGInfoData {
    private val database: DatabaseReference = DatabaseManager().getDatabaseReference()
    fun writeDatabase(text: String, context: Context){
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("WGInfo").setValue(text)
    }

    suspend fun readDatabase(context: Context):String{
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