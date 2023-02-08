package com.example.ema_projekt

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DatabaseManager {
    companion object {
        private lateinit var database: DatabaseReference
    }

    fun setUpDatabase(){
        database = FirebaseDatabase.getInstance("https://ema-projekt-e036e-default-rtdb.europe-west1.firebasedatabase.app/").reference
    }

    fun getDatabaseReference():DatabaseReference{
        return database
    }
}