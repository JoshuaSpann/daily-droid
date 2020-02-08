/**
 * Daily Droid :: MainActivity
 *
 * PURPOSE:
 *   Landing-point controller for the MainActivity, which is in charge of text editing, time-logging,
 *   file creating/updating, and additional activity selection. Documenting input.
 *
 * MAIN CONTRIBUTORS:
 *   Joshua Spann (jspann) - Author
 *
 * STRUCTURE:
 *   imports
 *   utilty and view declarations
 *   launch controller
 *   app bar menu controllers
 *   button click functions
 *   file management controllers
 *   view controllers
 **/

/* /  IMPORTS  / */
package com.example.jspann.dailydroid

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.*

import java.io.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.ActionBar
import android.view.WindowManager
import android.os.Build
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.*
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.view.inputmethod.InputMethodManager
import android.view.MotionEvent
import android.view.GestureDetector
import android.widget.Scroller
import android.widget.TextView



class MainActivity : AppCompatActivity() {

/****  UTILTY AND VIEW DECLARATIONS  ****/
    // Custom Classes //
    private val utils:Utils = Utils()
    private val config = Config()
    private val markdown = Markdown()
    //config.setDefaultPreferences(this)

    // Permissions //
    val _REQUEST_PERMISSION_RW_EXTERNAL_STORAGE = 1
    val _REQUEST_PERMISSION_READ_CONTACTS = 2
    val _REQUEST_PERMISSION_READ_PHONE_STATE = 3

    // Base Type Properties //
    private var _blnPerformAutoSave: Boolean = false
    private var _intAutoSaveTrigger: Int = 0
    private var _intEditTextStartLength: Int = 0
    private var _strDirPath:String = utils.getDirectoryPathToString()
    private var _strCurrentFileName:String = ""
    private var _isMarkdownEnabled = true
    private var _editTextPosition = 0

    // View-Widget Properties //
    private var _textView_Title: TextView ?= null
    private var _debug_text: TextView ?= null
    private var _editText: EditText ?= null
    private var _spinner: Spinner ?= null

    // Properties Specific to Navigation and Editing //
    private var _buttonStartClickCount :Int = 0
    private var _buttonEndClickCount :Int = 0



/****  LAUNCH CONTROLLER  ****/

    /**
     * Main activity creation
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
        properties_Setup()

        try {
            setTextFieldToLatestFile()
            setCursorToEndOfTxtField()
        } catch (e: Exception) {}

        // ADD FILE SELECTION DROPDOWN TO ALLOW DYNAMIC EDITING //
        fileSelection_Setup()

        /*TODO - ALLOW JOURNAL(entry)S TO BE GROUPED INTO FOLDER AND HAVE USER NAME JOURNAL FOLDER!!!
          [-] MyNamedJournalFolder
           |__[] Auto-JournalEntry.txt
          [-] MyOtherNamedJournalFolder
           |__[] Auto-JournalEntry2.txt
           |__[] Auto-JournalEntryLatest.txt -- Put here by user choice
         */

        // Autosave functionality //
        if (_blnPerformAutoSave) {
            autosave_Setup()
        }

        // COLORIZING FUNCTIONALITY //
        setApplicationColor()

