package com.example.jspann.textfilewriter

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView

import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import android.content.Intent
import android.os.Environment

/**
 * Created by jspann on 11/29/2017.
 */

class Utils{
    fun getCurrentFormattedDateAsString(): String {
        val dteCurrentDate = Date()
        val dteFormat = SimpleDateFormat("yyyyMMdd")
        return dteFormat.format(dteCurrentDate).toString()
    }
    fun getCurrentTimeStampAsString(): String {
        return SimpleDateFormat("HHmm").format(Date()).toString()
    }

    fun getDirectoryPathToString(): String {
        var strDefaultDir = Environment.getExternalStorageDirectory().toString()
        strDefaultDir+="/DailyDroid/"
        val projectDir = File(strDefaultDir)
        projectDir.mkdir()
        return strDefaultDir
    }
    fun getDirectoryPathToString(str_subdir: String): String {
        var strDefaultDir:String = this.getDirectoryPathToString()+str_subdir
        return strDefaultDir
    }

    fun getListOfAllFilenamesInDir(pathString: String): Array<String?> {
        val filesInDir = this.getListOfAllFilesInDir(pathString)

        var intFileCounter = 0
        val filenamesInDir = arrayOfNulls<String>(filesInDir.size)

        for (currFile in filesInDir) {
            filenamesInDir[intFileCounter] = currFile.getName().toString()
            intFileCounter++
        }

        return filenamesInDir
    }
    fun getListOfAllFilesInDir(pathString: String): Array<File> {
        val dir = File(pathString)

        val filesInDir = dir.listFiles()
        Arrays.sort(filesInDir!!, Collections.reverseOrder<Any>())
        return filesInDir
    }

    fun popup(applicationContext: Context, data: Any) {
        android.widget.Toast.makeText(applicationContext, data.toString(), Toast.LENGTH_LONG).show()
    }

    @Throws(Exception::class)
    fun readFileContentsToString(file: File): String {
        return Scanner(file).useDelimiter("\\A").next()
    }
}