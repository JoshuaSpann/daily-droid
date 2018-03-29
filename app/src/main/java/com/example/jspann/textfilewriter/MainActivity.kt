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
package com.example.jspann.textfilewriter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*

import java.io.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.ActionBar
import android.view.WindowManager
import android.os.Build
import android.text.SpannableString
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.view.inputmethod.InputMethodManager

//import sun.swing.plaf.synth.Paint9Painter.PaintType




class MainActivity : AppCompatActivity() {

    /* /  UTILTY AND VIEW DECLARATIONS  / */
    // Custom Classes //
    private val utils:Utils = Utils()
    private val config = Config()
    private val markdown = Markdown()
    //config.setDefaultPreferences(this)

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

    // System Properties //
    //private var _phoneStateListener: PhoneStateListener
    //private val _sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

    /* /  LAUNCH CONTROLLER  / */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        properties_Setup()

        try {
            setTextFieldToLatestFile()
            setCursorToEndOfTxtField()
        } catch (e: Exception) {
            //utils.popup(applicationContext, e)
        }

        // ADD FILE SELECTION DROPDOWN TO ALLOW DYNAMIC EDITING //
        fileSelection_Setup()

        /*TODO - ALLOW JOURNAL(entry)S TO BE GROUPED INTO FOLDER AND HAVE USER NAME JOURNAL FOLDER!!!
          [-] MyNamedJournalFolder
           |__[] Auto-JournalEntry.txt
          [-] MyOtherNamedJournalFolder
           |__[] Auto-JournalEntry2.txt
           |__[] Auto-JournalEntryLatest.txt -- Put here by user choice
         */

        // COLORIZING FUNCTIONALITY //
        setApplicationColor()

        // AUTO-SAVE FUNCTIONALITY //
        if(_blnPerformAutoSave) {
            autosave_Setup()
        }