        showKeyboardOnEditTextClick((_editText!!))
    }

    /**
     * Activates When App is Restarted and Has Focus Returned to It
     */
    override fun onRestart() {
        super.onRestart()
        preferencesLoad()
        setApplicationColor()
        resetEditTextToGivenValue()

        // Pull back up the soft keyboard automatically //
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    /**
     * Activates When Leaving or Suspending App
     */
    override fun onPause() {
        super.onPause()
        setApplicationColor()
        resetEditTextToGivenValue()
    }

    /**
     * Activates When Paused App Has Focus Returned to It
     */
    override fun onResume() {
        super.onResume()
        preferencesLoad()
        setApplicationColor()
        resetEditTextToGivenValue()

        // Pull back up the soft keyboard automatically //
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }



/****  APP BAR MENU CONTROLLERS  ****/

    /**
     * ONCE_ONLY: Populates options menu with items from resource XML
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    /**
     * Every time the options menu is opened.
     * Sets certain options' visibility by Advanced UI pref status
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (config.advancedUI != null) {
            (menu!!).findItem(R.id.menuitem_open).setVisible(config.advancedUI)
            (menu!!).findItem(R.id.menuitem_save).setVisible(config.advancedUI)
            (menu!!).findItem(R.id.menuitem_export_settings).setVisible(config.advancedUI)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * Runs on item selected from the Options Menu
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val blnRetItem = super.onOptionsItemSelected(item)
        when(item?.itemId){
            R.id.menuitem_open -> {
                launchFileSelectionSpinner()
            }
            R.id.menuitem_add -> {
                createNewTextFile()
            }
            R.id.menuitem_changecolor -> {
                this.launchColorSelectorActivity()
            }
            R.id.menuitem_settings -> {
                launchSettingsActivity()
            }
            R.id.menuitem_export_settings -> {
                this.preferencesExport()
            }
        }
        return blnRetItem
    }

    /**
     * Checks that the Base, Needed Permissions to Run the App are Granted, if not then Request them
     */
    private fun checkPermissions(){
        // GET MANDATORY STORAGE PERMISSION //
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                utils.createDialog(this, "Permission Needed", "Daily Droid needs Storage to allow you to save text files!", fun(isOkClicked: Boolean){
                    if (isOkClicked) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), _REQUEST_PERMISSION_RW_EXTERNAL_STORAGE)
                        finish()
                        startActivity(intent)
                        return
                    }
                    utils.popup(this, "Can't Read or Save files")
                    finish()
                    return
                })
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), _REQUEST_PERMISSION_RW_EXTERNAL_STORAGE)
            }
        }
    }

    /**
     * Undoes any unsaved changes made within the main EditText
     */
    fun resetEditTextToGivenValue(){
        this.setEditTextToFileContents(_strCurrentFileName)
    }



/****  BUTTON CLICK FUNCTIONS  ****/

    /**
     * Runs on the taps/clicks of the |< button
     */
    fun button_toolbar_startoftext__click(view: View) {
        _buttonEndClickCount = 0
        _buttonStartClickCount++

        if (_buttonStartClickCount == 1) {
            this.moveCursorBackOneWord()
        }
        if (_buttonStartClickCount == 2) {
            this.setCursorToStartOfLine()
        }
        if (_buttonStartClickCount >= 3) {
            this.setCursorToStartOfTxtField()
            _buttonStartClickCount = 0
        }

        this.resetButtonNavigationProperties()
    }

    /**
     * Runs on the floppy disk save button tap/click
     */
    @Throws(Exception::class)
    fun button_toolbar_save__click(view: View) {
        this.saveToFile()
        this.setEditTextToFileContents(_strCurrentFileName)
        if (config.verbosePopups) utils.popup(this, "${resources.getString(R.string.app_notify_file_saved)}")
    }

    /**
     * Runs on the timestamp button tap/click
     */
    fun button_toolbar_timestamp__click(view: View) {
        this.insertTimestampToEditText()
    }

    /**
     * Runs on the taps/clicks of the >| button
     */
    fun button_toolbar_endoftext__click(view: View){
        _buttonStartClickCount = 0
        _buttonEndClickCount++

        if (_buttonEndClickCount == 1) {
            this.moveCursorForwardOneWord()
        }
        if (_buttonEndClickCount == 2) {
            this.setCursorToEndOfLine()
        }
        if (_buttonEndClickCount >= 3) {
            this.setCursorToEndOfTxtField()
            _buttonEndClickCount = 0
        }

        this.resetButtonNavigationProperties()
    }

    /**
     * Runs on the reset/refresh button tap/click
     */
    fun button_toolbar_refreshtext__click(view: View){
        // Todo - Run check for no files in dir then return
        this.resetEditTextToGivenValue()
    }




