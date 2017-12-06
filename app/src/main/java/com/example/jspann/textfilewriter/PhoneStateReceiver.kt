package com.example.jspann.textfilewriter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

/**
 * Created by jspann on 12/6/2017.
 */
abstract class PhoneStateReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val utils = Utils()
        try{
            println("_________________ RECEIVER START _________________")
            var state:String = (intent!!).getStringExtra(TelephonyManager.EXTRA_STATE)

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                utils.popup((context!!), "INCOMING CALL" + state)
                println("_________________________________________________________________________________ INCOMING _________________________________________________________________________________")
            }
        }catch(e:Exception){
            e.printStackTrace()
        }
    }
}