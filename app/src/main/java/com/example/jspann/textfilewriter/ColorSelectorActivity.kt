package com.example.jspann.textfilewriter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_color_selector.*

class ColorSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_selector)
        val btnBlueBright = findViewById<View>(R.id.btn_blue_bright)
        btnBlueBright.setOnClickListener {
            popup("HI")
        }
    }

    private fun popup(data: Any) {
        android.widget.Toast.makeText(applicationContext, data.toString(), Toast.LENGTH_LONG).show()
    }
}