/****  FILE MANAGEMENT CONTROLLERS  ****/

    /**
     * Automatically saves after so many new characters have been entered
     */
    private fun autosave_Setup(){
        /* Listener for characters time period after (x# characters?) entered.
           Listen every few seconds and see if new content then save?
           Which is more efficient?
        */
        var charCount = 0
        property_ResetEditTextLength()

        var strOldFileName = _strCurrentFileName

        (_editText!!).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(strOldFileName != _strCurrentFileName){
                    strOldFileName = _strCurrentFileName
                    return
                }
                if (charCount > _intAutoSaveTrigger
                        && (_editText!!).length() > _intEditTextStartLength
                        && strOldFileName == _strCurrentFileName) {
                    saveToFile()
                    charCount = 0
                    property_ResetEditTextLength()
                    return
                }
                charCount++
            }
        })


        /*
        //CODE FOR ADDING CUSTOM FUNCTION TO EXISTING ANDROID API OBJECT:
        fun EditText.customOnTextChanged(afterTextChanged: (String) -> Unit) {
            this.addTextChangedListener(object : TextWatcher {
                ...
            })
        }
        //IMPLEMENTATION OF THE ABOVE
        (_editText!!).customOnTextChanged { saveToFile() }*/
    }

    /**
     * Creates new files in the app directory
     */
    private fun createNewTextFile(){
        val newFile = File(_strDirPath, utils.getCurrentFormattedDateAsString() + ".txt")

        if (newFile.exists()) {
            return
        }

        try {
            File(_strDirPath).mkdir()
            utils.file_Append(newFile, ("# " + utils.getCurrentFormattedDateAsString() + utils.getCurrentTimeStampAsString() + "\n\n---\n\n"))

            this.setTextFieldToLatestFile()
            this.setSpinnerItems(utils.getListOfAllFilenamesInDir(_strDirPath))
        } catch (e: Exception) {
            Log.d("JSDEV: ", e.toString()+"\n\t_strDirPath= "+_strDirPath)
            utils.popup(applicationContext, e)
        }
        setCursorToEndOfTxtField()
    }

    /**
     * Sets spinner item list, performs actions on a spinner item click/tap
     */
    private fun fileSelection_Setup(){
        setSpinnerItems(utils.getListOfAllFilenamesInDir(_strDirPath))
        (_spinner!!).onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setEditTextToFileContents((_spinner!!).selectedItem.toString())
                setChosenFilename((_spinner!!).selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //not needed outside of making sure the object signature matches (allows use of above fn without errors//
            }
        }
    }

    /**
     * Saves main EditText data to the current working file
     */
    private fun saveToFile(){
        var strDataBody = (_editText!!).text.toString()
        try {
            val dteToday = utils.getCurrentFormattedDateAsString()
            val rootPath = File(_strDirPath)
            rootPath.mkdir()

            val file = File(rootPath, _strCurrentFileName)
            if (!file.exists()) {
                strDataBody = dteToday + "\n\n---\n\n" + strDataBody
            }

            utils.file_Append(file, strDataBody)

            property_ResetEditTextLength()
            (_debug_text!!).text = "Last Saved: "+utils.getCurrentTimeStampAsString()
        } catch (e: Exception) {}
    }


    /* /  PROPERTY CONTROLLERS  / */
    private fun property_ResetEditTextLength(){
        _intEditTextStartLength = (_editText!!).length()
    }


