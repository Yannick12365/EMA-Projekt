package com.example.ema_projekt

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView


class ConnectionManager : BroadcastReceiver() {
    companion object {
        private var loginScreen: Boolean = false
        private lateinit var appContext: Context
        private var errorDialogShow = false
    }

    //Event das guckt ob es eine Netzwerk Änderung gibt
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

    //Objekte setzen für einen neue Activity
    fun setOjects(login:Boolean, context: Context){
        loginScreen = login
        appContext = context
    }

    //Keine Internetverbindung Nachricht zeigen/verstecken lassen bei einem Activity wechsel
    fun switchScreen(context: Context){
        val appContext:View = (context as Activity).findViewById(android.R.id.content)
        val textView:TextView = appContext.findViewById(R.id.textViewInternetError)

        if (!checkConnection(context)){
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.INVISIBLE
        }
    }


    //-----------------------------------------------------------------------
    //https://stackoverflow.com/questions/5474089/how-to-check-currently-internet-connection-is-available-or-not-in-android
    //Code der Funktion checkConnection von StackOverflow siehe Link (mit eigenen kleinen anpassungen)

    //Pruefen ob Handy mit dem Internet verbunden ist
    fun checkConnection(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
    }
    //-----------------------------------------------------------------------


    //Kein Internet verfügbar Popup
    private fun createInternetErrorPopUp(context: Context){
        if (!errorDialogShow) {
            val errorPopUp = Dialog(context)
            errorPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            errorPopUp.setContentView(R.layout.popup_internet_error)
            val button: Button = errorPopUp.findViewById(R.id.button_verstanden)
            button.setOnClickListener {
                errorPopUp.dismiss()
                errorDialogShow = false
            }
            errorPopUp.show()
            errorDialogShow = true
        }
    }

    //Kein Internet verfügbar Popup für Login Screen
     private fun createLoginErrorPopUp(context: Context){
        if (!errorDialogShow) {
            val errorPopUp = Dialog(context)
            errorPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            errorPopUp.setContentView(R.layout.popup_login_error)
            val button: Button = errorPopUp.findViewById(R.id.button_verstanden)
            button.setOnClickListener {
                errorPopUp.dismiss()
                errorDialogShow = false
            }
            errorPopUp.show()
            errorDialogShow = true
        }
    }
}
