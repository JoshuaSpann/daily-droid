package com.example.jspann.textfilewriter

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast

import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import android.provider.ContactsContract



/**
 * Created by jspann on 11/29/2017.
 */

class Utils{
    fun configFile_Create(config: Config){
        val configFile = File(getDirectoryPathToString(),".config")
        if(configFile.exists()){
           return
        }

        // TODO - Create JSON of Config options array and save it to this file???
        var strJsonProps = "{colors:[main: \"#fff\",accent: \"#222\"]}"
        file_Write(configFile, strJsonProps)
    }
    fun configFile_Read(): Config{
        // TODO - Pull JSON of Config options and save it to array???
        return Config()
    }
    //TODO - Create Data Model of config opts in array/propclass and modify using predefined set of functions
    fun convertMutMapToJSONString(p_map: MutableMap<String,String>): String{
        var strOutput = "["
        var i = 1
        for(currItem in p_map){
            if(currItem.key==""||currItem.value==""){
                i++
                continue
            }
            strOutput += "\""+currItem.key+"\": \""+currItem.value+"\""
            if(i < (p_map.size-1)){
                strOutput+=", "
            }
            i++
        }
        strOutput+="]"
        println("\n\n"+p_map.size+"\n\n"+strOutput)
        return strOutput
    }

    fun file_Append(file:File, data:Any){
        fileWriter(file,data,true)
    }
    fun file_Write(file:File, data:Any){
        fileWriter(file,data,false)
    }

    private fun fileWriter(file:File, data:Any, isSafeWrite: Boolean){
        val fwriter = FileWriter(file)
        if(isSafeWrite == false){
            fwriter.write(data.toString())
        }
        else if(isSafeWrite == true){
            fwriter.append(data.toString())
        }
        else{
            return //ERROR, no save
        }
        fwriter.flush()
        fwriter.close()
    }

    fun getContactName(phoneNumber: String, context: Context): String {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))

        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        var contactName = ""
        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }

        if(contactName == ""){
            return phoneNumber
        }

        return contactName //+ " ("+phoneNumber+")"
    }

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
        var strDefaultDir:String = getDirectoryPathToString()+str_subdir
        return strDefaultDir
    }

    fun getListOfAllFilenamesInDir(pathString: String): Array<String?> {
        var filesInDir = getListOfAllFilesInDir(pathString)

        var intFileCounter = 0
        if (filesInDir === null) return arrayOf("No Files")

        val filenamesInDir = arrayOfNulls<String>(filesInDir.size)

        for (currFile in filesInDir) {
            filenamesInDir[intFileCounter] = currFile.getName().toString()
            intFileCounter++
        }

        return filenamesInDir
    }
    fun getListOfAllFilesInDir(pathString: String): Array<File>? {
        val dir = File(pathString)

        val filesInDir = dir.listFiles()
        if (filesInDir === null) return null

        Arrays.sort(filesInDir!!, Collections.reverseOrder<Any>())
        return filesInDir
    }

    fun popup(applicationContext: Context, data: Any) {
        android.widget.Toast.makeText(applicationContext, data.toString(), Toast.LENGTH_SHORT).show()
    }

    @Throws(Exception::class)
    fun readFileContentsToString(file: File): String {
        try {
            return Scanner(file).useDelimiter("\\A").next()
        }catch(e: Exception){
            return ""
        }
    }

}