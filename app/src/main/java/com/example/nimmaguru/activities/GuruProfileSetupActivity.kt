package com.example.nimmaguru.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R
import com.google.firebase.firestore.FirebaseFirestore

class GuruProfileSetupActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guru_profile_setup)

        val edtName     = findViewById<EditText>(R.id.edtName)
        val edtSubject  = findViewById<EditText>(R.id.edtSubject)
        val edtTime     = findViewById<EditText>(R.id.edtTime)
        val edtLocation = findViewById<EditText>(R.id.edtLocation)
        val btnSave     = findViewById<Button>(R.id.btnSaveGuru)
        val btnGoHome   = findViewById<Button>(R.id.btnGoHome)

        // Go to Home without saving
        btnGoHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Save profile to Firestore
        btnSave.setOnClickListener {

            val name     = edtName.text.toString().trim()
            val subject  = edtSubject.text.toString().trim()
            val time     = edtTime.text.toString().trim()
            val location = edtLocation.text.toString().trim()

            // Validation
            if (name.isEmpty()) {
                edtName.error = "Please enter your name"
                edtName.requestFocus()
                return@setOnClickListener
            }
            if (subject.isEmpty()) {
                edtSubject.error = "Please enter your subject"
                edtSubject.requestFocus()
                return@setOnClickListener
            }
            if (time.isEmpty()) {
                edtTime.error = "Please enter your free hours"
                edtTime.requestFocus()
                return@setOnClickListener
            }
            if (location.isEmpty()) {
                edtLocation.error = "Please enter your village/location"
                edtLocation.requestFocus()
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            btnSave.text = "Saving..."

            val guru = hashMapOf(
                "name"     to name,
                "subject"  to subject,
                "time"     to time,
                "location" to location,
                "image"    to "https://i.pravatar.cc/150?img=3"
            )

            db.collection("gurus")
                .add(guru)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Profile saved! Welcome, $name 🎉",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    btnSave.isEnabled = true
                    btnSave.text = "✅  Save Guru Profile"
                    Toast.makeText(
                        this,
                        "Save failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}
