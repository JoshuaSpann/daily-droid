package com.example.jspann.textfilewriter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import java.util.regex.Pattern


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
                val h2ColorSpan = ForegroundColorSpan(Colors.APP.PRIMARY)
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

    /**
     * Applies Highlighted Formatting for "Timestamps" matching " - 0000:  " where '0' is 0-9
     */
    private fun setTimestampSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString

        // Search for "\n - 0000:  " timestamps and format accordingly with REGEX //
        val p = Pattern.compile("\n - \\d\\d\\d\\d:")
        val m = p.matcher(spannableString)

        // Holds the text coordinates to set ranges for spans //
        var timestampHighlightLocs: MutableList<IntArray> = mutableListOf()

        // Search through the regex matcher and assign the coordinates to the list //
        while (m.find()) {
            timestampHighlightLocs.add(intArrayOf(m.start(), m.end()))
        }

        // Set the Timestamp formatting spans to the text //
        for (i in 0 until timestampHighlightLocs.size) {
            val colorSpan = ForegroundColorSpan(Color.RED)
            spannableString.setSpan(colorSpan, timestampHighlightLocs[i][0], timestampHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }
}