/****  PREFERENCES, SETTINGS, AND APP DATA  ****/

    /**
     * Gets all preferences and writes them as JSON to the given file
     */
    private fun preferencesExport(strFileName: String = ".dailydroid.preferences") {
        var preferences = preferencesGet()
        var preferncesString = preferencesToJSONString(preferences)

        var preferencesFile = File(_strDirPath, strFileName)

        try {
            File(_strDirPath).mkdir()
            utils.file_Write(preferencesFile, preferncesString)
            if (config.verbosePopups) utils.popup(this, "${resources.getString(R.string.app_notify_file_saved_to)} $_strDirPath$strFileName")
        } catch (e: Exception) {
            Log.d("JSDEV: ", "ERROR WRITING PREFS: ${e.toString()}\n\t_strDirPath= "+_strDirPath)
            utils.popup(applicationContext, e)
        }

        // NOTE: Call preferences are stored in the CallReciever Class //
    }

    /**
     * Gets all preferences/values into a data model and returns as an array
     */
    private fun preferencesGet() : MutableList<PreferencesModel> {
        var preferenceNames = preferenceNamesGet()
        var preferences: MutableList<PreferencesModel> = mutableListOf<PreferencesModel>()

        for (name in preferenceNames) {
            var preferenceVal = config.getPreferenceValue(this, "${config.preferencePrefix}$name")
            preferences.add(PreferencesModel("$name", preferenceVal))
        }

        return preferences
    }

    /**
     * Check for preferences and enable them if they're set (Will be set once user opens Menu.Settings.General for the 1st time)
     */
    private fun preferencesLoad() {
        var preferences = preferencesGet()

        for (preference in preferences) {
            var preferenceKey = preference.key.substring(0,preference.key.length-4)
            if (preference.value !== null) {
                Log.d("PREFERENCE KEY:", preferenceKey)
                if (preferenceKey == "autosave_enabled") _blnPerformAutoSave = preference.value as Boolean
                if (preferenceKey == "autosave_number") _intAutoSaveTrigger = preference.value.toString().toInt()
                if (preferenceKey == "markdown_enabled") _isMarkdownEnabled = preference.value as Boolean
                if (preferenceKey == "fancy_scroll_enabled"
                        && preference.value as Boolean === true
                        && (_editText !== null)) addFlingScrollingToEditText((_editText!!))
                if (preferenceKey == "verbose_popups") config.verbosePopups = preference.value as Boolean
                if (preferenceKey == "ui_advanced") config.advancedUI = preference.value as Boolean
                if (preferenceKey == "files_hidden") config.viewHiddenFiles = preference.value as Boolean
            }
        }
        // NOTE: Call preferences are stored in the CallReciever Class //
    }

    /**
     * Returns the preference and setting names with option for global (android ecosystem name)
     *   or shortened name without dailyDroid prefix (default)
     */
    private fun preferenceNamesGet(getAsGlobal:Boolean = false) : MutableList<String>
    {
        var preferenceNames = mutableListOf<String>(
                "autosave_enabled_bln",
                "autosave_number_num",
                "markdown_enabled_bln",
                "fancy_scroll_enabled_bln",
                "verbose_popups_bln",
                "ui_advanced_bln",
                "files_hidden_bln"
        )
        var preferenceNamesGlobal = mutableListOf<String>()

        for (prefernceName in preferenceNames) {
            var name = prefernceName
            if (getAsGlobal == true) {
                name = "${config.preferencePrefix}$name"
                utils.popup(applicationContext, name)
            }
            preferenceNamesGlobal.add(name)
        }
        return preferenceNamesGlobal
    }

    /**
     * Iterates through preferenes and converts them to JSON
     */
    private fun preferencesToJSONString(preferences:MutableList<PreferencesModel>) : String {
        var preferncesString = "["

        for (preferences_i in 0..preferences.size-1) {
            val preference = preferences[preferences_i]
            val preferenceKey = preference.key.substring(0, preference.key.length-4)
            var preferenceVal: Any? = preference.value
            var c_preferenceType = preferenceTypeGet(preference)

            if (preferenceVal != null) {
                if (c_preferenceType == "bln") preferenceVal as Boolean
                if (c_preferenceType == "num") preferenceVal = (preferenceVal as String).toInt()
                if (c_preferenceType == "str") preferenceVal = "\"${preferenceVal as String}\""
            }

            var name = "\"name\""
            var type = "\"type\""
            var value = "\"value\""

            var currentLine = "{$name:\"$preferenceKey\", $type:\"$c_preferenceType\", $value:$preferenceVal}"
            if (preferences_i < preferences.size-1) {
                currentLine = "$currentLine,"
            }

            preferncesString += currentLine
        }

        preferncesString += ", \"files\":${preferencesMappedFileColorsGet()}"

        preferncesString += "]"

        return preferncesString
    }

    private fun preferencesMappedFileColorsGet() :String {
        var fileColorsString = "["
        val files = utils.getListOfAllFilenamesInDir(_strDirPath)

        for (files_i in 0..files.size-1) {
            val filename = files[files_i]

            val curColorPrefixName = "${config.colorpreferencePrefix}$filename"
            val fileColor = config.getPreferenceValue(this, curColorPrefixName) as String?

            var curLineString = "{\"filename\":\"$filename\", \"color\":\"$fileColor\"}"
            if (files_i < files.size-1) curLineString += ", "

            fileColorsString += curLineString
            //if(!fileColor.isNullOrEmpty())
        }
        fileColorsString += "]"

        return fileColorsString
    }

    /**
     * Gets the data type as a string from the preference name's extension
     */
    private fun preferenceTypeGet(preference: PreferencesModel) : String {
        var preferenceType = preference.key.substring(preference.key.length-3, preference.key.length)
        return preferenceType
    }

    /**
     * Loads preferences and sets all view widgets to be modified
     */
    private fun properties_Setup(){

        this.preferencesLoad()

        _debug_text = (findViewById(R.id.debug_text))
        _editText = (findViewById<View>(R.id.editText) as EditText)
        _intEditTextStartLength = (_editText!!).length()
        _spinner = findViewById<View>(R.id.spinner) as Spinner
        _textView_Title = (findViewById(R.id.textView_Title))
        (_textView_Title!!).setOnClickListener {
            launchFileSelectionSpinner()
        }
    }



