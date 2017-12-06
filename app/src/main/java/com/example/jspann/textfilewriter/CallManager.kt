package com.example.jspann.textfilewriter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

/**
 * Created by jspann on 12/6/2017.
 */
class CallManager{
    constructor(context: Context){
        val utils = Utils()
        var _phoneStateListener: PhoneStateListener = PhoneStateListener()
        var tm: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        tm.listen(_phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)

        var blnCallLoop = true
        while(blnCallLoop) {
            if (tm.callState == TelephonyManager.CALL_STATE_RINGING) {
                println("_________________________________________________________________________________INCOMING CALL_________________________________________________________________________________")
            }
            if (tm.callState == TelephonyManager.CALL_STATE_IDLE) {
                println("_________________________________________________________________________________IDLE_________________________________________________________________________________")
            }

            utils.popup(context, tm.callState)
        }
    }
    /*abstract class CallReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
        fun onCallStateChanged(ctx: Context, intState: Int, strNumber: String){

        }
    }*/
}