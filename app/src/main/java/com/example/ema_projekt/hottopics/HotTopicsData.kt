package com.example.ema_projekt.hottopics

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.ema_projekt.DatabaseManager
import com.example.ema_projekt.vorratskammer.VorratskammerData
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


data class HotTopicsData(
    var id: Int,
    var text: String,
    var kommentare: MutableList<HotTopicKommentarData>
)

data class HotTopicKommentarData(
    var id:Int,
    var text:String
)

class HotTopicDatabase{
    private val database: DatabaseReference = DatabaseManager().getDatabaseReference()

    fun writeDatabase(data: HotTopicsData, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("HotTopic").child(data.id.toString()).child("Text").setValue(data.text)
    }

    suspend fun readDatabase(context: Context):List<HotTopicsData>{
        return suspendCoroutine { value ->
            val list = mutableListOf<HotTopicsData>()
            val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
            database.child(wgName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("HotTopic")) {
                        for (data in snapshot.child("HotTopic").children) {
                            val kommentarListe = mutableListOf<HotTopicKommentarData>()

                            if (snapshot.child("HotTopic").child(data.key.toString()).hasChild("Kommentare")) {
                                for (kommentar in snapshot.child("HotTopic")
                                    .child(data.key.toString()).child("Kommentare").children) {
                                    kommentarListe.add(
                                        HotTopicKommentarData(
                                            kommentar.key.toString().toInt(),
                                            kommentar.value.toString()
                                        )
                                    )
                                }
                            }

                            list.add(HotTopicsData(
                                data.key.toString().toInt(),
                                data.child("Text").value.toString(),
                                kommentarListe))
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
        database.child(wgName).child("HotTopic").child(id.toString()).removeValue()
    }

    fun writeKommentar(idTopic:Int, data:HotTopicKommentarData, context: Context){
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        database.child(wgName).child("HotTopic").child(idTopic.toString()).child("Kommentare").child(data.id.toString()).setValue(data.text)
    }

    fun deleteDatabaseItemKommentar(idTopic:Int,id: Int, context: Context) {
        val wgName = LoginDataSettingsJSON().readLoginDataJSON(context).wgName
        Log.d("DEBUG",idTopic.toString())
        Log.d("DEBUG",id.toString())
        database.child(wgName).child("HotTopic").child(idTopic.toString()).child("Kommentare").child(id.toString()).removeValue()
    }
}
