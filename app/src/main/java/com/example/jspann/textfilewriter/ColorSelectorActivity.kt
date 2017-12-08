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
                val strButtonColor: String = Integer.toHexString((button.background as ColorDrawable).color)
                utils.popup(applicationContext,strButtonColor)
                try {
                    //TODO - Set MainActivity landing page color bg to colorselector selected button, also store in config file?
                    //(findViewById<View>(R.id.button_colorLauncher) as? Button)?.setBackgroundColor(button.background as Int)
                    //val files = utils.getListOfAllFilenamesInDir(utils.getDirectoryPathToString())
                    //config.fileColors.put(files[0].toString(), strButtonColor)
                    //config.fileColors.put(files[1].toString(), strButtonColor)
                    config.setPreference(this,"dailydroid__"+intent.getStringExtra("currentSelectedFile"), strButtonColor)
                }catch(e: Exception){
                    utils.popup(applicationContext, e)
                }
                this.finish()
            }
        }
    }

}
