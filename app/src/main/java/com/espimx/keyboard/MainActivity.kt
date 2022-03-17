package com.espimx.keyboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout

class MainActivity : AppCompatActivity() {
    private var safeKeyboard: SafeKeyboard? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val et1 = findViewById<EditText>(R.id.et_1)
        val et2 = findViewById<EditText>(R.id.et_2)
        val et3 = findViewById<EditText>(R.id.et_3)

        val rootView = findViewById<RelativeLayout>(R.id.rl_layout)
        val keyboardContainer = findViewById<LinearLayout>(R.id.ll_keyboard)
        safeKeyboard = SafeKeyboard(applicationContext, keyboardContainer,
            R.layout.layout_keyboard_containor, R.id.safeKeyboardView, rootView, rootView)
        safeKeyboard?.apply {
            putEditText(et1)
            putEditText(et2)
            putEditText(et3)
            putEditText2IdCardType(et1.id, et1)
        }
    }

    override fun onBackPressed() {
        if (safeKeyboard != null && (safeKeyboard!!.stillNeedOptManually(false) || safeKeyboard!!.isShow)) {
            safeKeyboard!!.hideKeyboard()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        safeKeyboard?.release()
        safeKeyboard = null
    }
}