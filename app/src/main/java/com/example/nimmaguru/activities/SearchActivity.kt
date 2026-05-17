package com.example.nimmaguru.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.nimmaguru.R
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : BaseActivity() {

    private lateinit var etSearch: EditText
    private lateinit var container: LinearLayout
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        etSearch = findViewById(R.id.etSearch)
        container = findViewById(R.id.layoutSearchResults)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        // Load all gurus initially
        searchGurus("")

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchGurus(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchGurus(query: String) {
        db.collection("gurus")
            .get()
            .addOnSuccessListener { result ->
                container.removeAllViews()

                var found = false
                for (doc in result) {
                    val docId = doc.id
                    val name = doc.getString("name") ?: ""
                    val subject = doc.getString("subject") ?: ""
                    val time = doc.getString("time") ?: ""
                    val location = doc.getString("location") ?: ""

                    val matches = query.isEmpty() ||
                            name.lowercase().contains(query.lowercase()) ||
                            subject.lowercase().contains(query.lowercase()) ||
                            location.lowercase().contains(query.lowercase())

                    if (matches) {
                        found = true
                        addResultCard(docId, name, subject, time, location)
                    }
                }

                if (!found) {
                    val tv = TextView(this)
                    tv.text = "No gurus found for \"$query\""
                    tv.textSize = 15f
                    tv.setTextColor(Color.parseColor("#999999"))
                    tv.gravity = android.view.Gravity.CENTER
                    tv.setPadding(20, 40, 20, 40)
                    container.addView(tv)
                }
            }
    }

    private fun addResultCard(
        docId: String, name: String, subject: String,
        time: String, location: String
    ) {
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
        inner.setPadding(30, 25, 30, 25)

        val tvName = TextView(this)
        tvName.text = "👨‍🏫 $name"
        tvName.textSize = 18f
        tvName.setTypeface(null, android.graphics.Typeface.BOLD)
        tvName.setTextColor(Color.parseColor("#0D47A1"))

        val tvSubject = TextView(this)
        tvSubject.text = "📘 $subject"
        tvSubject.textSize = 15f
        tvSubject.setTextColor(Color.parseColor("#444444"))
        tvSubject.setPadding(0, 6, 0, 0)

        val tvTime = TextView(this)
        tvTime.text = "⏰ $time"
        tvTime.textSize = 14f
        tvTime.setTextColor(Color.parseColor("#666666"))
        tvTime.setPadding(0, 4, 0, 0)

        val tvLocation = TextView(this)
        tvLocation.text = "📍 $location"
        tvLocation.textSize = 14f
        tvLocation.setTextColor(Color.parseColor("#666666"))
        tvLocation.setPadding(0, 4, 0, 0)

        val btnView = Button(this)
        btnView.text = "View Profile"
        btnView.setTextColor(Color.WHITE)
        btnView.backgroundTintList = android.content.res.ColorStateList.valueOf(
            Color.parseColor("#0D47A1")
        )
        val btnParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        btnParams.topMargin = 12
        btnView.layoutParams = btnParams

        btnView.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("docId", docId)
            intent.putExtra("guru_name", name)
            intent.putExtra("guru_subject", subject)
            intent.putExtra("guru_time", time)
            intent.putExtra("guru_location", location)
            startActivity(intent)
        }

        inner.addView(tvName)
        inner.addView(tvSubject)
        inner.addView(tvTime)
        inner.addView(tvLocation)
        inner.addView(btnView)
        card.addView(inner)
        container.addView(card)
    }
}
