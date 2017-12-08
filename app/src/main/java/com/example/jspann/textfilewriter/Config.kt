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

    /* /  PREFERENCE CONTROLLERS  / */
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

        // Define Keys and their Values //
        var map: MutableMap<String, Any> = mutableMapOf(
                "blnAutoSave" to false,
                "blnAutoLogCalls" to true,
                "strAppColor" to "ffee2200"
        )

        // Remove Any Existing Values by Keys //
        var keys: MutableSet<String> = map.keys
        //removePreference(p_context, keys)

        // Set Values //
        map = mutableMapOf(
                "dailydroid__blnAutoSave" to false,
                "dailydroid__blnAutoLogCalls" to true,
                "dailydroid__strAppColor" to "ffee2200"
        )
        setPreference(p_context, map)

        // Get Pref Values //

        var mapAllPrefs: MutableMap<String, Any?> = getPreferences(p_context) as MutableMap<String, Any?>
        for((key, value) in mapAllPrefs){
            if(key.contains("dailydroid__",true))
                utils.popup(p_context.applicationContext, key+" : "+value.toString())
        }

    }

    fun showAllPreferences(p_context: Context){
        var mapAllPrefs: MutableMap<String, Any?> = getPreferences(p_context) as MutableMap<String, Any?>
        for((key, value) in mapAllPrefs){
            if(key.contains("dailydroid__",true))
                utils.popup(p_context.applicationContext, key+" : "+value.toString())
        }

    }
}