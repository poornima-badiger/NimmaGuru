package com.example.nimmaguru.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R
import com.google.firebase.auth.FirebaseAuth

class GuruLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guru_login)

        auth = FirebaseAuth.getInstance()

        val etEmail    = findViewById<EditText>(R.id.etGuruEmail)
        val etPassword = findViewById<EditText>(R.id.etGuruPassword)
        val btnLogin   = findViewById<Button>(R.id.btnGuruLogin)
        val btnSkip    = findViewById<Button>(R.id.btnGuruSkip)

        // SKIP — go directly to Guru Profile Setup
        btnSkip.setOnClickListener {
            goToGuruHome()
        }

        // LOGIN / AUTO-REGISTER
        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            btnLogin.text = "Please wait..."

            // Try login first
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Welcome back, Guru! 👨‍🏫", Toast.LENGTH_SHORT).show()
                        goToGuruHome()
                    } else {
                        // Account doesn't exist — auto create
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { regTask ->
                                btnLogin.isEnabled = true
                                btnLogin.text = "Login / Register"
                                if (regTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Guru account created! 🎉",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    goToGuruHome()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Error: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                }
        }
    }

    private fun goToGuruHome() {
        val intent = Intent(this, GuruProfileSetupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
