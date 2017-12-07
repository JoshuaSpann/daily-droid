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
}