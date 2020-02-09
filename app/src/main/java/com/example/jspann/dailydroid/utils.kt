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
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import java.util.regex.Pattern


/**
 * Created by jspann on 11/29/2017.
 */

class Utils{
    val VERSION = "3.0"

    /**
     * Creates the main configuration JSON file if it does not exist
     */
    fun configFile_Create(config: Config){
        val configFile = File(getDirectoryPathToString(),".config")
        if(configFile.exists()){
           return
        }

        // TODO - Create JSON of Config options array and save it to this file???
        var strJsonProps = "{colors:[main: \"#fff\",accent: \"#222\"]}"
        file_Write(configFile, strJsonProps)
    }

    /**
     * Reads config options from file and saves to a Config object
     */
    fun configFile_Read(): Config{
        // TODO - Pull JSON of Config options and save it to array???
        return Config()
    }

    /**
     * Converts JSON to a KVP array or object
     */
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

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    /**
     * Add data to end of file without modifying original contents
     */
    fun file_Append(file:File, data:Any){
        fileWriter(file,data,true)
    }

    /**
     * Overwrite file with data
     */
    fun file_Write(file:File, data:Any){
        fileWriter(file,data,false)
    }

    /**
     * Unified function that overwrites or appedns to a file depending on params' call state
     */
    private fun fileWriter(file:File, data:Any, isSafeWrite: Boolean){
        val fwriter = FileWriter(file)
        if(!isSafeWrite){
            fwriter.write(data.toString())
        }
        else if(isSafeWrite){
            fwriter.append(data.toString())
        }
        else{
            return //ERROR, no save
        }
        fwriter.flush()
        fwriter.close()
    }

    /**
     * Returns the given contact name (if any) for the supplied phone number
     */
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

    /**
     * Returns Date.now() in a "yyyyMMdd" string value
     */
    fun getCurrentFormattedDateAsString(): String {
        val dteCurrentDate = Date()
        val dteFormat = SimpleDateFormat("yyyyMMdd")
        return dteFormat.format(dteCurrentDate).toString()
    }

    /**
     * Returns the a 24-hour timestamp string of the current time
     */
    fun getCurrentTimeStampAsString(): String {
        return SimpleDateFormat("HHmm").format(Date()).toString()
    }

    /**
     * Returns a given date for the supplied string
     */
    fun getDateFromIntString(dateString: String): Date {
        val intDateYear = dateString.substring(0,3).toInt()
        val intDateMonth = dateString.substring(4,5).toInt()
        val intDateDay = dateString.substring(6,7).toInt()

        return Date(intDateYear,intDateMonth,intDateDay)
    }

    /**
     * Creates a string of the actual application path
     */
    fun getDirectoryPathToString(): String {
        var strDefaultDir = Environment.getExternalStorageDirectory().toString()
        strDefaultDir+="/DailyDroid/"
        val projectDir = File(strDefaultDir)
        projectDir.mkdir()
        return strDefaultDir
    }

    /**
     * Prefixes a given directory with the app directory to give a full subdir path
     */
    fun getDirectoryPathToString(str_subdir: String): String {
        val strDefaultDir:String = getDirectoryPathToString()+str_subdir
        return strDefaultDir
    }

    /**
     * Gives file coloring if set in preferences by user
     */
    fun getFileColorIntFromPreferences(context: Context, config: Config, strFileName: String): Int {
        var color = Colors.App.CURRENT_PRIMARY
        val str: String? = config.getPreferenceValue(context, "${config.colorpreferencePrefix}strFileName") as String?

        if(!str.isNullOrEmpty()) {
            color = Color.parseColor("#"+str)
            //var hexColor = String.format("#%06X", 0xBBBBCC and color)
            //accentColor = Color.parseColor(hexColor)
        }

        return color
    }

    /*
    TODO - NEED APPLICATIONCONTEXT PASSED IN, THEN USE FOR THESE TO WORK INDIE OF ACTIVITY
    fun preferencesGet() : MutableList<PreferencesModel> {
        var preferenceNames = preferenceNamesGet()
        var preferences: MutableList<PreferencesModel> = mutableListOf<PreferencesModel>()

        for (name in preferenceNames) {
            var preferenceVal = config.getPreferenceValue(applicationC, "${config.preferencePrefix}$name")
            preferences.add(PreferencesModel("$name", preferenceVal))
        }

        return preferences
    }

    var config = Config()
    fun preferencesLoad() {
        var preferences = preferencesGet()

        for (preference in preferences) {
            var preferenceKey = preference.key.substring(0,preference.key.length-4)
            if (preference.value !== null) {
                Log.d("PREFERENCE KEY:", preferenceKey)
                if (preferenceKey == "autosave_enabled") config.performAutoSave = preference.value as Boolean
                if (preferenceKey == "autosave_number") config.autoSaveTrigger = preference.value.toString().toInt()
                if (preferenceKey == "markdown_enabled") config.isMarkdownEnabled = preference.value as Boolean

                if (preferenceKey == "verbose_popups") config.verbosePopups = preference.value as Boolean
                if (preferenceKey == "ui_advanced") config.advancedUI = preference.value as Boolean
                if (preferenceKey == "files_hidden") config.viewHiddenFiles = preference.value as Boolean
                if (preferenceKey == "ui_theme_dark") config.darkThemeEnabled = preference.value as Boolean

            }
        }
        // NOTE: Call preferences are stored in the CallReciever Class //
    }
    */

