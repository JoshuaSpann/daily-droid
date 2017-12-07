package com.example.jspann.textfilewriter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Toast
import java.io.File


/**
 * Created by jspann on 12/6/2017.
 */

class CallReceiver : BroadcastReceiver() {

    var intRingingWriteCount: Int = 0
    private var lastState: Int = TelephonyManager.CALL_STATE_IDLE;
    private var lastMsg: String = ""

    override fun onReceive(context: Context, intent: Intent) {
        var savedNumber = ""

        // Outgoing Calls //
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        }
        else {
            // Incoming Calls //
            var stateStr: String = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);

            //var number: String = TelephonyManager.EXTRA_INCOMING_NUMBER
            var strIncomingNumber: String = TelephonyManager.EXTRA_INCOMING_NUMBER
            var state: Int = 0;
            //println("__________CallReceiver::onReceive()__________"+strIncomingNumber+" CALLED WITH A STATE OF "+stateStr)


            if(stateStr == TelephonyManager.EXTRA_STATE_RINGING){
                state = TelephonyManager.CALL_STATE_RINGING
            }
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }
            try {
                onCallStateChanged(context, state, intent.getStringExtra(strIncomingNumber))
            }catch(e:Exception){}
        }
    }


    private fun logCallToTodaysFile(content: Any){
        var utils = Utils()
        var todayFile = File(utils.getDirectoryPathToString(),utils.getCurrentFormattedDateAsString()+".txt")

        if(todayFile.exists()){
            // Add to File //
            utils.file_Append(todayFile, utils.readFileContentsToString(todayFile)+content)
        }

        if(!todayFile.exists()){
            // Create File //
            utils.file_Append(todayFile, ("# " + utils.getCurrentFormattedDateAsString() + utils.getCurrentTimeStampAsString() + "\n\n---\n\n") + content)
        }
        intRingingWriteCount++
        // TODO - FIND WAY TO MAKE THIS HAPPEN!
        MainActivity::resetEditTextToGivenValue
    }

    fun onCallStateChanged(context: Context, state: Int, number: String) {
        if (lastState === state) {
            return
        }

        val utils = Utils()

        var isIncoming: Boolean = false
        var callStartTime: Any = java.util.Date()
        var savedNumber: String = "NONE"

        when (state) {

            // TODO - STOP FROM FIRING TWICE! BUG WHERE IT PERFORMS TWICE IMMEDIATELY IN SUCCESSION!
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                callStartTime = java.util.Date()
                savedNumber = number

                var strIncomingCallLog = "\n - "+utils.getCurrentTimeStampAsString()+":  Call from "+utils.getContactName(number,context)
                if(strIncomingCallLog != lastMsg) {
                    println("        JS3 DEBUG TEST:: " + strIncomingCallLog)
                    lastMsg = strIncomingCallLog
                }
                if(intRingingWriteCount == 0){
                    logCallToTodaysFile(strIncomingCallLog)
                }

                Toast.makeText(context, "Incoming Call Ringing", Toast.LENGTH_SHORT).show()
            }

            TelephonyManager.CALL_STATE_OFFHOOK ->
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState !== TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    callStartTime = java.util.Date()

                    var strOutgoingCallLog = "\n - "+utils.getCurrentTimeStampAsString()+":  Called "+utils.getContactName(number,context)
                    logCallToTodaysFile(strOutgoingCallLog)

                    Toast.makeText(context, "Outgoing Call Started", Toast.LENGTH_SHORT).show()
                }

            TelephonyManager.CALL_STATE_IDLE ->
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState === TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    logCallToTodaysFile(" missed. ")
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