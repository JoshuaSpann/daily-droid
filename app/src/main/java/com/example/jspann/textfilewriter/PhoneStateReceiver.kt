package com.example.jspann.textfilewriter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Toast



/**
 * Created by jspann on 12/6/2017.
 */

class CallReceiver : BroadcastReceiver() {

    private var lastState: Int = TelephonyManager.CALL_STATE_IDLE;
    private var lastMsg: String = ""


    override fun onReceive(context: Context, intent: Intent) {
        //Log.w("intent " , intent.getAction().toString());
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        var savedNumber = ""
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

        } else {
            var stateStr: String = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);

            //var number: String = TelephonyManager.EXTRA_INCOMING_NUMBER
            var strIncomingNumber: String = TelephonyManager.EXTRA_INCOMING_NUMBER
            var state: Int = 0;
            //println("__________CallReceiver::onReceive()__________"+strIncomingNumber+" CALLED WITH A STATE OF "+stateStr)


            if(stateStr == TelephonyManager.EXTRA_STATE_RINGING){
                strIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                //println("____________________ "+"RINGING CALL: "+strIncomingNumber)
                val utils = Utils()
                var strIncomingCallLog = " - "+utils.getCurrentTimeStampAsString()+":  Call from "+strIncomingNumber
                if(strIncomingCallLog != lastMsg) {
                    println("        DEBUG TEST:: " + strIncomingCallLog)
                    lastMsg = strIncomingCallLog
                }
            }
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }
        }
    }


    fun onCallStateChanged(context: Context, state: Int, number: String) {
        if (lastState === state) {
            //No change, debounce extras
            return
        }

        var isIncoming: Boolean = false
        var callStartTime: Any = java.util.Date()
        var savedNumber: String = "NONE"

        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                callStartTime = java.util.Date()
                savedNumber = number

                Toast.makeText(context, "Incoming Call Ringing", Toast.LENGTH_SHORT).show()
            }
            TelephonyManager.CALL_STATE_OFFHOOK ->
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState !== TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    callStartTime = java.util.Date()
                    Toast.makeText(context, "Outgoing Call Started", Toast.LENGTH_SHORT).show()
                }
            TelephonyManager.CALL_STATE_IDLE ->
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState === TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    Toast.makeText(context, "Ringing but no pickup" + savedNumber + " Call time " + callStartTime + " Date " + java.util.Date(), Toast.LENGTH_SHORT).show()
                } else if (isIncoming) {

                    Toast.makeText(context, "Incoming $savedNumber Call time $callStartTime", Toast.LENGTH_SHORT).show()
                } else {

                    Toast.makeText(context, "outgoing " + savedNumber + " Call time " + callStartTime + " Date " + java.util.Date(), Toast.LENGTH_SHORT).show()

                }
        }
        lastState = state
    }
}