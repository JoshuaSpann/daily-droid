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
import android.view.*
import android.widget.EditText
import android.widget.TextView

import java.io.*


class MainActivity : AppCompatActivity() {

    /* /  UTILTY AND VIEW DECLARATIONS  / */
    //private var originalEditTextContent: String = ""
    //private val editTextField = (findViewById<View>(R.id.editText) as EditText)
    private val utils = Utils()

    /* /  LAUNCH CONTROLLER  / */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setSupportActionBar(findViewById<View>(R.id.toolbar2) as Toolbar)

        try {
            setTextFieldToLatestFile()
            //this.originalEditTextContent = this.editTextField.text.toString()
            setCursorToEndOfTxtField()
        } catch (e: Exception) {
            //utils.popup(applicationContext, e)
        }

        //TODO - ADD FILE SELECTION DROPDOWN TO ALLOW DYNAMIC EDITING!!!
        //TODO - ADD COLORIZING FUNCTIONALITY
        //TODO - ADD AUTO-SAVE FUNCTIONALITY!!!
        /*
            Listener for characters time period after (x# characters?) entered?
            Listen every few seconds and see if new content then save?
        */
        /*(findViewById<View>(R.id.editText) as EditText).addTextChangedListener(object: TextWatcher{
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
        (findViewById<View>(R.id.editText) as EditText).afterTextChanged { saveToFile()  }*/
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
            R.id.menuitem_add -> {
                this.createNewTextFile()
                return blnRetItem
            }
            R.id.menuitem_save -> {
                this.saveToFile()
                return blnRetItem
            }
            R.id.menuitem_timestamp -> {
                this.insertTimestampToEditText()
            }
            R.id.menuitem_changecolor -> {
                this.launchColorSelectorActivity()
            }
            R.id.menuitem_resetText -> {
                this.resetEditTextToGivenValue()
            }
        }
        return blnRetItem
    }
    private fun resetEditTextToGivenValue(){
        //this.editTextField.text = this.originalEditTextContent
        setTextFieldToLatestFile()
        setCursorToEndOfTxtField()
    }
    private fun refreshEditText(){
        val curCurrentPosition = (findViewById<View>(R.id.editText) as EditText).selectionStart
        setTextFieldToLatestFile()
        (findViewById<View>(R.id.editText) as EditText).setSelection(curCurrentPosition)
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
        val newFile = File(utils.getDirectoryPathToString(), utils.getCurrentFormattedDateAsString() + ".txt")

        if (newFile.exists()) {
            return
        }

        try {
            val fwriter = FileWriter(newFile)
            fwriter.append("# " + utils.getCurrentFormattedDateAsString() + utils.getCurrentTimeStampAsString() + "\n\n---\n\n")
            fwriter.flush()
            fwriter.close()
            setTextFieldToLatestFile()
        } catch (e: Exception) {
            utils.popup(applicationContext, e)
        }
        setCursorToEndOfTxtField()
    }

    private fun saveToFile(){
        val strFilenames = utils.getListOfAllFilenamesInDir(utils.getDirectoryPathToString())
        val strLatestFilename = strFilenames[0]

        var txtEditText = (findViewById<View>(R.id.editText) as EditText)
        var strDataBody = txtEditText.text.toString()
        try {
            val dteToday = utils.getCurrentFormattedDateAsString()

            val rootPath = File(utils.getDirectoryPathToString())
            rootPath.mkdir()

            val file = File(rootPath, strLatestFilename)

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
        val strDataBody = txtMain.text.toString()
        val strTimestamp: String = "\n - "+utils.getCurrentTimeStampAsString()+":  "
        txtMain.text.insert(txtMain.selectionStart, strTimestamp)
    }

    private fun setCursorToEndOfTxtField(){
        val txtMain: EditText = (findViewById<View>(R.id.editText) as EditText)
        txtMain.setSelection(txtMain.text.length)
    }

    /*   TEXT FIELD FUNCTIONS  */
    @Throws(Exception::class)
    private fun setTextFieldToFileName(file: File) {
        val strOriginalText = utils.readFileContentsToString(file)
        (findViewById<View>(R.id.editText) as EditText).setText(strOriginalText)
        val ctx = applicationContext
        (findViewById<View>(R.id.debug_text) as TextView).text = utils.getDirectoryPathToString() + file.name
        (findViewById<View>(R.id.textView_Title) as TextView).text = (file.name).substring(0,(file.name).length-4)
    }

    @Throws(Exception::class)
    private fun setTextFieldToLatestFile() {
        val files = utils.getListOfAllFilesInDir(utils.getDirectoryPathToString())
        val latestFile = files[0]
        setTextFieldToFileName(latestFile)
    }

}