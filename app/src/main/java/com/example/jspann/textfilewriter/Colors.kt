package com.example.jspann.textfilewriter

import android.graphics.Color
import android.graphics.Color.rgb
import android.graphics.drawable.ColorDrawable

/**
 * Created by jspann on 3/20/2018.
 */
object Colors {
    val GREEN = 0xFF669900.toInt()
    val GREEN_LIGHT = 0xFF99cc00.toInt()
    val ORANGE = 0xFFff8800.toInt()
    val ORANGE_DARK = 0xFFdd6611.toInt()
    val ORANGE_LIGHT = 0xFFffbb33.toInt()
    val SKYBLUE = 0xFF33b5e5.toInt()
    val SKYBLUE_BRIGHT = 0xFF00ddff.toInt()
    val SKYBLUE_DARK = 0xFF0099cc.toInt()

    object App {
        val PRIMARY = rgb(63, 81, 181)
        val SECONDARY = rgb(48, 63, 159)
        val ACCENT = rgb(255,64,129)
    }
    object Markdown {
        private val header = 25
        val H1 = 0xFF111111.toInt()
        val H2 = 0xFF222222.toInt()
        val H3 = rgb(header*3,header*3,header*3)
        val H4 = rgb(header*4,header*4,header*4)
        val H5 = rgb(header*5,header*5,header*5)
        val TEXT_BODY = 0xFFff0000.toInt()
        val TIMESTAMP = ORANGE_DARK
    }
}