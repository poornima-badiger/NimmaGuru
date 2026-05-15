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

        val etEmail = findViewById<EditText>(R.id.etGuruEmail)
        val etPassword = findViewById<EditText>(R.id.etGuruPassword)
        val btnLogin = findViewById<Button>(R.id.btnGuruLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Try login first; if no account, register automatically
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, GuruProfileSetupActivity::class.java))
                        finish()
                    } else {
                        // Account doesn't exist — create one
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { regTask ->
                                if (regTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Guru account created!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(
                                        Intent(this, GuruProfileSetupActivity::class.java)
                                    )
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Login Failed: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                }
        }
    }
}
