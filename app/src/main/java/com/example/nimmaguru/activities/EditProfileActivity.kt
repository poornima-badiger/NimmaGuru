package com.example.nimmaguru.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : BaseActivity() {

    lateinit var edtName: EditText
    lateinit var edtSubject: EditText
    lateinit var edtTime: EditText
    lateinit var edtLocation: EditText
    lateinit var btnUpdate: Button

    lateinit var docId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // INIT UI
        edtName = findViewById(R.id.edtName)
        edtSubject = findViewById(R.id.edtSubject)
        edtTime = findViewById(R.id.edtTime)
        edtLocation = findViewById(R.id.edtLocation)
        btnUpdate = findViewById(R.id.btnUpdate)

        val btnBack = findViewById<android.widget.ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        // GET DATA FROM INTENT
        docId = intent.getStringExtra("docId") ?: ""

        val name = intent.getStringExtra("name") ?: ""
        val subject = intent.getStringExtra("subject") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val location = intent.getStringExtra("location") ?: ""

        // SET DATA TO FIELDS
        edtName.setText(name)
        edtSubject.setText(subject)
        edtTime.setText(time)
        edtLocation.setText(location)

        // UPDATE BUTTON
        btnUpdate.setOnClickListener {

            val db = FirebaseFirestore.getInstance()

            val updatedData = hashMapOf<String, Any>(
                "name" to edtName.text.toString(),
                "subject" to edtSubject.text.toString(),
                "time" to edtTime.text.toString(),
                "location" to edtLocation.text.toString()
            )

            db.collection("gurus")
                .document(docId)
                .update(updatedData)
                .addOnSuccessListener {

                    Toast.makeText(this, "Profile Updated",  Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {

                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}