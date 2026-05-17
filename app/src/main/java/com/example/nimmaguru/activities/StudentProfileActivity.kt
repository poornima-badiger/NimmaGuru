package com.example.nimmaguru.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentProfileActivity : BaseActivity() {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile)

        val etName      = findViewById<EditText>(R.id.etStudentName)
        val etClass     = findViewById<EditText>(R.id.etStudentClass)
        val etSchool    = findViewById<EditText>(R.id.etStudentSchool)
        val etInterests = findViewById<EditText>(R.id.etStudentInterests)
        val btnSave     = findViewById<Button>(R.id.btnSaveStudentProfile)
        val btnBack     = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        // Load existing profile if any
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("students").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        etName.setText(doc.getString("name") ?: "")
                        etClass.setText(doc.getString("class") ?: "")
                        etSchool.setText(doc.getString("school") ?: "")
                        etInterests.setText(doc.getString("interests") ?: "")
                    }
                }
        }

        btnSave.setOnClickListener {
            val name      = etName.text.toString().trim()
            val cls       = etClass.text.toString().trim()
            val school    = etSchool.text.toString().trim()
            val interests = etInterests.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Enter your name"; return@setOnClickListener
            }

            val data = hashMapOf(
                "name"      to name,
                "class"     to cls,
                "school"    to school,
                "interests" to interests,
                "uid"       to (uid ?: "guest")
            )

            val docRef = if (uid != null)
                db.collection("students").document(uid)
            else
                db.collection("students").document()

            docRef.set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile saved! ✅", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Save failed. Try again.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
