package com.example.jspann.textfilewriter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView

import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            setTextFieldToLatestFile()
        } catch (e: Exception) {
            popup(e)
        }

        //TODO - ADD FILE SELECTION DROPDOWN TO ALLOW DYNAMIC EDITING!!!
        //TODO - ADD COLORIZING FUNCTIONALITY
        //TODO - ADD AUTO-SAVE FUNCTIONALITY!!!
        /*TODO - ALLOW JOURNAL(entry)S TO BE GROUPED INTO FOLDER AND HAVE USER NAME JOURNAL FOLDER!!!
          [-] MyNamedJournalFolder
           |__[] Auto-JournalEntry.txt
          [-] MyOtherNamedJournalFolder
           |__[] Auto-JournalEntry2.txt
           |__[] Auto-JournalEntryLatest.txt -- Put here by user choice
         */

    }


    /* /  BUTTON CLICK FUNCTIONS / */
    fun createNewTextFile(view: View) {
        val newFile = File(getDirectoryPathToString(), getCurrentFormattedDateAsString() + ".txt")

        if (newFile.exists()) {
            return
        }

        try {
            val fwriter = FileWriter(newFile)
            fwriter.append("# " + getCurrentFormattedDateAsString() + getCurrentTimeStampAsString() + "\n\n---\n\n")
            fwriter.flush()
            fwriter.close()
            setTextFieldToLatestFile()
        } catch (e: Exception) {
            popup(e)
        }
    }

    @Throws(Exception::class)
    fun saveToFile(view: View) {
        val strFilenames = getListOfAllFilenamesInDir(getDirectoryPathToString())
        val strLatestFilename = strFilenames[0]

        var strDataBody = (findViewById<View>(R.id.editText) as EditText).text.toString()
        try {
            val dteToday = getCurrentFormattedDateAsString()

            val rootPath = File(getDirectoryPathToString())
            rootPath.mkdir()

            val file = File(rootPath, strLatestFilename)

            if (!file.exists()) {
                strDataBody = dteToday + "\n\n---\n\n" + strDataBody
            }

            val fwriter = FileWriter(file)
            fwriter.append(strDataBody + "\n\n")
            fwriter.flush()
            fwriter.close()

            setTextFieldToLatestFile()
        } catch (e: Exception) {
            popup(e)
        }
    }

    fun click_btnTimestamp(view: View) {
        val txtMain = findViewById<EditText>(R.id.editText)
        val strDataBody = txtMain.text.toString()
        val strTimestamp = getCurrentTimeStampAsString()
        txtMain.text.insert(txtMain.selectionStart, strTimestamp)
    }

    private fun getCurrentTimeStampAsString(): String {
        return SimpleDateFormat("HHmm").format(Date()).toString()
    }


    /* /  TEXT FIELD FUNCTIONS / */
    @Throws(Exception::class)
    private fun setTextFieldToFile(file: File) {
        val strOriginalText = readFileContentsToString(file)
        (findViewById<View>(R.id.editText) as EditText).setText(strOriginalText)
        val ctx = applicationContext
        (findViewById<View>(R.id.debug_text) as TextView).text = ctx.filesDir.toString() + file.name
    }

    @Throws(Exception::class)
    private fun setTextFieldToLatestFile() {
        val files = getListOfAllFilesInDir(getDirectoryPathToString())
        val latestFile = files[0]
        setTextFieldToFile(latestFile)
    }


    /* /  HELPER FUNCTIONS  / */
    private fun getCurrentFormattedDateAsString(): String {
        val dteCurrentDate = Date()
        val dteFormat = SimpleDateFormat("yyyyMMdd")
        return dteFormat.format(dteCurrentDate).toString()
    }

    private fun getDirectoryPathToString(): String {
        val ctx = applicationContext
        val strDefaultDir = ctx.filesDir.toString() + "/DailyDroid/"
        val projectDir = File(strDefaultDir)
        projectDir.mkdir()
        return strDefaultDir
    }

    private fun getListOfAllFilenamesInDir(pathString: String): Array<String?> {
        val filesInDir = getListOfAllFilesInDir(pathString)

        var intFileCounter = 0
        val filenamesInDir = arrayOfNulls<String>(filesInDir.size)

        for (currFile in filesInDir) {
            filenamesInDir[intFileCounter] = currFile.getName().toString()
            intFileCounter++
        }

        return filenamesInDir
    }

    private fun getListOfAllFilesInDir(pathString: String): Array<File> {
        val dir = File(pathString)

        val filesInDir = dir.listFiles()
        Arrays.sort(filesInDir!!, Collections.reverseOrder<Any>())
        return filesInDir
    }

    private fun popup(data: Any) {
        android.widget.Toast.makeText(applicationContext, data.toString(), Toast.LENGTH_LONG).show()
    }

    @Throws(Exception::class)
    private fun readFileContentsToString(file: File): String {
        return Scanner(file).useDelimiter("\\A").next()
    }
}