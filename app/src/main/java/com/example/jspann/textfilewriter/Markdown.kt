package com.example.jspann.textfilewriter

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.*
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

        // Bolds and Italics //
        spannableString = setItalicSpans(spannableString)
        spannableString = setBoldSpans(spannableString)
        spannableString = setBoldItalicSpans(spannableString)

        // Code //
        spannableString = setCodeSpans(spannableString)

        // Headings //
        spannableString = setHeadingSpans(spannableString)
        spannableString = setHyperlinkSpans(spannableString)

        // Timestamps, Strikethroughs, and Lists //
        spannableString = setListSpans(spannableString)
        spannableString = setTimestampSpans(spannableString)
        spannableString = setStrikethroughSpans(spannableString)

        return spannableString
    }

    /**
     * Returns [begin, end] Coordinates List for Given Heading Level in a String
     */
    private fun getCoordinatesOfHeadingsLevel(headingLevel: Int, spannableString: SpannableString) : MutableList<IntArray>{
        //val r = "((^|\n)#{"+headingLevel+"}[\\s\\S&&[^\n#]]*\n)"
        val r = "(?m)((^|\n)#{"+headingLevel+"} .*$\n)"
        val p = Pattern.compile(r)
        val m = p.matcher(spannableString)

        // Search through the regex matcher and assign the coordinates to a list //
        var hHighlightLocs: MutableList<IntArray> = mutableListOf()

        while (m.find()) {
            hHighlightLocs.add(intArrayOf(m.start(), m.end()))
        }

        return hHighlightLocs
    }

    /**
     * Returns given spannableString formatted to have styles applied to __...__ or **....**
     */
    private fun setBoldSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        // Matches all words like " **............** " or " __.....__ "
        //val r = " (\\*{2}|_{2}) [\\s\\S&&[^\n]]* (\\*{2}|_{2})"
        val r = "\\s(\\*|_){2}[\\s\\S&&[^\n]]*(\\*|_){2}"
        val p = Pattern.compile(r)
        val m =p.matcher(spannableString)

        // Search through the regex matcher and assign the coordinates to a list //
        var iHighlightLocs: MutableList<IntArray> = mutableListOf()

        while (m.find()) {
            iHighlightLocs.add(intArrayOf(m.start(), m.end()))
        }

        // Set the formatting spans to the text //
        for (i in 0 until iHighlightLocs.size) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.BOLD)
            val styleSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(colorSpan, iHighlightLocs[i][0], iHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(styleSpan, iHighlightLocs[i][0], iHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Formats SpannableString as BoldItalic-styled Markdown
     */
    private fun setBoldItalicSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        // Matches all words like " **............** " or " __.....__ "
        //val r = " (\\*{2}|_{2}) [\\s\\S&&[^\n]]* (\\*{2}|_{2})"
        val r = "\\s(\\*|_){3}[\\s\\S&&[^\n]]*(\\*|_){3}"
        val p = Pattern.compile(r)
        val m =p.matcher(spannableString)

        // Search through the regex matcher and assign the coordinates to a list //
        var iHighlightLocs: MutableList<IntArray> = mutableListOf()

        while (m.find()) {
            iHighlightLocs.add(intArrayOf(m.start(), m.end()))
        }

        // Set the formatting spans to the text //
        for (i in 0 until iHighlightLocs.size) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.BOLD)
            val styleSpan = StyleSpan(Typeface.BOLD_ITALIC)
            spannableString.setSpan(colorSpan, iHighlightLocs[i][0], iHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(styleSpan, iHighlightLocs[i][0], iHighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Applies Code Formatting to Areas of String where is `.` and ```.```
     */
    private fun setCodeSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString

        // Matches all words like `.`
        val p1 = Pattern.compile("`.+`")
        val m1 =p1.matcher(spannableString)

        // Matches all words like ```.```
        val p3 = Pattern.compile("\n```\n[\\s\\S&&[^`]]+\n```")
        val m3 =p3.matcher(spannableString)

        // Search through the regex matcher and assign the coordinates to a list //
        var iHighlightLocs: MutableList<IntArray> = mutableListOf()

        // `.`
        while (m1.find()) {
            iHighlightLocs.add(intArrayOf(m1.start(), m1.end()))
        }

        // ```.```
        while (m3.find()) {
            iHighlightLocs.add(intArrayOf(m3.start(), m3.end()))
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
    private fun setH1Spans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        // Matches all lines like "# ............"
        val h1HighlightLocs = getCoordinatesOfHeadingsLevel(1, spannableString)

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
    /*private fun setH2Spans(spannableString: SpannableString) : SpannableString{
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
    */
    private fun setH2Spans(spannableString: SpannableString) : SpannableString{
        var spannableString = spannableString

        val h2HighlightLocs = getCoordinatesOfHeadingsLevel(2, spannableString)

        for (i in 0 until h2HighlightLocs.size) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.H2)
            val textSizeSpan = RelativeSizeSpan(1.2.toFloat())
            spannableString.setSpan(colorSpan, h2HighlightLocs[i][0], h2HighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(textSizeSpan, h2HighlightLocs[i][0], h2HighlightLocs[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Returns given spannableString formatted to have H3 ###.....### styles
     */
    private fun setH3Spans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        //val h1Identifier = "#"
        // Matches all lines like "# ............"
        val h3HighlightLocs = getCoordinatesOfHeadingsLevel(3, spannableString)

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
     * Formats SpannableString with Hyperlink-styled Markdown
     */
    private fun setHyperlinkSpans(spannblStr: SpannableString) : SpannableString {
        var spannableString =  spannblStr

        //val r = "\\s\\[[\\s\\S&&[^\n)]]+\\)"
        //val r = "\\[[\\s\\S&&[^\n\\[\\]()]]+\\]\\([\\s\\S&&[^\n\\[\\]()]]+\\)"
        val r = "\\[[\\s\\S&&[^\n\\[\\]]]+\\]\\([\\s\\S&&[^\n()]]+\\)"
        val p = Pattern.compile(r)
        val m = p.matcher(spannableString)
        var hyperlinkCoordinates: MutableList<IntArray> = mutableListOf()

        while (m.find()) {
            hyperlinkCoordinates.add(intArrayOf(m.start(), m.end()))
        }

        for (i in 0 until hyperlinkCoordinates.size) {
            val hyperlinkColorSpan =  ForegroundColorSpan(Colors.Markdown.HYPERLINK)
            val hyperlinkStyleSpan = UnderlineSpan()
            spannableString.setSpan(hyperlinkColorSpan, hyperlinkCoordinates[i][0], hyperlinkCoordinates[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(hyperlinkStyleSpan, hyperlinkCoordinates[i][0], hyperlinkCoordinates[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Returns given spannableString formatted to have styles applied to _..._ or *....*
     */
    private fun setItalicSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        // Matches all words like " *............* " or " _....._ "
        val r = "\\s(\\*|_)[\\s\\S&&[^*_]]+(\\*|_)" //"\\s\\*.*\\*\\s"
        val p = Pattern.compile(r)
        val m =p.matcher(spannableString)

        // Search through the regex matcher and assign the coordinates to a list //
        var iHighlightLocs: MutableList<IntArray> = mutableListOf()

        while (m.find()) {
            iHighlightLocs.add(intArrayOf(m.start(), m.end()))
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
     * Formats SpannableStrings to the Style Markdown for (Un)ordered Lists
     */
    private fun setListSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString

        val r = "\n {2}(-|\\d+\\.|\\*|\\+) [\\s\\S&&[^\n]]+"
        val p = Pattern.compile(r)
        val m = p.matcher(spannableString)
        var listItemCoordinates: MutableList<IntArray> = mutableListOf()

        while(m.find()) {
            listItemCoordinates.add(intArrayOf(m.start(), m.end()))
        }

        for (i in 0 until listItemCoordinates.size) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.LIST_ITEM)
            spannableString.setSpan(colorSpan, listItemCoordinates[i][0], listItemCoordinates[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Formats SpannableString with Strikethrough-styled Markdown
     */
    private fun setStrikethroughSpans(spannableStr: SpannableString) : SpannableString {
        val spannableString = spannableStr

        val r = "~{2}[\\s\\S&&[^~]]+~{2}"
        val p = Pattern.compile(r)
        val m = p.matcher(spannableString)
        val strikeThruCoordinates: MutableList<IntArray> = mutableListOf()

        while (m.find()) {
            strikeThruCoordinates.add(intArrayOf(m.start(), m.end()))
        }

        for (i in 0 until strikeThruCoordinates.size) {
            val strikethroughSpan = StrikethroughSpan()
            spannableString.setSpan(strikethroughSpan, strikeThruCoordinates[i][0], strikeThruCoordinates[i][1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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