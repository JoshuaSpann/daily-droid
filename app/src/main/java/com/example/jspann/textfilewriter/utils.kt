package com.example.jspann.textfilewriter
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jspann on 11/29/2017.
 */

fun getCurrentTimeStampAsString(): String{
    return SimpleDateFormat("HHmm").format(Date()).toString()
}

fun getCurrentFormattedDateAsString(): String {
    return SimpleDateFormat("yyyyMMdd").format(Date()).toString()
}

fun popup(data: Any) {
    //Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_LONG).show()
}

//@Throws(Exception::class)
fun readFileContentsToString(file: File): String{
    return Scanner(file).useDelimiter("\\A").next()
}