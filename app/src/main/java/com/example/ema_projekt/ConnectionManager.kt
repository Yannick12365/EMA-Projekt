package com.example.ema_projekt

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi

//https://youtu.be/BoiBuRwZ6RE
class ConnectionManager : BroadcastReceiver() {
    companion object {
        private lateinit var appContext: Context
        private var loginScreen: Boolean = false
    }

    fun setOjects(context: Context, login:Boolean, textView: TextView){
        appContext = context
        loginScreen = login
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (!checkConnection(context)){
            if (!loginScreen) {
                createInternetErrorPopUp(appContext)
            } else{
                createLoginErrorPopUp(appContext)
            }
            Log.d("DEBUG","Connection Lost")
        } else {
            Log.d("DEBUG","Connection")
        }
    }

    private fun checkConnection(context: Context?):Boolean{
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo

        if (activeNetwork != null) {
            return activeNetwork.isConnectedOrConnecting
        }
        return false
    }

    private fun createInternetErrorPopUp(context: Context){
        val errorPopUp = Dialog(context)
        errorPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        errorPopUp.setContentView(R.layout.popup_internet_error)
        val button:Button = errorPopUp.findViewById(R.id.button_verstanden)
        button.setOnClickListener {
            errorPopUp.dismiss()
        }
        errorPopUp.show()
    }

     private fun createLoginErrorPopUp(context: Context){
        val errorPopUp = Dialog(context)
        errorPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        errorPopUp.setContentView(R.layout.popup_login_error)
        val button:Button = errorPopUp.findViewById(R.id.button_verstanden)
        button.setOnClickListener {
            errorPopUp.dismiss()
        }
        errorPopUp.show()
    }
}
