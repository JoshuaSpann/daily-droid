package com.example.jspann.dailydroid

public class PreferencesModel {
    var key: String = ""
    var value: Any? = null
    constructor(key:String, value:Any?) {
        this.key = key
        this.value = value
    }
}