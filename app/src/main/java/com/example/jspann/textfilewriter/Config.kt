package com.example.jspann.textfilewriter

import android.content.Context
import android.preference.PreferenceManager
import java.io.File

/**
 * Created by lxd on 12/2/17.
 */
/*
 * THINK IN JSON NOTATION:

    {
        appcolor: "#0ef",
        autologcalls: false,
        currentfile: "dir/file",
        filecolors:[
            "20171202.txt": "#0ff",
            "20171201.txt": "#0e2"
        ]
    }
*/
class Config{
    private val utils = Utils()
    private val configFile = File(utils.getDirectoryPathToString(),".config")
    //private val configFile = File(getC,".config")

    var appColor: String = ""
    var autoLogCalls: Boolean = false
    var currentFile: String = ""
    var fileColors: MutableMap<String,String> = hashMapOf("" to "")

    public constructor(){

    }
    public fun read(){

    }

    public fun write(){
        var strJsonProps = """{
            appColor: """+this.appColor+"""
            autoLogCalls: """+this.autoLogCalls+"""
            currentFile: """+this.currentFile+"""
            fileColors: """+utils.convertMutMapToJSONString(this.fileColors)+"""
        }"""

        //(fileColors as CharSequence).associate {  }
        utils.file_Write(configFile, strJsonProps)
    }

    /*fun getPreference(p_context: Context, p_key: String): Map<String, Any>{
        val prefs = PreferenceManager.getDefaultSharedPreferences(p_context)

        var blnAutoLogCalls_v = prefs.getBoolean(strAutoLogCalls_k, false)
        var strAppColor_v = prefs.getString(strAppColor_k, "")
    }
    */
    fun getPreferences(p_context: Context): Map<String, Any?>{
        val prefs = PreferenceManager.getDefaultSharedPreferences(p_context)
        return prefs.all
    }
    fun removePreference(p_context: Context, p_key: String){
        val prefs = PreferenceManager.getDefaultSharedPreferences(p_context)
        prefs.edit().remove(p_key).commit()
    }
    fun removePreference(p_context: Context, p_keys: MutableSet<String>){
        for(key: String in p_keys){
            removePreference(p_context, key)
        }
    }
    fun setPreference(p_context: Context, p_key: String, p_value: Any){
        val prefs = PreferenceManager.getDefaultSharedPreferences(p_context)

        if(p_value::class == Boolean::class){
            prefs.edit().putBoolean(p_key, p_value as Boolean).commit()
        }
        if(p_value::class == Int::class){
            prefs.edit().putInt(p_key, p_value as Int).commit()
        }
        if(p_value::class == String::class){
            prefs.edit().putString(p_key, p_value as String).commit()
            //addPref_Str(key, value as String)
        }
        if(p_value::class == Set::class) {
            prefs.edit().putStringSet(p_key, p_value as Set<String>)
        }
    }
    fun setPreference(p_context: Context, p_map: MutableMap<String, Any>){
        for((key, value) in p_map){
            setPreference(p_context, key, value)
        }
    }

    fun setDefaultPreferences(p_context: Context){
        //println(_sharedPrefs.getString("pref_app_color",""))
        val prefs = PreferenceManager.getDefaultSharedPreferences(p_context)

        var map: MutableMap<String, Any> = mutableMapOf(
                "blnAutoSave" to false,
                "blnAutoLogCalls" to true,
                "strAppColor" to "ffee2200"
        )
        var keys: MutableSet<String> = map.keys
        removePreference(p_context, keys)

        map = mutableMapOf(
                "dailydroid__blnAutoSave" to false,
                "dailydroid__blnAutoLogCalls" to true,
                "dailydroid__strAppColor" to "ffee2200"
        )
        setPreference(p_context, map)

        var mapAllPrefs: MutableMap<String, Any?> = getPreferences(p_context) as MutableMap<String, Any?>
        for((key, value) in mapAllPrefs){
            if(key.contains("dailydroid__",true))
                utils.popup(p_context.applicationContext, key+" : "+value.toString())
        }
        /*
        // Define Keys //
        var strAutoSave_k = "blnAutoSave"
        var strAutoLogCalls_k = "blnAutoLogCalls"
        var strAppColor_k = "strAppColor"

        // Remove Any Existing Values //
        removePreference(p_context, strAutoSave_k)
        removePreference(p_context, strAutoLogCalls_k)
        removePreference(p_context, strAppColor_k)

        // Set Values if they do Not Exist //
        if(!prefs.contains(strAutoSave_k)) {
            setPreference(p_context, strAutoSave_k, true)
        }
        if(!prefs.contains(strAutoLogCalls_k)) {
            setPreference(p_context, strAutoSave_k, false)
        }
        if(!prefs.contains(strAppColor_k)){
            setPreference(p_context, strAppColor_k, "ffee1100")
        }


        // Get Pref Values //
        var blnAutoSaveVal = prefs.getBoolean("blnAutoSave", false)
        var blnAutoLogCalls_v = prefs.getBoolean(strAutoLogCalls_k, false)
        var strAppColor_v = prefs.getString(strAppColor_k, "")

        utils.popup(p_context.applicationContext, strAutoSave_k+":"+blnAutoSaveVal)
        utils.popup(p_context.applicationContext, strAutoLogCalls_k+":"+blnAutoLogCalls_v)
        utils.popup(p_context.applicationContext, strAppColor_k+":"+strAppColor_v)
        */
    }
}