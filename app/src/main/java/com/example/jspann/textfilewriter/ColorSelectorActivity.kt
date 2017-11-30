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

        var buttons: List<Button>
        buttons = (findViewById<View>(R.id.container_color_buttons) as ConstraintLayout).getTouchables() as List<Button>
        for(button: Button in buttons){
            button.setOnClickListener{
                //utils.popup(applicationContext, button.background)
                try {
                    //TODO - Set MainActivity landing page color button bg to colorselector selected button, also store in config file?
                    (findViewById<View>(R.id.button_colorLauncher) as? Button)?.setBackgroundColor(button.background as Int)
                }catch(e: Exception){
                    utils.popup(applicationContext, e)
                }
                this.finish()
            }
        }
    }

}
