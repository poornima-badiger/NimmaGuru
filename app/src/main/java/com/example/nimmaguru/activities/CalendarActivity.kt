package com.example.nimmaguru.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.nimmaguru.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CalendarActivity : BaseActivity() {

    private lateinit var container: LinearLayout
    private lateinit var tvEmpty: TextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        container = findViewById(R.id.layoutSessionContainer)
        tvEmpty = findViewById(R.id.tvEmpty)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        loadBookings()
    }

    private fun loadBookings() {
        db.collection("bookings")
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                showSessions(result.documents.mapNotNull { doc ->
                    Triple(
                        doc.getString("guruName") ?: "",
                        doc.getString("date") ?: "",
                        doc.getString("time") ?: ""
                    )
                })
            }
            .addOnFailureListener {
                // Firestore index not created yet — load without ordering
                db.collection("bookings")
                    .get()
                    .addOnSuccessListener { result ->
                        showSessions(result.documents.mapNotNull { doc ->
                            Triple(
                                doc.getString("guruName") ?: "",
                                doc.getString("date") ?: "",
                                doc.getString("time") ?: ""
                            )
                        })
                    }
            }
    }

    private fun showSessions(sessions: List<Triple<String, String, String>>) {
        // Remove all views except tvEmpty
        val childCount = container.childCount
        for (i in childCount - 1 downTo 0) {
            val child = container.getChildAt(i)
            if (child.id != R.id.tvEmpty) {
                container.removeViewAt(i)
            }
        }

        if (sessions.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
        } else {
            tvEmpty.visibility = View.GONE
            for ((guruName, date, time) in sessions) {
                container.addView(buildSessionCard(guruName, date, time))
            }
        }
    }

    private fun buildSessionCard(guruName: String, date: String, time: String): CardView {
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
        inner.orientation = LinearLayout.HORIZONTAL
        inner.setPadding(30, 25, 30, 25)
        inner.gravity = Gravity.CENTER_VERTICAL

        // Date badge
        val dateBadge = LinearLayout(this)
        dateBadge.orientation = LinearLayout.VERTICAL
        dateBadge.gravity = Gravity.CENTER
        dateBadge.setBackgroundColor(Color.parseColor("#0D47A1"))
        dateBadge.setPadding(20, 20, 20, 20)
        val badgeParams = LinearLayout.LayoutParams(120, 120)
        badgeParams.setMargins(0, 0, 20, 0)
        dateBadge.layoutParams = badgeParams

        val tvDate = TextView(this)
        tvDate.text = date
        tvDate.textSize = 12f
        tvDate.setTextColor(Color.WHITE)
        tvDate.gravity = Gravity.CENTER
        tvDate.setTypeface(null, android.graphics.Typeface.BOLD)
        dateBadge.addView(tvDate)

        // Details
        val details = LinearLayout(this)
        details.orientation = LinearLayout.VERTICAL
        details.layoutParams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )

        val tvGuru = TextView(this)
        tvGuru.text = "👨‍🏫 $guruName"
        tvGuru.textSize = 17f
        tvGuru.setTypeface(null, android.graphics.Typeface.BOLD)
        tvGuru.setTextColor(Color.parseColor("#222222"))

        val tvTime = TextView(this)
        tvTime.text = "⏰ $time"
        tvTime.textSize = 14f
        tvTime.setTextColor(Color.parseColor("#555555"))
        tvTime.setPadding(0, 6, 0, 0)

        val tvStatus = TextView(this)
        tvStatus.text = "✅ Confirmed"
        tvStatus.textSize = 13f
        tvStatus.setTextColor(Color.parseColor("#2E7D32"))
        tvStatus.setPadding(0, 6, 0, 0)

        details.addView(tvGuru)
        details.addView(tvTime)
        details.addView(tvStatus)

        inner.addView(dateBadge)
        inner.addView(details)
        card.addView(inner)
        return card
    }
}
