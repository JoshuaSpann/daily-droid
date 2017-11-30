package com.example.jspann.textfilewriter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_color_selector.*

class ColorSelectorActivity : AppCompatActivity() {

    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_selector)
        val btnBlueBright = findViewById<View>(R.id.btn_blue_bright)
        btnBlueBright.setOnClickListener {
            utils.popup(applicationContext, "HI")
        }

        var buttons: List<View>
        buttons = (findViewById<View>(R.id.container_color_buttons) as ConstraintLayout).getTouchables()
        //for(button: Button in buttons){
        //
        //}
        utils.popup(applicationContext, buttons[0].id)
    }

}
