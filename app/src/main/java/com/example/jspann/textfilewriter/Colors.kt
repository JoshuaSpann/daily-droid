package com.example.jspann.textfilewriter

import android.graphics.Color
import android.graphics.Color.rgb
import android.graphics.drawable.ColorDrawable

/**
 * Created by jspann on 3/20/2018.
 */
class Colors {
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
        //val h1 = rgb(header, header, header)
        //val h1 = Color.parseColor("#222222")
        val h1 = 0xFF111111.toInt()
        //val h2 = rgb(header*2,header*2,header*2)
        val h2 = 0xFF222222.toInt()
        val h3 = rgb(header*3,header*3,header*3)
        val h4 = rgb(header*4,header*4,header*4)
        val h5 = rgb(header*5,header*5,header*5)
        //val text = rgb(120,120,120)
        //val text = Color.parseColor("#333333")
        val text = 0xFFff0000.toInt()
        //val TIMESTAMP = ORANGE_DARK
    }
}