    /**
     * Applies file coloring to string if set in preferences
     */
    fun getFilenameStringFormattedWithPropertiesColor(context: Context, strFileName: String, config: Config): SpannableString {
        // Parse filename date to get day's letter: [N,M,T,W,R,F,S] //
        val strFileDisplayName = strFileName

        // Set SpannableString color to the existing file color assigned in config //
        val fileNameSpannableStr = SpannableString(strFileDisplayName)
        var colorSpan = ForegroundColorSpan(Colors.GRAY)

        val currentFileColorPref = config.getPreferenceValue(context, "dailydroid__"+strFileName)
        if (currentFileColorPref !== null) {
            val fileColor = currentFileColorPref.toString()
            colorSpan = ForegroundColorSpan(Color.parseColor("#"+fileColor))
        }
        fileNameSpannableStr.setSpan(colorSpan, 0, strFileDisplayName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return fileNameSpannableStr
    }

    /**
     * Gets array of all filenames (no dirs) in the app directory
     */
    fun getListOfAllFilenamesInDir(pathString: String): Array<String?> {
        val filesInDir = getListOfAllFilesInDir(pathString)

        var intFileCounter = 0
        if (filesInDir === null) return arrayOf("No Files")

        val filenamesInDir = arrayOfNulls<String>(filesInDir.size)

        for (currFile in filesInDir) {
            filenamesInDir[intFileCounter] = currFile.getName().toString()
            intFileCounter++
        }

        return filenamesInDir
    }

    /**
     * Gets array of all files in the app dir
     */
    fun getListOfAllFilesInDir(pathString: String): Array<File>? {
        val dir = File(pathString)

        val filesInDir = dir.listFiles()
        if (filesInDir === null) return null

        Arrays.sort(filesInDir, Collections.reverseOrder<Any>())
        return filesInDir
    }

    /**
     * Finds the coordinates of the nearest space to the cursor in a text field
     * You could return all space locations by making a list and inserting into it
     *   instead of reassigning the intClosestPosition
     */
    fun getClosestWhitespaceLocationFromCursor(editText: EditText, cursorPosition: Int = 0, blnLookForward: Boolean = true): Int {
        // Matches all spaces
        val spaceCharRegex = Pattern.compile("\\s")
        val spaceCharMatcher = spaceCharRegex.matcher(editText.text)
        var intClosestPosition = 0

        if (blnLookForward) intClosestPosition = editText.text.length

        while (spaceCharMatcher.find()) {
            val startPos = spaceCharMatcher.start()
            val endPos = spaceCharMatcher.end()

            if (blnLookForward) {
                if (endPos < cursorPosition)
                    continue
                if (endPos > cursorPosition && endPos < intClosestPosition)
                    intClosestPosition = endPos
            }

            if (!blnLookForward) {
                if (startPos > cursorPosition)
                    continue
                if (startPos < cursorPosition && startPos > intClosestPosition)
                    intClosestPosition = startPos
            }
        }

        return intClosestPosition
    }

    /**
     * Creates a simple and reusable toast message without being so verbose
     */
    fun popup(applicationContext: Context, data: Any) {
        android.widget.Toast.makeText(applicationContext, data.toString(), Toast.LENGTH_SHORT).show()
    }

    /**
     * Reads a given file and gets the text in it or an empty string
     */
    @Throws(Exception::class)
    fun readFileContentsToString(file: File): String {
        try {
            return Scanner(file).useDelimiter("\\A").next()
        }catch(e: Exception){
            return ""
        }
    }

    /**
     * Resets a given spinnerAdapter with a new list of items
     *   and optionally returns it
     */
    fun setAdapterWithItems(spinnerAdapter: ArrayAdapter<SpannableString>?, items: MutableList<SpannableString>): ArrayAdapter<SpannableString>? {
        if (spinnerAdapter !== null) {
            spinnerAdapter.clear()
            spinnerAdapter.notifyDataSetChanged()
            spinnerAdapter.addAll(items)
            spinnerAdapter.notifyDataSetChanged()
        }
        return spinnerAdapter
    }

}