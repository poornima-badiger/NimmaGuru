package com.example.nimmaguru.activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    // Current language — default English
    private var currentLang = "en"

    // UI references
    private lateinit var btnLangEnglish  : TextView
    private lateinit var btnLangKannada  : TextView
    private lateinit var tvAppName       : TextView
    private lateinit var tvTagline       : TextView
    private lateinit var tvKannadaTagline: TextView
    private lateinit var tvWhoAreYou     : TextView
    private lateinit var btnStudent      : Button
    private lateinit var btnGuru         : Button
    private lateinit var txtRegister     : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved language before setContentView
        val prefs = getSharedPreferences("NimmaGuruPrefs", MODE_PRIVATE)
        currentLang = prefs.getString("language", "en") ?: "en"
        applyLocale(currentLang)

        setContentView(R.layout.activity_login)

        // Bind views
        btnLangEnglish   = findViewById(R.id.btnLangEnglish)
        btnLangKannada   = findViewById(R.id.btnLangKannada)
        tvAppName        = findViewById(R.id.tvAppName)
        tvTagline        = findViewById(R.id.tvTagline)
        tvKannadaTagline = findViewById(R.id.tvKannadaTagline)
        tvWhoAreYou      = findViewById(R.id.tvWhoAreYou)
        btnStudent       = findViewById(R.id.btnStudent)
        btnGuru          = findViewById(R.id.btnGuru)
        txtRegister      = findViewById(R.id.txtRegister)

        // Apply correct UI text for saved language
        updateUI(currentLang)

        // ── LANGUAGE BUTTONS ─────────────────────────────────────────
        btnLangEnglish.setOnClickListener {
            switchLanguage("en")
        }

        btnLangKannada.setOnClickListener {
            switchLanguage("kn")
        }

        // ── NAVIGATION ───────────────────────────────────────────────
        btnStudent.setOnClickListener {
            startActivity(Intent(this, StudentLoginActivity::class.java))
        }

        btnGuru.setOnClickListener {
            startActivity(Intent(this, GuruLoginActivity::class.java))
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // Switch language and update UI instantly
    private fun switchLanguage(lang: String) {
        currentLang = lang

        // Save preference
        getSharedPreferences("NimmaGuruPrefs", MODE_PRIVATE)
            .edit().putString("language", lang).apply()

        // Apply locale
        applyLocale(lang)

        // Update all text on screen instantly
        updateUI(lang)
    }

    // Update all text based on selected language
    private fun updateUI(lang: String) {
        if (lang == "kn") {
            // ── KANNADA ──────────────────────────────────────────────
            tvAppName.text        = "ನಿಮ್ಮ-ಗುರು"
            tvTagline.text        = "ಪ್ರತಿ ಹಳ್ಳಿಗೂ ಜ್ಞಾನದ ಹಂಚಿಕೆ"
            tvKannadaTagline.text = "Knowledge Sharing for Every Village"
            tvWhoAreYou.text      = "ನೀವು ಯಾರು?"
            btnStudent.text       = "🎓  ನಾನು ವಿದ್ಯಾರ್ಥಿ"
            btnGuru.text          = "👨‍🏫  ನಾನು ಗುರು"
            txtRegister.text      = "ಹೊಸ ಬಳಕೆದಾರರೇ? ನೋಂದಾಯಿಸಿ"

            // Highlight Kannada button, dim English
            btnLangKannada.setBackgroundColor(Color.parseColor("#0D47A1"))
            btnLangKannada.setTextColor(Color.WHITE)
            btnLangEnglish.setBackgroundColor(Color.parseColor("#E3F2FD"))
            btnLangEnglish.setTextColor(Color.parseColor("#0D47A1"))

        } else {
            // ── ENGLISH ──────────────────────────────────────────────
            tvAppName.text        = "Nimma-Guru"
            tvTagline.text        = "Knowledge Sharing for Every Village"
            tvKannadaTagline.text = "ಪ್ರತಿ ಹಳ್ಳಿಗೂ ಜ್ಞಾನದ ಹಂಚಿಕೆ"
            tvWhoAreYou.text      = "Who are you?"
            btnStudent.text       = "🎓  I am a Student"
            btnGuru.text          = "👨‍🏫  I am a Guru"
            txtRegister.text      = "New User? Register Here"

            // Highlight English button, dim Kannada
            btnLangEnglish.setBackgroundColor(Color.parseColor("#0D47A1"))
            btnLangEnglish.setTextColor(Color.WHITE)
            btnLangKannada.setBackgroundColor(Color.parseColor("#E3F2FD"))
            btnLangKannada.setTextColor(Color.parseColor("#0D47A1"))
        }
    }

    // Apply locale to context
    private fun applyLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
