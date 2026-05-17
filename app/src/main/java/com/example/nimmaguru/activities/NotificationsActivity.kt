package com.example.nimmaguru.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.nimmaguru.R
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsActivity : BaseActivity() {

    private lateinit var container: LinearLayout
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        container = findViewById(R.id.layoutNotificationsContainer)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        loadNotifications()
    }

    private fun loadNotifications() {
        // Show recent bookings as notifications
        db.collection("bookings")
            .get()
            .addOnSuccessListener { result ->
                container.removeAllViews()

                if (result.isEmpty) {
                    val tv = TextView(this)
                    tv.text = "No notifications yet.\nBook a session to get started!"
                    tv.textSize = 15f
                    tv.setTextColor(Color.parseColor("#999999"))
                    tv.gravity = android.view.Gravity.CENTER
                    tv.setPadding(20, 60, 20, 20)
                    container.addView(tv)
                    return@addOnSuccessListener
                }

                for (doc in result) {
                    val guruName = doc.getString("guruName") ?: ""
                    val date = doc.getString("date") ?: ""
                    val time = doc.getString("time") ?: ""
                    addNotificationCard(
                        "📅 Session Booked",
                        "Your session with $guruName is confirmed for $date at $time"
                    )
                }
            }
    }

    private fun addNotificationCard(title: String, message: String) {
        val card = CardView(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 12)
        card.layoutParams = params
        card.radius = 14f
        card.cardElevation = 4f
        card.setCardBackgroundColor(Color.WHITE)

        val inner = LinearLayout(this)
        inner.orientation = LinearLayout.VERTICAL
        inner.setPadding(30, 25, 30, 25)

        val tvTitle = TextView(this)
        tvTitle.text = title
        tvTitle.textSize = 16f
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD)
        tvTitle.setTextColor(Color.parseColor("#0D47A1"))

        val tvMsg = TextView(this)
        tvMsg.text = message
        tvMsg.textSize = 14f
        tvMsg.setTextColor(Color.parseColor("#444444"))
        tvMsg.setPadding(0, 8, 0, 0)

        inner.addView(tvTitle)
        inner.addView(tvMsg)
        card.addView(inner)
        container.addView(card)
    }
}
