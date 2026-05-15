package com.example.nimmaguru.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvName = findViewById<TextView>(R.id.tvGuruName)
        val tvSubject = findViewById<TextView>(R.id.tvGuruSubject)
        val tvDescription = findViewById<TextView>(R.id.tvGuruDescription)
        val tvTime = findViewById<TextView>(R.id.tvGuruTime)
        val tvLocation = findViewById<TextView>(R.id.tvLocation)

        val btnBookSession = findViewById<Button>(R.id.btnBookSession)
        val btnEdit = findViewById<Button>(R.id.btnEditProfile)

        // GET DATA FROM INTENT (IMPORTANT FIX HERE)
        val docId = intent.getStringExtra("docId") ?: ""

        val guruName = intent.getStringExtra("guru_name") ?: ""
        val guruSubject = intent.getStringExtra("guru_subject") ?: ""
        val guruTime = intent.getStringExtra("guru_time") ?: ""
        val guruLocation = intent.getStringExtra("guru_location") ?: ""

        // SET DATA
        tvName.text = guruName
        tvSubject.text = guruSubject
        tvDescription.text = "Experienced mentor helping students learn effectively."
        tvTime.text = guruTime
        tvLocation.text = guruLocation

        // BOOK SESSION BUTTON
        btnBookSession.setOnClickListener {

            val bookingIntent = Intent(this, BookingActivity::class.java)

            bookingIntent.putExtra("guru_name", guruName)

            startActivity(bookingIntent)

            Toast.makeText(
                this,
                "Opening Booking Page",
                Toast.LENGTH_SHORT
            ).show()
        }

        // EDIT BUTTON
        btnEdit.setOnClickListener {

            val intent = Intent(this, EditProfileActivity::class.java)

            intent.putExtra("docId", docId)
            intent.putExtra("name", guruName)
            intent.putExtra("subject", guruSubject)
            intent.putExtra("time", guruTime)
            intent.putExtra("location", guruLocation)

            startActivity(intent)
        }
    }
}