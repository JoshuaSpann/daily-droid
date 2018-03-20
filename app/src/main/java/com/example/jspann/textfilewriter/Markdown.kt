package com.example.jspann.textfilewriter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import java.util.regex.Pattern
import android.R.attr.start
import android.app.Application
import android.content.Context
import android.graphics.Color.rgb
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat


/**
 * Created by jspann on 3/20/2018.
 */

class Markdown {
    private val utils = Utils()

    /**
     * Formats a String as a SpannableString based off of its Markdown Content
     */
    public fun formatFromString(text: String): SpannableString {
        var spannableString = SpannableString(text)

        // Default EditText Color ro Darker Gray //
        spannableString.setSpan(ForegroundColorSpan(Colors.Markdown.text), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableString = setHeadingSpans(spannableString)
        spannableString = setTimestampSpans(spannableString)

        return spannableString
    }

    /**
     * Formats a SpannableString to have Spans for All Markdown Headings (#, ##, ###...)
     */
    private fun setHeadingSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        spannableString = setH2Spans(spannableString)

        return spannableString
    }

    /**
     * Formats a SpannableString to have Spans for All H2 Markdown Headings (##) between the opening and closing tags (##.....##)
     */
    private fun setH2Spans(spannableString: SpannableString) : SpannableString{
        val h2Identifier = "##"

        val h2CountInText = spannableString.toString().length - spannableString.toString().replace("##", "").length

        var startLocation = spannableString.indexOf(h2Identifier)
        var stopLocation = startLocation

        for (i in 1..(h2CountInText/2)) {
            try {
                // These have to stay in the loop or else they will be overwritten! //
                // val testColor = Color.HSVToColor(1, floatArrayOf((0).toFloat(),(0).toFloat(),(0).toFloat()))
                val h2ColorSpan = ForegroundColorSpan(Colors.APP.PRIMARY)
                val backgroundSpan = BackgroundColorSpan(Color.YELLOW)
                val h2SizeSpan = RelativeSizeSpan((1.2).toFloat())

                // Find next instance of "tags" and apply style until those "tags" end //
                stopLocation = spannableString.indexOf(h2Identifier, startLocation + h2Identifier.length)// + h2Identifier.length

                // Set Styles //
                spannableString.setSpan(h2SizeSpan, startLocation, stopLocation+h2Identifier.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(h2ColorSpan, startLocation, stopLocation+h2Identifier.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                // Reset the start location to the new stop location after setting the span //
                startLocation = spannableString.indexOf(h2Identifier,stopLocation + h2Identifier.length)
            }
            catch (e: Exception) {
                continue
            }
        }

        return  spannableString
    }

    private fun setTimestampSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString

        val timestampStartSymbol = " - "
        val timestampEndSymbol = ":  "

        val timestampCount = spannableString.toString().length - spannableString.toString().replace(" - ", "").replace(":  ","").length
        var startLocation = spannableString.indexOf(timestampStartSymbol)
        var stopLocation = startLocation//spannableString.indexOf(timestampEndSymbol)

        val p = Pattern.compile("\n - \\d\\d\\d\\d:")
        val m = p.matcher(spannableString)   // get a matcher object
        var count = 0

        var timestampHighlightLocs: MutableList<IntArray> = mutableListOf()
        while (m.find()) {
            count++
            println("Match number " + count)
            System.out.println("start(): " + m.start())
            System.out.println("end(): " + m.end())
            timestampHighlightLocs.add(intArrayOf(m.start(), m.end()))
        }

        for (i in 0 until timestampHighlightLocs.size) {
            //try {
                val colorSpan = ForegroundColorSpan(Color.RED)
                Log.d("JSDEV - ARR: ", i.toString())
                spannableString.setSpan(colorSpan, timestampHighlightLocs[i][0], timestampHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
           // }
           // catch (e: Exception) {
           //     continue
           // }
        }


        /*
        for (i in 1..timestampCount) {
            try {
                Log.d("JSDEV", "["+i+"] strL: "+startLocation+" stpL: "+stopLocation)
                // Formatting //
                val colorSpan = ForegroundColorSpan(Color.RED)
                stopLocation = spannableString.indexOf(timestampEndSymbol)
                spannableString.setSpan(colorSpan, startLocation, stopLocation + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                startLocation = spannableString.indexOf(timestampStartSymbol, stopLocation)

            }
            catch (e: Exception) {
                continue
            }
        }
        */

        return spannableString
    }
}