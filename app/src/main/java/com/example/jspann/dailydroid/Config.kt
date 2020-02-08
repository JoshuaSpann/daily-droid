package com.example.jspann.dailydroid

import android.content.Context
import android.preference.PreferenceManager

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

    val appPrefix = "daily_droid__"
    val preferencePrefix = "${appPrefix}pref_"
    val colorpreferencePrefix = "${appPrefix}color_"
    var verbosePopups = true

    var advancedUI = false

    var appColor: String = ""
    var autoLogCalls: Boolean = false
    var currentFile: String = ""
    var fileColors: MutableMap<String,String> = hashMapOf("" to "")

    /* /  PREFERENCE CONTROLLERS  / */
    /*  Get Preferences  */
    fun getPreferenceValue(p_context: Context, p_key: String): Any?{
        val prefs = PreferenceManager.getDefaultSharedPreferences(p_context)
        for((key, value) in prefs.all){
            if(key == p_key){
                return value
            }
        }
        return null
    }

    fun getPreference(p_context: Context, p_key: String): Map<String, Any?>?{
        val prefs = PreferenceManager.getDefaultSharedPreferences(p_context)
        for((key, value) in prefs.all){
            if(key == p_key){
                return mapOf(key.toString() to value)
            }
        }
        return null
    }

    fun getPreferences(p_context: Context): Map<String, Any?>{
        val prefs = PreferenceManager.getDefaultSharedPreferences(p_context)
        return prefs.all
    }


    /*  Remove Preferences */
    fun removePreference(p_context: Context, p_key: String){
        val prefs = PreferenceManager.getDefaultSharedPreferences(p_context)
        prefs.edit().remove(p_key).commit()
    }

    fun removePreference(p_context: Context, p_keys: MutableSet<String>){
        for(key: String in p_keys){
            removePreference(p_context, key)
        }
    }


    /*  Set Preferences  */
    fun setDefaultPreferences(p_context: Context){
        // Define Keys and their Values //
        var map: MutableMap<String, Any> = mutableMapOf(
                "dailydroid__blnAutoSave" to false,
                "dailydroid__blnAutoLogCalls" to true,
                "dailydroid__strAppColor" to "ffee2200"
        )
        setPreference(p_context, map)
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


    /*  Display Preferences  */
    fun showAllPreferences(p_context: Context){
        var mapAllPrefs: MutableMap<String, Any?> = getPreferences(p_context) as MutableMap<String, Any?>
        for((key, value) in mapAllPrefs){
            if(key.contains("dailydroid__",true))
                utils.popup(p_context.applicationContext, key+" : "+value.toString())
        }

    }
}