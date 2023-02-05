package com.example.ema_projekt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi

//https://youtu.be/BoiBuRwZ6RE

class ConnectionManager {
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkConnection(context: Context?):Boolean{
        val connectManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectManager.getNetworkCapabilities(connectManager.activeNetwork)

        if (networkInfo != null){
            return true
        }
        return false
    }
}

 class NetworkChangeListener : BroadcastReceiver() {
     @RequiresApi(Build.VERSION_CODES.M)
     override fun onReceive(context: Context?, intent: Intent?) {
         val conManager = ConnectionManager()
         if(!conManager.checkConnection(context)){
             Log.d("DEBUG","Connection Lost")
         } else {
             Log.d("DEBUG","Connection")
         }
     }
 }