/****  VIEW CONTROLLERS  ****/

//*//   ALL/MANY VIEW ELEMENTS   //*//
    fun setApplicationColor(){
        if(_strCurrentFileName.isNullOrEmpty()) {
            return
        }

        var color = resources.getColor(R.color.colorPrimary)
        var accentColor = Color.parseColor(String.format("#%06X", 0xBBBBCC and resources.getColor(R.color.colorAccent)))
        val str: String? = config.getPreferenceValue(this, "${config.colorpreferencePrefix}$_strCurrentFileName") as String?

        if(!str.isNullOrEmpty()) {
            color = Color.parseColor("#"+str)
            val hexColor = String.format("#%06X", 0xBBBBCC and color)
            accentColor = Color.parseColor(hexColor)
        }

        Colors.App.CURRENT_PRIMARY = color
        Colors.App.CURRENT_ACCENT = accentColor
        (findViewById<View>(R.id.toolbar3) as android.support.v7.widget.Toolbar).setBackgroundColor(color)
        (_textView_Title!!).setTextColor(color)
        (_debug_text!!).setTextColor(accentColor)
        (findViewById<View>(R.id.view) as View).setBackgroundColor(accentColor)

        val cd = ColorDrawable(color)
        var ab: ActionBar? = supportActionBar
        (ab!!).setBackgroundDrawable(cd)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }

//*//   ACTIVITY MODIFIERS   //*//
    /**
     * Opens the color selection activity view
     */
    private fun launchColorSelectorActivity(){
        val intent = Intent(this, ColorSelectorActivity::class.java)
        intent.putExtra("currentSelectedFile", _strCurrentFileName)
        startActivity(intent)
    }

    /**
     * Opens the settings activity view
     */
    private fun launchSettingsActivity(){
        val intent = Intent(this, SettingsActivity::class.java)
        // TODO - TRY loading fragment for SettingsActivity: val intent = Intent(this, SettingsActivity::GeneralPreferenceFragment.javaClass)
        startActivity(intent)
    }



