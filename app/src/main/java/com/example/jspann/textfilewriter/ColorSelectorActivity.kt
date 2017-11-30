package com.example.jspann.textfilewriter

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button

class ColorSelectorActivity : AppCompatActivity() {

    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_selector)
        val btnBlueBright = findViewById<View>(R.id.btn_blue_bright)
        btnBlueBright.setOnClickListener {
            utils.popup(applicationContext, "HI")
        }

        var buttons: List<Button>
        buttons = (findViewById<View>(R.id.container_color_buttons) as ConstraintLayout).getTouchables() as List<Button>
        for(button: Button in buttons){
            button.setOnClickListener{
                //utils.popup(applicationContext, button.background)
                try {
                    (findViewById<View>(R.id.button_colorLauncher) as? Button)?.setBackgroundColor(button.drawingCacheBackgroundColor)
                }catch(e: Exception){
                    utils.popup(applicationContext, e)
                }
                //this.finish()
            }
        }
        utils.popup(applicationContext, buttons[0].id)
    }

}
