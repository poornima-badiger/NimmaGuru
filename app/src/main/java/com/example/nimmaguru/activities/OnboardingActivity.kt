package com.example.nimmaguru.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_onboarding)

        val btnStart = findViewById<Button>(R.id.btnStart)

        btnStart.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}