//*//   EDIT TEXT FUNCTIONS   //*//
    /**
     * Adds scrolling capabilities to a supplied EditText
     */
    private fun addFlingScrollingToEditText(editText: EditText) {
        val scroller = Scroller(this)
        editText.setScroller(scroller)
        editText.setOnTouchListener(object : View.OnTouchListener {

            // Could make this a field member on your activity
            internal var gesture = GestureDetector(this@MainActivity, object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                    scroller.fling(0, editText.scrollY, 0, (-velocityY).toInt(), 0, 0, 0, editText.lineCount * editText.lineHeight)
                    return super.onFling(e1, e2, velocityX, velocityY)
                }

            })

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return gesture.onTouchEvent(event)
                return false
            }
        })
    }

    /**
     * "Loads" a new working file's info and contents into the editor
     */
    @Throws(Exception::class)
    private fun setEditTextToFileContents_and_setTextFieldToFileName(file: File) {
        this.setEditTextToFileContents(file.name)
        setChosenFilename(file.name)
    }

    /**
     * Applies markdown formatting and colorization (if it exists) and returns the string span
     */
    private fun getMarkdownColoredTextFromFile(filename: String, text: String): SpannableString {
        // Perform Markdown Styling //
        if (_isMarkdownEnabled) {
            val color = utils.getFileColorIntFromPreferences(this, this.config, filename)
            val hexColor = String.format("#%06X", 0xFFBBBBCC.toInt() and color)
            val accentColor = Color.parseColor(hexColor)

            return markdown.formatFromString(text, color, accentColor)
        }

        return SpannableString(text)
    }

    /**
     * Populates the edit text with the text from a given file
     */
    private fun setEditTextToFileContents(p_strFileName: String){
        var file = File(_strDirPath, p_strFileName)
        _strCurrentFileName = file.name
        _intEditTextStartLength = file.length().toInt()

        val rawFileText = utils.readFileContentsToString(file)

        this.setApplicationColor()
        val highlightedMarkdownText = getMarkdownColoredTextFromFile(file.name, rawFileText)

        _editTextPosition = (_editText!!).selectionStart
        (_editText!!).setText(highlightedMarkdownText)

        // Ensure that cursor is in most current position or the beginning of file //
        try {
            (_editText!!).setSelection(_editTextPosition)
        }
        catch (e: Exception) {
            (_editText!!).setSelection(0)
        }
    }


//*//   FOOTER ACTION BUTTON FUNCTIONS   //*//
    /**
     * Inserts a "- ####:  " timestamp at the line under the cursor
     */
    private fun insertTimestampToEditText(){
        val txtMain = findViewById<EditText>(R.id.editText)
        val strTimestamp: String = "\n - "+utils.getCurrentTimeStampAsString()+":  "
        if (txtMain.selectionStart < 0) return
        txtMain.text.insert(txtMain.selectionStart, strTimestamp)
    }

    /**
     * Resets the nav button counters around 1 second (allows nav by word/line/document)
     */
    fun resetButtonNavigationProperties() {
        Handler().postDelayed({
            _buttonStartClickCount = 0
            _buttonEndClickCount = 0
        },370)
    }

    /**
     * Works like the End key
     */
    private fun setCursorToEndOfLine() {
        val txtMain: EditText = (_editText!!)
        val cursorLine = this.getCursorLineFromEditText(txtMain)
        val endOfLineIndex = txtMain.layout.getLineVisibleEnd(cursorLine)
        txtMain.setSelection(endOfLineIndex)
    }

    /**
     * Works like the Ctrl+End keypresses
     */
    private fun setCursorToEndOfTxtField(){
        val txtMain: EditText = (_editText!!)
        txtMain.setSelection(txtMain.text.length)
    }

    /**
     * Works like the Home key
     */
    private fun setCursorToStartOfLine(){
        val txtMain: EditText = (_editText!!)
        val cursorLine = this.getCursorLineFromEditText(txtMain)
        val startOfLineIndex = txtMain.layout.getLineStart(cursorLine)
        txtMain.setSelection(startOfLineIndex)
    }

    /**
     * Works like Ctrl+LeftArrow
     */
    private fun moveCursorBackOneWord() {
        this.moveCursor(false)
    }

    /**
     * Works like Ctrl+RightArrow
     */
    private fun moveCursorForwardOneWord() {
        this.moveCursor(true)
    }

    /**
     * Determines which direction to move the cursor in the main EditText
     */
    private fun moveCursor(blnForward: Boolean) {
        val txtMain: EditText = (_editText!!)
        val intCursorLocation = txtMain.selectionStart
        val intClosestWhiteSpaceLocation = utils.getClosestWhitespaceLocationFromCursor(txtMain, intCursorLocation, blnForward)

        txtMain.setSelection(intClosestWhiteSpaceLocation)
        _editTextPosition = intClosestWhiteSpaceLocation
    }

    /**
     * Returns the cursor's current line or an error code
     */
    private fun getCursorLineFromEditText(editText: EditText) :Int{
        val selectionStartLocation = Selection.getSelectionStart(editText.text)
        val txtMainLayout = editText.layout

        if (selectionStartLocation != -1) {
            return txtMainLayout.getLineForOffset(selectionStartLocation)
        }
        return -1
    }

    /**
     * Works like the Ctrl+Home key combo
     */
    private fun setCursorToStartOfTxtField(){
        val txtMain: EditText = (_editText!!)
        txtMain.setSelection(0)
    }



