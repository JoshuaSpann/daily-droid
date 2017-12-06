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

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*

import java.io.*


class MainActivity : AppCompatActivity() {

    /* /  UTILTY AND VIEW DECLARATIONS  / */
    // Custom Classes //
    private val utils:Utils = Utils()

    // Base Type Properties //
    private var _blnPerformAutoSave: Boolean = true
    private var _intAutoSaveTrigger: Int = 0
    private var _intEditTextStartLength: Int = 0
    private var _strDirPath:String = utils.getDirectoryPathToString()
    private var _strCurrentFileName:String = ""

    // View-Widget Properties //
    private var _textView_Title: TextView ?= null
    private var _debug_text: TextView ?= null
    private var _editText: EditText ?= null
    private var _spinner: Spinner ?= null

    /* /  LAUNCH CONTROLLER  / */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setSupportActionBar(findViewById<View>(R.id.toolbar2) as Toolbar)
        _textView_Title = (findViewById(R.id.textView_Title))
        (_textView_Title!!).setOnClickListener { (_spinner!!).performClick() }
        _debug_text = (findViewById(R.id.debug_text))
        _editText = (findViewById<View>(R.id.editText) as EditText)
        _intEditTextStartLength = (_editText!!).length()
        _spinner = findViewById<View>(R.id.spinner) as Spinner

        try {
            setTextFieldToLatestFile()
            setCursorToEndOfTxtField()
        } catch (e: Exception) {
            //utils.popup(applicationContext, e)
        }

        // ADD FILE SELECTION DROPDOWN TO ALLOW DYNAMIC EDITING //
        this.setSpinnerItems(utils.getListOfAllFilenamesInDir(_strDirPath))
        (findViewById<View>(R.id.spinner) as Spinner).onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                setEditTextToFileContents((_spinner!!).selectedItem.toString())
                setChosenFilename((_spinner!!).selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //not needed outside of making sure the object signature matches (allows use of above fn without errors//
            }
        }
        /*TODO - ALLOW JOURNAL(entry)S TO BE GROUPED INTO FOLDER AND HAVE USER NAME JOURNAL FOLDER!!!
          [-] MyNamedJournalFolder
           |__[] Auto-JournalEntry.txt
          [-] MyOtherNamedJournalFolder
           |__[] Auto-JournalEntry2.txt
           |__[] Auto-JournalEntryLatest.txt -- Put here by user choice
         */

        //TODO - ADD COLORIZING FUNCTIONALITY

        //TODO - ADD AUTO-SAVE FUNCTIONALITY!!!
        /* Listener for characters time period after (x# characters?) entered? */
        if(_blnPerformAutoSave) {
            var charCount = 0
            _intEditTextStartLength = (_editText!!).length()

            (_editText!!).addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (charCount > _intAutoSaveTrigger && (_editText!!).length() > _intEditTextStartLength) {
                        utils.popup(applicationContext, "Saving...")
                        saveToFile()
                        charCount = 0
                        _intEditTextStartLength = (_editText!!).length()
                        return
                    }
                    charCount++
                }
            })


            /*
            //CODE FOR ADDING CUSTOM FUNCTION TO EXISTING ANDROID API OBJECT:
            fun EditText.customOnTextChanged(afterTextChanged: (String) -> Unit) {
                this.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        if (charCount > _intAutoSaveTrigger && (_editText!!).length() > _intEditTextStartLength) {
                            //utils.popup(applicationContext, "Saving...")
                            saveToFile()
                            charCount = 0
                            _intEditTextStartLength = (_editText!!).length()
                            return
                        }
                        charCount++
                    }

                    override fun afterTextChanged(editable: Editable?) {
                        //afterTextChanged.invoke(editable.toString())
                    }
                })
            }
            //IMPLEMENTATION OF THE ABOVE
            (_editText!!).customOnTextChanged { saveToFile() }*/
        }
        /*TODO - Like above: Listen every few seconds and see if new content then save? Which is more efficient?*/
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
            R.id.menuitem_changecolor -> {
                this.launchColorSelectorActivity()
            }
        }
        return blnRetItem
    }
    private fun resetEditTextToGivenValue(){
        //this.editTextField.text = this.originalEditTextContent
        setEditTextToFileContents(_strCurrentFileName)
        setCursorToEndOfTxtField()
    }
    private fun refreshEditText(){
        val curCurrentPosition = (_editText!!).selectionStart
        setEditTextToFileContents(_strCurrentFileName)
        (_editText!!).setSelection(curCurrentPosition)
    }


    /* /  BUTTON CLICK FUNCTIONS  / */
    fun button_toolbar_new__click(view: View) {
        this.createNewTextFile()
    }

    @Throws(Exception::class)
    fun button_toolbar_save__click(view: View) {
        this.saveToFile()
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


    /* /  FILE MANAGEMENT CONTROLLERS  / */
    private fun createNewTextFile(){
        val newFile = File(_strDirPath, utils.getCurrentFormattedDateAsString() + ".txt")

        if (newFile.exists()) {
            return
        }

        try {
            utils.file_Append(newFile, ("# " + utils.getCurrentFormattedDateAsString() + utils.getCurrentTimeStampAsString() + "\n\n---\n\n"))

            this.setTextFieldToLatestFile()
            this.setSpinnerItems(utils.getListOfAllFilenamesInDir(_strDirPath))
        } catch (e: Exception) {
            utils.popup(applicationContext, e)
        }
        setCursorToEndOfTxtField()
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

            _intEditTextStartLength = (_editText!!).length()
        } catch (e: Exception) {
            utils.popup(applicationContext, e)
        }
    }


    /* /  VIEW CONTROLLERS  / */
    /*   ACTIVITY MODIFIERS   */
    private fun launchColorSelectorActivity(){
        val intent = Intent(this, ColorSelectorActivity::class.java)
        startActivity(intent)
    }

    /*   EDIT TEXT FUNCTIONS   */
    private fun insertTimestampToEditText(){
        val txtMain = findViewById<EditText>(R.id.editText)
        val strTimestamp: String = "\n - "+utils.getCurrentTimeStampAsString()+":  "
        txtMain.text.insert(txtMain.selectionStart, strTimestamp)
    }

    private fun setCursorToEndOfTxtField(){
        val txtMain: EditText = (_editText!!)
        txtMain.setSelection(txtMain.text.length)
    }

    /*   SPINNER FUNCTIONS   */
    private fun setSpinnerItems(p_strItems: Array<String?>){
        var spinner = findViewById<View>(R.id.spinner) as Spinner
        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, p_strItems)
    }

    /*   TEXT FIELD FUNCTIONS  */
    private fun setChosenFilename(p_strFileName: String){
        (_debug_text!!).text = _strDirPath + p_strFileName
        (_textView_Title!!).text = (p_strFileName).substring(0,(p_strFileName).length-4)
    }

    @Throws(Exception::class)
    private fun setEditTextToFileContents_and_setTextFieldToFileName(file: File) {
        val strOriginalText = utils.readFileContentsToString(file)
        (_editText!!).setText(strOriginalText)
        this.setChosenFilename(file.name)
    }

    private fun setEditTextToFileContents(p_strFileName: String){
        var file = File(_strDirPath, p_strFileName)
        (_editText!!).setText(utils.readFileContentsToString(file))
        _strCurrentFileName = file.name
    }

    @Throws(Exception::class)
    private fun setTextFieldToLatestFile() {
        val files = utils.getListOfAllFilesInDir(_strDirPath)
        val latestFile = files[0]
        this._strCurrentFileName = latestFile.name
        setEditTextToFileContents_and_setTextFieldToFileName(latestFile)
    }

}