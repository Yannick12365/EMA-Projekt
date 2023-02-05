package com.example.ema_projekt

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi

//https://youtu.be/BoiBuRwZ6RE

@RequiresApi(Build.VERSION_CODES.M)
class ConnectionManager : BroadcastReceiver() {
    companion object {
        private lateinit var appContext: Context
        private var loginScreen: Boolean = false
    }

    fun setOjects(context: Context, login:Boolean){
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

    fun checkConnection(context: Context?):Boolean{
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (networkInfo != null){
            return true
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

     fun createLoginErrorPopUp(context: Context){
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
