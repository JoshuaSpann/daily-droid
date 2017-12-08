/**
 * Daily Droid :: ColorSelectorActivity
 *
 * PURPOSE:
 *   Landing-point controller for the ColorSelectorActivity, which is in charge of selecting and
 *   applying folder/document colors to color-configuration file.
 *
 * MAIN CONTRIBUTORS:
 *   Joshua Spann (jspann) - Author
 *
 * STRUCTURE:
 *   imports
 *   utilty and view declarations
 *   launch controller
 **/

/* /  INPORTS  / */
package com.example.jspann.textfilewriter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button

class ColorSelectorActivity : AppCompatActivity() {

    /* /  UTILITY AND VIEW DECLARATIONS  / */
    private var config = Config()
    private val utils = Utils()

    /* /  LAUNCH CONTROLLER  / */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_selector)

        var buttons: List<Button>
        buttons = (findViewById<View>(R.id.container_color_buttons) as ConstraintLayout).getTouchables() as List<Button>
        for(button: Button in buttons){
            button.setOnClickListener{
                val MainActivity__strFileName: String = "dailydroid__"+intent.getStringExtra("currentSelectedFile")
                if(button.id == R.id.btn_default){
                    config.removePreference(this,MainActivity__strFileName)
                    utils.popup(this,"Color Reset to Default")
                    this.finish()
                }
                else {
                    val strButtonColor: String = Integer.toHexString((button.background as ColorDrawable).color)
                    try {
                        config.setPreference(this, MainActivity__strFileName, strButtonColor)
                        utils.popup(this, "Color Set")
                    } catch (e: Exception) {
                        utils.popup(applicationContext, e)
                    }
                    this.finish()
                }
            }
        }
    }

}
