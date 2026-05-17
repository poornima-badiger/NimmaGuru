package com.example.nimmaguru.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupLanguage)
        val radioEnglish = findViewById<RadioButton>(R.id.radioEnglish)
        val radioKannada = findViewById<RadioButton>(R.id.radioKannada)
        val btnApplyLanguage = findViewById<Button>(R.id.btnApplyLanguage)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnBack.setOnClickListener { finish() }

        // Restore saved language preference
        val prefs = getSharedPreferences("NimmaGuruPrefs", MODE_PRIVATE)
        val savedLang = prefs.getString("language", "en")
        if (savedLang == "kn") {
            radioKannada.isChecked = true
        } else {
            radioEnglish.isChecked = true
        }

        // Apply language button
        btnApplyLanguage.setOnClickListener {
            val selectedLang = if (radioKannada.isChecked) "kn" else "en"

            // Save preference
            prefs.edit().putString("language", selectedLang).apply()

            // Apply locale
            applyLocale(selectedLang)

            Toast.makeText(
                this,
                if (selectedLang == "kn") "ಭಾಷೆ ಬದಲಾಯಿಸಲಾಗಿದೆ!" else "Language changed to English!",
                Toast.LENGTH_SHORT
            ).show()

            // Restart HomeActivity to apply
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Logout
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun applyLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
