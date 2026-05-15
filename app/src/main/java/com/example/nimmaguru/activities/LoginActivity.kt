package com.example.nimmaguru.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnStudent = findViewById<Button>(R.id.btnStudent)
        val btnGuru = findViewById<Button>(R.id.btnGuru)
        val txtRegister = findViewById<TextView>(R.id.txtRegister)

        // Student button → Student Login screen
        btnStudent.setOnClickListener {
            startActivity(Intent(this, StudentLoginActivity::class.java))
        }

        // Guru button → Guru Login screen
        btnGuru.setOnClickListener {
            startActivity(Intent(this, GuruLoginActivity::class.java))
        }

        // Register link → Register screen
        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
