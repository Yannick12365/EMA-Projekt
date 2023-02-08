package com.example.ema_projekt

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class ConnectionManager : BroadcastReceiver() {
    companion object {
        private var loginScreen: Boolean = false
        private lateinit var appContext: Context
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val appContextView:View = (appContext as Activity).findViewById(android.R.id.content)
        val textView:TextView = appContextView.findViewById(R.id.textViewInternetError)

        if (!checkConnection(appContext)){
            if (!loginScreen) {
                createInternetErrorPopUp(appContext)
            } else{
                createLoginErrorPopUp(appContext)
            }
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.INVISIBLE
        }
    }

    fun setOjects(login:Boolean, context: Context){
        loginScreen = login
        appContext = context
    }

    fun switchScreen(context: Context){
        val appContext:View = (context as Activity).findViewById(android.R.id.content)
        val textView:TextView = appContext.findViewById(R.id.textViewInternetError)

        if (!checkConnection(context)){
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.INVISIBLE
        }
    }

    //https://stackoverflow.com/questions/5474089/how-to-check-currently-internet-connection-is-available-or-not-in-android
    fun checkConnection(context: Context?):Boolean{
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected){
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
