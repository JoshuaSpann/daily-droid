package com.example.jspann.dailydroid

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.*
import java.util.regex.Pattern


/**
 * Created by jspann on 3/20/2018.
 */

class Markdown {
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

        // Code, Checkboxes, Blockquotes //
        spannableString = setCodeSpans(spannableString)
        spannableString = setCheckboxSpans(spannableString)
        spannableString = setBlockquoteSpans(spannableString)

        // Headings, Hyperlinks, Images //
        spannableString = setHeadingSpans(spannableString)
        spannableString = setHyperlinkSpans(spannableString)
        spannableString = setImageSpans(spannableString)

        // Timestamps, Strikethroughs, Lists, Horizontal Rules //
        spannableString = setListSpans(spannableString)
        spannableString = setTimestampSpans(spannableString)
        spannableString = setStrikethroughSpans(spannableString)
        spannableString = setHorizontalRuleSpans(spannableString)

        return spannableString
    }

    /**
     * Returns [begin, end] Coordinates List for Given Heading Level in a String
     */
    private fun getCoordinatesOfHeadingsLevel(headingLevel: Int, spannableString: SpannableString) : MutableList<IntArray>{
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
     * Formats SpannableString with Blockquote-style Markdown
     */
    private fun setBlockquoteSpans(spannableString: SpannableString) : SpannableString {
        var spnblStr = spannableString
        val r = "\n> [\\s\\S&&[^\n]]+"
        val p = Pattern.compile(r)
        val m = p.matcher(spnblStr)

        while (m.find()) {
            val bqStartBgSpan = BackgroundColorSpan(Colors.Markdown.BLOCK_QUOTE_START_BACKGROUND)
            val bqColorSpan = ForegroundColorSpan(Colors.Markdown.BLOCK_QUOTE)
            val bqBgSpan = BackgroundColorSpan(Colors.Markdown.BLOCK_QUOTE_BACKGROUND)
            spannableString.setSpan(bqColorSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(bqBgSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(bqStartBgSpan, m.start(), m.start()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spnblStr
    }
    /**
     * Formats SpannableString with Bold Markdown styles applied to __...__ or **....**
     */
    private fun setBoldSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        val r = "\\s(\\*|_){2}[\\s\\S&&[^\n_*]]*(\\*|_){2}"
        val p = Pattern.compile(r)
        val m =p.matcher(spannableString)

        while (m.find()) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.BOLD)
            val styleSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(colorSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(styleSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Formats SpannableString as BoldItalic-styled Markdown
     */
    private fun setBoldItalicSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        val r = "\\s(\\*|_){3}[\\s\\S&&[^\n*_]]*(\\*|_){3}"
        val p = Pattern.compile(r)
        val m =p.matcher(spannableString)

        while (m.find()) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.BOLD)
            val styleSpan = StyleSpan(Typeface.BOLD_ITALIC)
            spannableString.setSpan(colorSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(styleSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Formats SpannableString as Github-Checkbox-styled Markdown
     */
    private fun setCheckboxSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        var r = "\\s\\[\\s\\]\\s"
        var p = Pattern.compile(r)
        var m =p.matcher(spannableString)

        while (m.find()) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.CODE)
            val backgroundSpan = BackgroundColorSpan(Colors.Markdown.BLOCK_QUOTE_BACKGROUND)
            val styleSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(colorSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(backgroundSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(styleSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        r = "\\s\\[(x|X)\\]\\s"
        p = Pattern.compile(r)
        m =p.matcher(spannableString)

        while (m.find()) {
            val colorSpan = ForegroundColorSpan(Colors.App.ACCENT)
            val backgroundSpan = BackgroundColorSpan(Colors.Markdown.CODE_BACKGROUND)
            val styleSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(colorSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(backgroundSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(styleSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Formats SpannableString with Code Markdown `.` and ```.```
     */
    private fun setCodeSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString

        // Matches all words like `.`
        val p1 = Pattern.compile("`[\\s\\S&&[^`]]+`")
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
            val bgSpan = BackgroundColorSpan(Colors.Markdown.CODE_BACKGROUND)
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
     * Formats SpannableString with H1 styles (#...#) applied
     */
    private fun setH1Spans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
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
     * Formats SpannableString with H2 ##...## styles
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
     * Formats SpannableString with H3 ###.....### styles
     */
    private fun setH3Spans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
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
     * Formats SpannableString with Horizontal-Rule-styled Markdown
     */
    private fun setHorizontalRuleSpans(spannableString: SpannableString) : SpannableString {
        var spnStr = spannableString
        val r= "\n(-|\\*|_){3}\n"
        val p = Pattern.compile(r)
        val m = p.matcher(spnStr)

        while (m.find()) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.HORIZONTAL_RULE)
            val stSpan = StrikethroughSpan()
            spnStr.setSpan(colorSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spnStr.setSpan(stSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spnStr
    }

    /**
     * Formats SpannableString with Hyperlink-styled Markdown
     */
    private fun setHyperlinkSpans(spannblStr: SpannableString) : SpannableString {
        var spannableString =  spannblStr

        val r = "[^!]\\[[\\s\\S&&[^\n\\[\\]]]+\\]\\([\\s\\S&&[^\n()]]+\\)"
        val p = Pattern.compile(r)
        val m = p.matcher(spannableString)

        while (m.find()) {
            val hyperlinkColorSpan =  ForegroundColorSpan(Colors.Markdown.HYPERLINK)
            val hyperlinkStyleSpan = UnderlineSpan()
            // TODO - Have hyperlinks clickable with dialog to confirm opening link in web browser
            spannableString.setSpan(hyperlinkColorSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(hyperlinkStyleSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Formats a SpannableString with Image-styled Markdown
     */
    private fun setImageSpans(spannableString: SpannableString) : SpannableString {
        var spnblStr = spannableString
        val r = "!\\[[\\s\\S&&[^\n\\[\\]]]+\\]\\([\\s\\S&&[^\n()]]+\\)"
        val p = Pattern.compile(r)
        val m = p.matcher(spnblStr)

        while (m.find()) {
            val imgColorSpan = ForegroundColorSpan(Colors.Markdown.IMAGE)
            spannableString.setSpan(imgColorSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spnblStr
    }

    /**
     * Formats SpannableString with Italic Markdown styles applied to _..._ or *....*
     */
    private fun setItalicSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString
        val r = "\\s(\\*|_)[\\s\\S&&[^*_\n]]+(\\*|_)"
        val p = Pattern.compile(r)
        val m =p.matcher(spannableString)

        while (m.find()) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.ITALICS)
            val styleSpan = StyleSpan(Typeface.ITALIC)
            spannableString.setSpan(colorSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(styleSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Formats SpannableString to the Style Markdown for (Un)ordered Lists
     */
    private fun setListSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString

        val r = "\n {2}(-|\\d+\\.|\\*|\\+) [\\s\\S&&[^\n]]+"
        val p = Pattern.compile(r)
        val m = p.matcher(spannableString)

        while(m.find()) {
            val colorSpan = ForegroundColorSpan(Colors.Markdown.LIST_ITEM)
            spannableString.setSpan(colorSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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

        while (m.find()) {
            val strikethroughSpan = StrikethroughSpan()
            spannableString.setSpan(strikethroughSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    /**
     * Applies Highlighted Formatting for "Timestamps" matching " - 0000:  " where '0' is 0-9
     */
    private fun setTimestampSpans(spannableString: SpannableString) : SpannableString {
        var spannableString = spannableString

        // Search for "\n - 0000:  " timestamps and format accordingly with REGEX //
        val p = Pattern.compile("\n - (\\d{4}|[\\d\\d\\d\\d, ]*|\\d{4} - \\d{4}):")
        val m = p.matcher(spannableString)

        // Search through the regex matcher and assign the coordinates to the list //
        while (m.find()) {
        // Set the Timestamp formatting spans to the text //
            val colorSpan = ForegroundColorSpan(Colors.Markdown.TIMESTAMP)
            spannableString.setSpan(colorSpan,m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }
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
}