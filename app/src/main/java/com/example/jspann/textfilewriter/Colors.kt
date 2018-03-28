package com.example.jspann.textfilewriter

import android.graphics.Color
import android.graphics.Color.rgb
import android.graphics.drawable.ColorDrawable

/**
 * Created by jspann on 3/20/2018.
 */
object Colors {
    val BLACK = 0xFF000000.toInt()

    val GRAY = 0xFF777777.toInt()
    val GRAY_BRIGHT = GRAY + 0xFF777777.toInt()
    val GRAY_DARK = GRAY - 0x00444444
    val GRAY_HIGHLIGHT = GRAY + 0xFF555555.toInt()
    val GRAY_LIGHT = GRAY + 0xFF444444.toInt()

    val GREEN = 0xFF669900.toInt()
    val GREEN_BRIGHT = 0xFF88ff88.toInt()
    val GREEN_LIGHT = 0xFF99cc00.toInt()

    val ORANGE = 0xFFff8800.toInt()
    val ORANGE_DARK = 0xFFdd6611.toInt()
    val ORANGE_LIGHT = 0xFFffbb33.toInt()

    val PURPLE_LIGHT = 0xFFaa66cc.toInt()

    val RED = 0xFFcc0000.toInt()
    val RED_LIGHT = 0xFFff4444.toInt()

    val SKYBLUE = 0xFF33b5e5.toInt()
    val SKYBLUE_BRIGHT = 0xFF00ddff.toInt()
    val SKYBLUE_DARK = 0xFF0099cc.toInt()

    val WHITE = 0xFFeeeeee.toInt()
    val YELLOW = 0xFFffbb33.toInt()

    object App {
        val PRIMARY = 0xFF3F51B5.toInt()
        val SECONDARY = 0xFF303F9F.toInt()
        val ACCENT = 0xFFFF4081.toInt()
    }
    object Markdown {
        val BOLD = BLACK
        val CODE = GRAY_LIGHT
        val H1 = App.PRIMARY//0xFF111111.toInt()
        val H2 = App.SECONDARY
        val H3 = GRAY_DARK
        val H4 = GRAY
        val H5 = GRAY_LIGHT
        val ITALICS = GRAY_DARK
        val LIST_ITEM = GRAY
        val TEXT_BODY = 0xFF444444.toInt()
        val TIMESTAMP = App.ACCENT
    }
}