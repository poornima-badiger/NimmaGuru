package com.example.nimmaguru.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R
import com.google.firebase.firestore.FirebaseFirestore

class BookingActivity : AppCompatActivity() {

    lateinit var tvGuruName: TextView
    lateinit var etDate: EditText
    lateinit var etTime: EditText
    lateinit var btnConfirm: Button

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        tvGuruName = findViewById(R.id.tvGuruName)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        btnConfirm = findViewById(R.id.btnConfirmBooking)

        val btnBack = findViewById<android.widget.ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val guruName = intent.getStringExtra("guru_name")

        tvGuruName.text = "Booking for $guruName"

        btnConfirm.setOnClickListener {

            val date = etDate.text.toString()
            val time = etTime.text.toString()

            if (date.isEmpty() || time.isEmpty()) {

                Toast.makeText(
                    this,
                    "Enter date and time",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val booking = hashMapOf(
                    "guruName" to guruName,
                    "date" to date,
                    "time" to time
                )

                db.collection("bookings")
                    .add(booking)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Session Booked Successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                    }
            }
        }
    }
}