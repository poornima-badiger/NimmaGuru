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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guru_profile_setup)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtSubject = findViewById<EditText>(R.id.edtSubject)
        val edtTime = findViewById<EditText>(R.id.edtTime)
        val edtLocation = findViewById<EditText>(R.id.edtLocation)

        val btnSave = findViewById<Button>(R.id.btnSaveGuru)

        btnSave.setOnClickListener {

            val db = FirebaseFirestore.getInstance()

            val name = edtName.text.toString()
            val subject = edtSubject.text.toString()
            val time = edtTime.text.toString()
            val location = edtLocation.text.toString()

            val guru = hashMapOf(
                "name" to name,
                "subject" to subject,
                "time" to time,
                "location" to location,
                "image" to "https://i.pravatar.cc/150?img=3"
            )
            db.collection("gurus")
                .add(guru)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Guru Saved Successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(this, HomeActivity::class.java))

                    finish()
                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Save Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}