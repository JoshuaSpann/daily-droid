package com.example.jspann.textfilewriter

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
    var appColor: String = ""
    var autoLogCalls: Boolean = false
    var currentFile: String = ""
    var fileColors: MutableMap<String,String> = hashMapOf("" to "")

    public constructor(){}
    public fun read(){}

    public fun write(){
        val utils = Utils()
        val configFile = File(utils.getDirectoryPathToString(),".config")

        var strJsonProps = """{
            appColor: """+this.appColor+"""
            autoLogCalls: """+this.autoLogCalls+"""
            currentFile: """+this.currentFile+"""
            fileColors: """+utils.convertMutMapToJSONString(this.fileColors)+"""
        }"""
        utils.fileWrite(configFile, strJsonProps)
    }
}
/*
class Config {
    private val utils = Utils()
    private var colors: Map<String, String> ?= null

    public fun getColors(): Map<String,String>?{
        return this.colors
    }
    public fun setColors(p_mapValuesToAssignColorsTo: Map<String,String>): Map<String,String>?{
        if(p_mapValuesToAssignColorsTo.isEmpty()){
            return this.getColors()
        }
        this.colors = p_mapValuesToAssignColorsTo

        return this.getColors()
    }

    /*
    fun configFile_Create(){
        val configFile = File(utils.getDirectoryPathToString(),".config")
        if(configFile.exists()){
            return
        }

        // TODO - Create JSON of Config options array and save it to this file???
        var strJsonProps = "{colors:[main: \"#fff\",accent: \"#222\"]}"
        utils.fileWrite(configFile, strJsonProps)
    }
    fun configFile_Read(){
        // TODO - Pull JSON of Config options and save it to array???
    }
    */
}*/