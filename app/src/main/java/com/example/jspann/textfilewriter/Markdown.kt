package com.example.jspann.textfilewriter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan

/**
 * Created by jspann on 3/20/2018.
 */

class Markdown {
    private val utils = Utils()
    public fun setHeadingSpans(text: String) : SpannableString{
        val h2Identifier = "##"
        val originalText = text
        var spannableString = SpannableString(originalText)
        val sizeIncrease= 1.5
        val h2SizeSpan = RelativeSizeSpan(sizeIncrease.toFloat())

        val h2CountInText = originalText.length - originalText.replace("##", "").length

        var startLocation = spannableString.indexOf(h2Identifier)
        var stopLocation = startLocation

        // Default EditText Color ro Darker Gray //
        spannableString.setSpan(ForegroundColorSpan(Color.DKGRAY), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        for (i in 1..(h2CountInText/2)) {
            try {
                // These have to stay in the loop or else they will be overwritten! //
                val h2ColorSpan = ForegroundColorSpan(Color.BLACK)
                val backgroundSpan = BackgroundColorSpan(Color.YELLOW)

                // Find next instance of "tags" and apply style until those "tags" end //
                stopLocation = spannableString.indexOf(h2Identifier, startLocation + h2Identifier.length)// + h2Identifier.length

                // Set Styles //
                spannableString.setSpan(backgroundSpan, startLocation, stopLocation+h2Identifier.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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
}