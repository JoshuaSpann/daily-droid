package com.example.jspann.textfilewriter

import android.graphics.Color.rgb

/**
 * Created by jspann on 3/20/2018.
 */
class Colors {
    object APP {
        val PRIMARY = rgb(63, 81, 181)
        val SECONDARY = rgb(48, 63, 159)
        val ACCENT = rgb(255,64,129)
    }
    object Markdown {
        private val header = 25
        val h1 = rgb(header, header, header)
        val h2 = rgb(header*2,header*2,header*2)
        val h3 = rgb(header*3,header*3,header*3)
        val h4 = rgb(header*4,header*4,header*4)
        val h5 = rgb(header*5,header*5,header*5)
        val text = rgb(120,120,120)
    }
}