        // Force the softkeyboard to become visible whenever editText is clicked //
        (_editText!!).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (editText.requestFocus()) {
                    val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    //mgr.hideSoftInputFromWindow((_editText!!).windowToken, 0)
                    mgr.showSoftInput((_editText!!), InputMethodManager.SHOW_IMPLICIT)
                }
            }
        })
    }


    /**
     * Activates When App is Restarted and Has Focus Returned to It
     */
    override fun onRestart() {
        super.onRestart()
        loadPreferences()
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
        loadPreferences()
        setApplicationColor()
        resetEditTextToGivenValue()

        // Pull back up the soft keyboard automatically //
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    /* /  APP BAR MENU CONTROLLERS  / */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val blnRetItem = super.onOptionsItemSelected(item)
        when(item?.itemId){
            R.id.menuitem_open -> {
                (_spinner!!).performClick()
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
        }
        return blnRetItem
    }
    fun resetEditTextToGivenValue(){
        //this.editTextField.text = this.originalEditTextContent
        setEditTextToFileContents(_strCurrentFileName)
        //setCursorToCurrentPositionOfTxtField()
    }


    /* /  BUTTON CLICK FUNCTIONS  / */
    fun button_toolbar_new__click(view: View) {
        this.createNewTextFile()
    }

    fun button_toolbar_startoftext__click(view: View) {
        this.setCursorToEndOfTxtField()
        setCursorToStartOfTxtField()
    }

    @Throws(Exception::class)
    fun button_toolbar_save__click(view: View) {
        this.saveToFile()
        setEditTextToFileContents(_strCurrentFileName)
    }

    fun button_toolbar_timestamp__click(view: View) {
        this.insertTimestampToEditText()
    }

    fun button_toolbar_endoftext__click(view: View){
        this.setCursorToEndOfTxtField()
    }

    fun button_toolbar_refreshtext__click(view: View){
        // Todo - Run check for no files in dir then return
        this.resetEditTextToGivenValue()
    }

    fun button_toolbar_mdDream__click(view: View){
        this.insertDreamMarkdownToEditText()
    }


    /* /  FILE MANAGEMENT CONTROLLERS  / */
    private fun autosave_Setup(){
        /* Listener for characters time period after (x# characters?) entered.
           Listen every few seconds and see if new content then save?
           Which is more efficient?
        */
        var charCount = 0
        property_ResetEditTextLength()

        var strOldFileName = _strCurrentFileName

        (_editText!!).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

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

    private fun fileSelection_Setup(){
        setSpinnerItems(utils.getListOfAllFilenamesInDir(_strDirPath))
        (findViewById<View>(R.id.spinner) as Spinner).onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setEditTextToFileContents((_spinner!!).selectedItem.toString())
                setChosenFilename((_spinner!!).selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //not needed outside of making sure the object signature matches (allows use of above fn without errors//
            }
        }
    }

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
        } catch (e: Exception) {
            //utils.popup(applicationContext, e)
        }
    }


    /* /  PROPERTY CONTROLLERS  / */
    private fun property_ResetEditTextLength(){
        _intEditTextStartLength = (_editText!!).length()
    }

    /**
     * Check for preferences and enable them if they're set (Will be set once user opens Menu.Settings.General for the 1st time)
     */
    private fun loadPreferences() {
        var daily_droid__pref_autosave_enabled = config.getPreferenceValue(this, "daily_droid__pref_autosave_enabled")
        var daily_droid__pref_autosave_number = config.getPreferenceValue(this, "daily_droid__pref_autosave_number")
        var daily_droid__pref_markdown_enabled = config.getPreferenceValue(this, "daily_droid__pref_markdown_enabled")

        // Check for Autosave preferences //
        if (daily_droid__pref_autosave_enabled !== null) {
            _blnPerformAutoSave = daily_droid__pref_autosave_enabled as Boolean

            if (daily_droid__pref_autosave_number !== null) {
                _intAutoSaveTrigger = daily_droid__pref_autosave_number.toString().toInt()
            }
        }

        // Check for Markdown preferences //
        if (daily_droid__pref_markdown_enabled !== null) {
            _isMarkdownEnabled = daily_droid__pref_markdown_enabled as Boolean
        }

        // Call preferences are stored in the CallReciever Class //
    }

    private fun properties_Setup(){

        loadPreferences()

        _debug_text = (findViewById(R.id.debug_text))
        _editText = (findViewById<View>(R.id.editText) as EditText)
        _intEditTextStartLength = (_editText!!).length()
        _spinner = findViewById<View>(R.id.spinner) as Spinner
        _textView_Title = (findViewById(R.id.textView_Title))
        (_textView_Title!!).setOnClickListener { (_spinner!!).performClick() }

        /*
        // This prevents edit/copy/paste
        (_editText!!).setMovementMethod(ScrollingMovementMethod())
        (_editText!!).setTextIsSelectable(true)
        (_editText!!).isFocusableInTouchMode = true
        */
    }


    /* /  VIEW CONTROLLERS  / */
    /*   ALL/MANY VIEW ELEMENTS   */
    fun setApplicationColor(){
        if(_strCurrentFileName.isNullOrEmpty()) {
            return
        }

        var color = resources.getColor(R.color.colorPrimary)
        var accentColor = Color.parseColor(String.format("#%06X", 0xBBBBCC and resources.getColor(R.color.colorAccent)))
        var str: String? = config.getPreferenceValue(this, "dailydroid__"+_strCurrentFileName) as String?

        if(!str.isNullOrEmpty()) {
            color = Color.parseColor("#"+str)
            var hexColor = String.format("#%06X", 0xBBBBCC and color)
            accentColor = Color.parseColor(hexColor)
        }

        (findViewById<View>(R.id.toolbar3) as android.support.v7.widget.Toolbar).setBackgroundColor(color)
        (_textView_Title!!).setTextColor(color)
        (_debug_text!!).setTextColor(accentColor)
        (findViewById<View>(R.id.view) as View).setBackgroundColor(accentColor)

        var cd = ColorDrawable(color)
        var ab: ActionBar? = supportActionBar
        (ab!!).setBackgroundDrawable(cd)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }

    /*   ACTIVITY MODIFIERS   */
    private fun launchColorSelectorActivity(){
        val intent = Intent(this, ColorSelectorActivity::class.java)
        intent.putExtra("currentSelectedFile", _strCurrentFileName)
        startActivity(intent)
    }
    private fun launchSettingsActivity(){
        val intent = Intent(this, SettingsActivity::class.java)
        // TODO - TRY loading fragment for SettingsActivity: val intent = Intent(this, SettingsActivity::GeneralPreferenceFragment.javaClass)
        startActivity(intent)
    }

    /*   EDIT TEXT FUNCTIONS   */
    private fun insertDreamMarkdownToEditText(){
        val txtMain = findViewById<EditText>(R.id.editText)
        val strDreamMd: String = "\n\n```\n\n```\n"
        val txtMain_cursorPosition = txtMain.selectionStart
        txtMain.text.insert(txtMain_cursorPosition, strDreamMd)
        txtMain.setSelection(txtMain_cursorPosition + 6)
    }

    private fun insertTimestampToEditText(){
        val txtMain = findViewById<EditText>(R.id.editText)
        val strTimestamp: String = "\n - "+utils.getCurrentTimeStampAsString()+":  "
        if (txtMain.selectionStart < 0) return
        txtMain.text.insert(txtMain.selectionStart, strTimestamp)
    }

    private fun setCursorToCurrentPositionOfTxtField() {
        val txtMain: EditText = (_editText!!)
        txtMain.setSelection(_editTextPosition)
    }
    private fun setCursorToEndOfTxtField(){
        val txtMain: EditText = (_editText!!)
        txtMain.setSelection(txtMain.text.length)
    }
    private fun setCursorToStartOfTxtField(){
        val txtMain: EditText = (_editText!!)
        txtMain.setSelection(0)
    }

    /*   SPINNER FUNCTIONS   */
    private fun setSpinnerItems(p_strItems: Array<String?>){

        // Use items as a list cleared of non *.* files //
        var items: MutableList<String> = mutableListOf()

        for (i in 0 until p_strItems.size) {
            if ((p_strItems[i]!!).contains(".")) items.add(p_strItems[i].toString())
        }

        // Assign items to spinner //
        var spinner = findViewById<View>(R.id.spinner) as Spinner
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items)

        // Assign visual styling to spinner //
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.adapter = adapter
        spinner.prompt = "Open File"

    }

    /*   TEXT FIELD FUNCTIONS  */
    private fun setChosenFilename(p_strFileName: String){
        (_debug_text!!).text = _strDirPath + p_strFileName
        (_textView_Title!!).text = (p_strFileName).substring(0,(p_strFileName).length-4)
    }

    @Throws(Exception::class)
    private fun setEditTextToFileContents_and_setTextFieldToFileName(file: File) {
        _strCurrentFileName = file.name
        property_ResetEditTextLength()

        val strOriginalText = utils.readFileContentsToString(file)
        var formattedText = SpannableString(strOriginalText)

        if (_isMarkdownEnabled) {
            formattedText = markdown.formatFromString(strOriginalText)
        }

        (_editText!!).setText(formattedText)
        (editText!!).setSelection(formattedText.length)
        setChosenFilename(file.name)
    }

    private fun setEditTextToFileContents(p_strFileName: String){
        var file = File(_strDirPath, p_strFileName)
        _strCurrentFileName = file.name
        _intEditTextStartLength = file.length().toInt()

        val rawFileText = utils.readFileContentsToString(file)
        var highlightedMarkdownText = SpannableString(rawFileText)

        // Perform Markdown Styling //
        if (_isMarkdownEnabled) {
            highlightedMarkdownText = markdown.formatFromString(rawFileText)
        }

        _editTextPosition = (_editText!!).selectionStart
        (_editText!!).setText(highlightedMarkdownText)

        // Ensure that cursor is in most current position or the beginning of file //
        try {
            (_editText!!).setSelection(_editTextPosition)
        }
        catch (e: Exception) {
            (_editText!!).setSelection(0)
        }

        setApplicationColor()
    }

    @Throws(Exception::class)
    private fun setTextFieldToLatestFile() {
        val files = utils.getListOfAllFilesInDir(_strDirPath)
        if (files === null) return
        val latestFile = files[0]
        this._strCurrentFileName = latestFile.name
        setEditTextToFileContents_and_setTextFieldToFileName(latestFile)
    }

}