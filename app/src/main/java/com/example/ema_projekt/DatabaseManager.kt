package com.example.ema_projekt

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class DatabaseManager {
    //Firebase Realtimedatabase Reference Objekt
    companion object {
        private lateinit var database: DatabaseReference
    }

    //Reference setzen
    fun setUpDatabase(){
        database = FirebaseDatabase.getInstance("https://ema-projekt-e036e-default-rtdb.europe-west1.firebasedatabase.app/").reference
    }

    //Reference getter
    fun getDatabaseReference():DatabaseReference{
        return database
    }
}