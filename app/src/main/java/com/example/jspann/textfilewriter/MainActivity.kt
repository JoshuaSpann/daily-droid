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
import android.view.*
import android.widget.*

import java.io.*


class MainActivity : AppCompatActivity() {

    /* /  UTILTY AND VIEW DECLARATIONS  / */
    private val utils = Utils()
    private var _strDirPath = utils.getDirectoryPathToString()
    private var _strCurrentFileName = ""

    private var _textView_Title: TextView ?= null
    private var _debug_text: TextView ?= null
    private var _editText: EditText ?= null

    /* /  LAUNCH CONTROLLER  / */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setSupportActionBar(findViewById<View>(R.id.toolbar2) as Toolbar)
        _textView_Title = (findViewById(R.id.textView_Title))
        _debug_text = (findViewById(R.id.debug_text))
        _editText = (findViewById<View>(R.id.editText) as EditText)

        try {
            setTextFieldToLatestFile()
            setCursorToEndOfTxtField()
        } catch (e: Exception) {
            //utils.popup(applicationContext, e)
        }

        //TODO - ADD FILE SELECTION DROPDOWN TO ALLOW DYNAMIC EDITING!!!
        this.setSpinnerItems(utils.getListOfAllFilenamesInDir(_strDirPath))
        (findViewById<View>(R.id.spinner) as Spinner).onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                var spinner = findViewById<View>(R.id.spinner) as Spinner
                setEditTextToFileContents(spinner.selectedItem.toString())
                setChosenFilename(spinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("not needed") //To change body of created functions use File | Settings | File Templates.
            }
        }

        //TODO - ADD COLORIZING FUNCTIONALITY
        //TODO - ADD AUTO-SAVE FUNCTIONALITY!!!
        /*
            Listener for characters time period after (x# characters?) entered?
            Listen every few seconds and see if new content then save?
        */
        /*_editText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?){

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Can Just Use without following code block, this method: saveToFile()
            }
        })
        fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(editable: Editable?) {
                    afterTextChanged.invoke(editable.toString())
                }
            })
        }
        _editText.afterTextChanged { saveToFile()  }*/
        /*TODO - ALLOW JOURNAL(entry)S TO BE GROUPED INTO FOLDER AND HAVE USER NAME JOURNAL FOLDER!!!
          [-] MyNamedJournalFolder
           |__[] Auto-JournalEntry.txt
          [-] MyOtherNamedJournalFolder
           |__[] Auto-JournalEntry2.txt
           |__[] Auto-JournalEntryLatest.txt -- Put here by user choice
         */

    }

    /* /  APP BAR MENU CONTROLLERS  / */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val blnRetItem = super.onOptionsItemSelected(item)
        when(item?.itemId){
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
            val fwriter = FileWriter(newFile)
            fwriter.append("# " + utils.getCurrentFormattedDateAsString() + utils.getCurrentTimeStampAsString() + "\n\n---\n\n")
            fwriter.flush()
            fwriter.close()
            this.setTextFieldToLatestFile()
            this.setSpinnerItems(utils.getListOfAllFilenamesInDir(_strDirPath))
        } catch (e: Exception) {
            utils.popup(applicationContext, e)
        }
        setCursorToEndOfTxtField()
    }

    private fun saveToFile(){
        var txtEditText = (_editText!!)
        var strDataBody = txtEditText.text.toString()
        try {
            val dteToday = utils.getCurrentFormattedDateAsString()

            val rootPath = File(_strDirPath)
            rootPath.mkdir()

            val file = File(rootPath, _strCurrentFileName)

            if (!file.exists()) {
                strDataBody = dteToday + "\n\n---\n\n" + strDataBody
            }

            val fwriter = FileWriter(file)
            fwriter.append(strDataBody)
            fwriter.flush()
            fwriter.close()

            this.refreshEditText()
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
        this._strCurrentFileName = file.name
    }

    @Throws(Exception::class)
    private fun setTextFieldToLatestFile() {
        val files = utils.getListOfAllFilesInDir(_strDirPath)
        val latestFile = files[0]
        this._strCurrentFileName = latestFile.name
        setEditTextToFileContents_and_setTextFieldToFileName(latestFile)
    }

}