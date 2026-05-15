package com.example.nimmaguru.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.nimmaguru.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AppreciationActivity : AppCompatActivity() {

    private lateinit var etGuruName: EditText
    private lateinit var etMessage: EditText
    private lateinit var btnSubmit: Button
    private lateinit var container: LinearLayout
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appreciation)

        etGuruName = findViewById(R.id.etGuruNameAppreciation)
        etMessage = findViewById(R.id.etThankYouMessage)
        btnSubmit = findViewById(R.id.btnSubmitAppreciation)
        container = findViewById(R.id.layoutAppreciationContainer)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        // Load existing appreciations
        loadAppreciations()

        // Submit new appreciation
        btnSubmit.setOnClickListener {
            val guruName = etGuruName.text.toString().trim()
            val message = etMessage.text.toString().trim()

            if (guruName.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val appreciation = hashMapOf(
                "guruName" to guruName,
                "message" to message,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("appreciations")
                .add(appreciation)
                .addOnSuccessListener {
                    Toast.makeText(this, "Thank you posted! 🙏", Toast.LENGTH_SHORT).show()
                    etGuruName.text.clear()
                    etMessage.text.clear()
                    loadAppreciations()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to post. Try again.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadAppreciations() {
        db.collection("appreciations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                container.removeAllViews()

                if (result.isEmpty) {
                    val tv = TextView(this)
                    tv.text = "No appreciations yet. Be the first to say Thank You! 🙏"
                    tv.textSize = 15f
                    tv.setTextColor(Color.parseColor("#999999"))
                    tv.gravity = Gravity.CENTER
                    tv.setPadding(20, 40, 20, 40)
                    container.addView(tv)
                    return@addOnSuccessListener
                }

                for (doc in result) {
                    val guruName = doc.getString("guruName") ?: ""
                    val message = doc.getString("message") ?: ""
                    addAppreciationCard(guruName, message)
                }
            }
    }

    private fun addAppreciationCard(guruName: String, message: String) {
        val card = CardView(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)
        card.layoutParams = params
        card.radius = 16f
        card.cardElevation = 6f
        card.setCardBackgroundColor(Color.WHITE)

        val inner = LinearLayout(this)
        inner.orientation = LinearLayout.VERTICAL
        inner.setPadding(40, 30, 40, 30)

        // Star emoji + guru name
        val tvGuru = TextView(this)
        tvGuru.text = "⭐ Thank you, $guruName!"
        tvGuru.textSize = 16f
        tvGuru.setTypeface(null, android.graphics.Typeface.BOLD)
        tvGuru.setTextColor(Color.parseColor("#0D47A1"))

        // Message
        val tvMsg = TextView(this)
        tvMsg.text = "\"$message\""
        tvMsg.textSize = 14f
        tvMsg.setTextColor(Color.parseColor("#444444"))
        tvMsg.setPadding(0, 10, 0, 0)

        inner.addView(tvGuru)
        inner.addView(tvMsg)
        card.addView(inner)
        container.addView(card)
    }
}
