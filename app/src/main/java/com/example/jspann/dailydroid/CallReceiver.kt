package com.example.jspann.dailydroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import java.io.File


/**
 * Created by jspann on 12/6/2017.
 */

class CallReceiver : BroadcastReceiver() {

    var intRingingWriteCount: Int = 0
    private var lastState: Int = TelephonyManager.CALL_STATE_IDLE;
    private var lastMsg: String = ""
    private val config = Config()
    var _isCallLoggingEnabled = false

    /**
     * Compares the phone call's state and returns the appropriate state value
     */
    private fun getPhoneCallState(stateStr: String): Int {
        var state = 0

        when (stateStr) {
            TelephonyManager.EXTRA_STATE_RINGING ->
                state = TelephonyManager.CALL_STATE_RINGING

            TelephonyManager.EXTRA_STATE_IDLE ->
                state = TelephonyManager.CALL_STATE_IDLE

            TelephonyManager.EXTRA_STATE_OFFHOOK ->
                state = TelephonyManager.CALL_STATE_OFFHOOK

            TelephonyManager.EXTRA_STATE_RINGING ->
                state = TelephonyManager.CALL_STATE_RINGING
        }

        return state
    }

    /**
     * Inserts a call activity into the most recent file or creates a new file if it doesn't exist
     */
    private fun logCallToTodaysFile(content: Any){
        val utils = Utils()
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
        //(MainActivity::resetEditTextToGivenValue)(MainActivity())
    }

    /**
     * Main listener for incoming/outgoing calls
     */
    fun onCallStateChanged(context: Context, state: Int, number: String) {
        // See if autologging calls is enabled or perform safe exit //
        val daily_droid__pref_autolog_calls_enabled = config.getPreferenceValue(context, "daily_droid__pref_autolog_calls_enabled")

        if (daily_droid__pref_autolog_calls_enabled !== null) {
            _isCallLoggingEnabled = config.getPreferenceValue(context, "daily_droid__pref_autolog_calls_enabled") as Boolean
        }

        if (!_isCallLoggingEnabled) return

        val utils = Utils()

        var isIncoming: Boolean = false
        var callStartTime: Any = java.util.Date()
        var savedNumber: String = "NONE"

        when (state) {
            TelephonyManager.CALL_STATE_RINGING ->
            {
                isIncoming = true
                callStartTime = java.util.Date()
                savedNumber = number

                val strIncomingCallLog = "\n - "+utils.getCurrentTimeStampAsString()+":  Call from "+utils.getContactName(number,context)
                if(strIncomingCallLog != lastMsg) {
                    println("        JS3 DEBUG TEST:: " + strIncomingCallLog)
                    lastMsg = strIncomingCallLog
                }
                if(intRingingWriteCount == 0){
                    logCallToTodaysFile(strIncomingCallLog)
                }

                if (config.verbosePopups) Toast.makeText(context, "Incoming Call Ringing", Toast.LENGTH_SHORT).show()
            }

            TelephonyManager.CALL_STATE_OFFHOOK ->
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    callStartTime = java.util.Date()

                    val strOutgoingCallLog = "\n - "+utils.getCurrentTimeStampAsString()+":  Called "+utils.getContactName(number,context)
                    logCallToTodaysFile(strOutgoingCallLog)

                    if (config.verbosePopups) Toast.makeText(context, "Outgoing Call Started", Toast.LENGTH_SHORT).show()
                }
                else {
                    val strOutgoingCallLog = "\n - "+utils.getCurrentTimeStampAsString()+":  Finished call. "
                    logCallToTodaysFile(strOutgoingCallLog)
                }

            TelephonyManager.CALL_STATE_IDLE ->
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    logCallToTodaysFile(" missed. ")
                    if (config.verbosePopups) Toast.makeText(context, "Ringing but no pickup" + savedNumber + " Call time " + callStartTime + " Date " + java.util.Date(), Toast.LENGTH_SHORT).show()
                } else if (isIncoming) {
                    if (config.verbosePopups) Toast.makeText(context, "Incoming $savedNumber Call time $callStartTime", Toast.LENGTH_SHORT).show()
                } else {
                    if (config.verbosePopups) Toast.makeText(context, "outgoing " + savedNumber + " Call time " + callStartTime + " Date " + java.util.Date(), Toast.LENGTH_SHORT).show()
                }

        }
        lastState = state
    }

    /**
     * Runs when device is being called
     */
    override fun onReceive(context: Context, intent: Intent) {
        // See if autologging calls is enabled or perform safe exit //
        val daily_droid__pref_autolog_calls_enabled = config.getPreferenceValue(context, "daily_droid__pref_autolog_calls_enabled")

        if (daily_droid__pref_autolog_calls_enabled !== null) {
            _isCallLoggingEnabled = config.getPreferenceValue(context, "daily_droid__pref_autolog_calls_enabled") as Boolean
        }

        if (!_isCallLoggingEnabled) return

        var savedNumber = ""

        // Outgoing Calls //
        if (intent.action == "android.intent.action.NEW_OUTGOING_CALL") {
            savedNumber = intent.extras.getString("android.intent.extra.PHONE_NUMBER")
        }
        // Incoming Calls //
        else {
            val stateStr: String = intent.extras.getString(TelephonyManager.EXTRA_STATE)
            //var number: String = TelephonyManager.EXTRA_INCOMING_NUMBER
            val strIncomingNumber: String = TelephonyManager.EXTRA_INCOMING_NUMBER
            val state = this.getPhoneCallState(stateStr)
            //println("__________CallReceiver::onReceive()__________"+strIncomingNumber+" CALLED WITH A STATE OF "+stateStr)

            try {
                onCallStateChanged(context, state, intent.getStringExtra(strIncomingNumber))
            }
            catch(e:Exception){}
        }
    }

}