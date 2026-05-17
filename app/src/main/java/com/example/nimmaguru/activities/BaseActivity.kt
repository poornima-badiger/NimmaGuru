package com.example.nimmaguru.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.utils.LocaleHelper

/**
 * All activities extend this class.
 * It applies the saved language locale BEFORE the layout is inflated,
 * so every screen automatically shows the correct language.
 */
open class BaseActivity : AppCompatActivity() {

    // Called by Android before setContentView — perfect place to apply locale
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }
}
