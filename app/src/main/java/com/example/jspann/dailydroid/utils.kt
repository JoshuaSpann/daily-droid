package com.example.jspann.dailydroid

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.widget.Toast

import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import android.provider.ContactsContract
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan


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

    /**
     * Creates an OK/Cancel Dialog Window with the supplied title and message, performing the callback after.
     * The callback takes TRUE if OK was clicked or FALSE if Cancel was clicked
     */
    fun createDialog(context: Context, title: String, body: String, callback: (Boolean)->Unit) {
        var dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(body).setTitle(title)
        dialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog: DialogInterface, id: Int -> callback(true) /* USE run{...} INSTEAD OF cb() FOR BLOCK STATEMENTS */})
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog: DialogInterface, id: Int -> callback(false)})

        var dialog = dialogBuilder.create()
        dialog.show()
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

    fun getDateFromIntString(dateString: String): Date {
        val intDateYear = dateString.substring(0,3).toInt()
        val intDateMonth = dateString.substring(4,5).toInt()
        val intDateDay = dateString.substring(6,7).toInt()

        return Date(intDateYear,intDateMonth,intDateDay)
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

    fun getFileColorIntFromPreferences(context: Context, config: Config, strFileName: String): Int {
        var color = Colors.App.CURRENT_PRIMARY
        //var accentColor = Color.parseColor(String.format("#%06X", 0xBBBBCC and Colors.App.CURRENT_ACCENT))
        var str: String? = config.getPreferenceValue(context, "dailydroid__"+strFileName) as String?

        if(!str.isNullOrEmpty()) {
            color = Color.parseColor("#"+str)
            //var hexColor = String.format("#%06X", 0xBBBBCC and color)
            //accentColor = Color.parseColor(hexColor)
        }

        return color
    }

    fun getFilenameStringFormattedWithPropertiesColor(context: Context, strFileName: String, config: Config): SpannableString {
        // Parse filename date to get day's letter: [N,M,T,W,R,F,S] //
        var strFileDisplayName = strFileName

        // Set SpannableString color to the existing file color assigned in config //
        var fileNameSpannableStr = SpannableString(strFileDisplayName)
        var colorSpan = ForegroundColorSpan(Colors.GRAY)

        val currentFileColorPref = config.getPreferenceValue(context, "dailydroid__"+strFileName)
        if (currentFileColorPref !== null) {
            val fileColor = currentFileColorPref.toString()
            colorSpan = ForegroundColorSpan(Color.parseColor("#"+fileColor))
        }
        fileNameSpannableStr.setSpan(colorSpan, 0, strFileDisplayName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return fileNameSpannableStr
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