//*//   SPINNER FUNCTIONS   //*//
    private var _spinnerAdapter: ArrayAdapter<SpannableString>? = null
    /**
     * Updates spinner formatting and opens the file selection spinner
     */
    private fun launchFileSelectionSpinner() {
        this.setSpinnerItems(utils.getListOfAllFilenamesInDir(_strDirPath))
        (_spinner!!).performClick()
    }

    /**
     * Creates the spinner's file listing with appropriate file colors
     */
    private fun setSpinnerItems(p_strItems: Array<String?>) {
        // Use items as a list cleared of non *.* files //
        var items: MutableList<SpannableString> = mutableListOf()

        for (i in 0 until p_strItems.size) {
            if ((p_strItems[i]!!).contains(".")) {
                var fileNameSpannableStr = utils.getFilenameStringFormattedWithPropertiesColor(this, p_strItems[i].toString(), config)
                if (fileNameSpannableStr.toString() == _strCurrentFileName) {
                    var activeFileSpan = android.text.style.StyleSpan(android.graphics.Typeface.BOLD_ITALIC)
                    fileNameSpannableStr.setSpan(activeFileSpan, 0, fileNameSpannableStr.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                items.add(fileNameSpannableStr)
            }
        }

        if (_spinnerAdapter !== null) {
            utils.setAdapterWithItems(_spinnerAdapter, items)
            return
        }

        // Assign items to spinner. Reassigning the adapter will load the latest file each click //
        _spinnerAdapter = ArrayAdapter<SpannableString>(this, android.R.layout.simple_spinner_item, items)

        // Assign visual styling to spinner //
        (_spinnerAdapter!!).setDropDownViewResource(R.layout.spinner_item)
        (_spinner!!).adapter = _spinnerAdapter
        (_spinner!!).prompt = "Open File"
    }


//*//   TEXT FIELD FUNCTIONS  //*//
    /**
     * Changes the filename TextView to the supplied/chosen filename
     */
    private fun setChosenFilename(p_strFileName: String){
        (_debug_text!!).text = _strDirPath + p_strFileName
        (_textView_Title!!).text = (p_strFileName).substring(0,(p_strFileName).length-4)
    }

    /**
     * Finds the "newest" or "largest" filename and sets the title TextField to the filename without the extension
     */
    @Throws(Exception::class)
    private fun setTextFieldToLatestFile() {
        val files = utils.getListOfAllFilesInDir(_strDirPath)
        if (files === null) return
        val latestFile = files[0]
        this._strCurrentFileName = latestFile.name
        setEditTextToFileContents_and_setTextFieldToFileName(latestFile)
    }

    /**
     * Force the softkeyboard to become visible whenever the supplied editText is clicked
     */
    private fun showKeyboardOnEditTextClick(element: EditText) {
        element.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (editText.requestFocus()) {
                    val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    //mgr.hideSoftInputFromWindow((_editText!!).windowToken, 0)
                    mgr.showSoftInput((_editText!!), InputMethodManager.SHOW_IMPLICIT)
                }
            }
        })
    }

}