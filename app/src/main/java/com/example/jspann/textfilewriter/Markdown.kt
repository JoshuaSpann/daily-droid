package com.example.jspann.textfilewriter

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
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
        spannableString.setSpan(ForegroundColorSpan(Colors.Markdown.TEXT_BODY),0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableString = setItalicSpans(spannableString)
        spannableString = setBoldSpans(spannableString)

        spannableString = setCodeSpans(spannableString)

        spannableString = setHeadingSpans(spannableString)
        spannableString = setTimestampSpans(spannableString)

        return spannableString
    }

    /**
     * Returns given spannableString formatted to have styles applied to __...__ or **....**
     */
    // TODO - TEST
    private fun setBoldSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        // Matches all words like " **............** " or " __.....__ "
        val p = Pattern.compile("\\s(\\*\\*)|(__).(\\*\\*)|(__)\\s")
        val m =p.matcher(spannableString)

        //val p2 = Pattern.compile("\\s__.__\\s")
        //val m2 =p2.matcher(spannableString)

        // Search through the regex matcher and assign the coordinates to a list //
        var iHighlightLocs: MutableList<IntArray> = mutableListOf()

        while (m.find()) {
            iHighlightLocs.add(intArrayOf(m.start(), m.end()))
        }

        /*
        while (m2.find()) {
            iHighlightLocs.add(intArrayOf(m2.start(), m2.end()))
        }
        */

        // Set the formatting spans to the text //
        for (i in 0 until iHighlightLocs.size) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.ITALICS)
            val styleSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(colorSpan, iHighlightLocs[i][0], iHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(styleSpan, iHighlightLocs[i][0], iHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    // TODO - FINISH `.*`
    private fun setCodeSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        // Matches all words like " ```.``` " or " `.` "
        // https://www.tutorialspoint.com/compile_java_online.php //
        val p = Pattern.compile("\n```\n[\\s\\S]*\n```\n")
        val m =p.matcher(spannableString)

        // Search through the regex matcher and assign the coordinates to a list //
        var iHighlightLocs: MutableList<IntArray> = mutableListOf()

        while (m.find()) {
            iHighlightLocs.add(intArrayOf(m.start(), m.end()))
        }

        // Set the formatting spans to the text //
        for (i in 0 until iHighlightLocs.size) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.CODE)
            val bgSpan = BackgroundColorSpan(Colors.GRAY_BRIGHT)
            spannableString.setSpan(colorSpan, iHighlightLocs[i][0], iHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(bgSpan, iHighlightLocs[i][0], iHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Formats a SpannableString to have Spans for All Markdown Headings (#, ##, ###...)
     */
    private fun setHeadingSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        spannableString = setH3Spans(spannableString)
        spannableString = setH2Spans(spannableString)
        spannableString = setH1Spans(spannableString)

        return spannableString
    }

    /**
     * Returns provided spannable string formatted to have H1 styles (#...#) applied
     */
    // TODO - TEST
    private fun setH1Spans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        //val h1Identifier = "#"
        // Matches all lines like "# ............"
        val p = Pattern.compile("(?m)^# /w $")
        val m =p.matcher(spannableString)

        // Search through the regex matcher and assign the coordinates to a list //
        var h1HighlightLocs: MutableList<IntArray> = mutableListOf()

        while (m.find()) {
            h1HighlightLocs.add(intArrayOf(m.start(), m.end()))
        }

        // Set the formatting spans to the text //
        for (i in 0 until h1HighlightLocs.size) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.H1)
            val relativeSizeSpan = RelativeSizeSpan(1.3.toFloat())
            spannableString.setSpan(colorSpan, h1HighlightLocs[i][0], h1HighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(relativeSizeSpan, h1HighlightLocs[i][0], h1HighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

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
                val h2ColorSpan = ForegroundColorSpan(Colors.Markdown.H2)
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
     * Returns given spannableString formatted to have H3 ###.....### styles
     */
    // TODO - TEST
    private fun setH3Spans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        //val h1Identifier = "#"
        // Matches all lines like "# ............"
        val p = Pattern.compile("(?m)^### /w $")
        val m =p.matcher(spannableString)

        // Search through the regex matcher and assign the coordinates to a list //
        var h3HighlightLocs: MutableList<IntArray> = mutableListOf()

        while (m.find()) {
            h3HighlightLocs.add(intArrayOf(m.start(), m.end()))
        }

        // Set the formatting spans to the text //
        for (i in 0 until h3HighlightLocs.size) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.H3)
            val textSizeSpan = RelativeSizeSpan(1.1.toFloat())
            spannableString.setSpan(colorSpan, h3HighlightLocs[i][0], h3HighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(textSizeSpan, h3HighlightLocs[i][0], h3HighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Returns given spannableString formatted to have styles applied to _..._ or *....*
     */
    // TODO - TEST
    private fun setItalicSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        // Matches all words like " *............* " or " _....._ "
        val p = Pattern.compile("\\s\\*.\\*\\s")
        val m =p.matcher(spannableString)

        val p2 = Pattern.compile("\\s_._\\s")
        val m2 =p2.matcher(spannableString)

        // Search through the regex matcher and assign the coordinates to a list //
        var iHighlightLocs: MutableList<IntArray> = mutableListOf()

        while (m.find()) {
            iHighlightLocs.add(intArrayOf(m.start(), m.end()))
        }

        while (m2.find()) {
            iHighlightLocs.add(intArrayOf(m2.start(), m2.end()))
        }

        // Set the formatting spans to the text //
        for (i in 0 until iHighlightLocs.size) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.ITALICS)
            val styleSpan = StyleSpan(Typeface.ITALIC)
            spannableString.setSpan(colorSpan, iHighlightLocs[i][0], iHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(styleSpan, iHighlightLocs[i][0], iHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
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
            val colorSpan = ForegroundColorSpan(Colors.Markdown.TIMESTAMP)
            spannableString.setSpan(colorSpan, timestampHighlightLocs[i][0], timestampHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }
}