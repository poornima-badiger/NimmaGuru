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

class HomeActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var searchGuru: EditText
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        container  = findViewById(R.id.layoutGuruContainer)
        searchGuru = findViewById(R.id.etSearchGuru)

        // ── NAV ──────────────────────────────────────────────────────────────
        findViewById<TextView>(R.id.btnNavSearch).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java)) }
        findViewById<TextView>(R.id.btnNavCalendar).setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java)) }
        findViewById<TextView>(R.id.btnNavAppreciation).setOnClickListener {
            startActivity(Intent(this, AppreciationActivity::class.java)) }
        findViewById<TextView>(R.id.btnNavAI).setOnClickListener {
            startActivity(Intent(this, AiAssistantActivity::class.java)) }
        findViewById<TextView>(R.id.btnNavNotifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java)) }
        findViewById<TextView>(R.id.btnNavSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)) }

        // ── SUBJECT FILTER CHIPS ─────────────────────────────────────────────
        findViewById<TextView>(R.id.chipAll).setOnClickListener      { loadGurus("") }
        findViewById<TextView>(R.id.chipMath).setOnClickListener     { loadGurus("Math") }
        findViewById<TextView>(R.id.chipScience).setOnClickListener  { loadGurus("Science") }
        findViewById<TextView>(R.id.chipEnglish).setOnClickListener  { loadGurus("English") }
        findViewById<TextView>(R.id.chipComputer).setOnClickListener { loadGurus("Computer") }

        // ── SEARCH ───────────────────────────────────────────────────────────
        searchGuru.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadGurus(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // ── LOAD DATA ────────────────────────────────────────────────────────
        checkAndLoadGurus()
    }

    // If Firestore is empty → add sample data first
    private fun checkAndLoadGurus() {
        db.collection("gurus").get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) addSampleGurus() else loadGurus("")
            }
            .addOnFailureListener { showHardcodedGurus() }
    }

    // Add 6 sample gurus to Firestore
    private fun addSampleGurus() {
        val list = listOf(
            hashMapOf("name" to "Vishwanath Sir",  "subject" to "Math",     "time" to "Sunday 10AM-1PM",   "location" to "Gadag"),
            hashMapOf("name" to "Savitha Madam",   "subject" to "Science",  "time" to "Saturday 9AM-12PM", "location" to "Hubli"),
            hashMapOf("name" to "Raju Sir",        "subject" to "English",  "time" to "Sunday 2PM-5PM",    "location" to "Dharwad"),
            hashMapOf("name" to "Anand Sir",       "subject" to "Computer", "time" to "Saturday 3PM-6PM",  "location" to "Hulkoti"),
            hashMapOf("name" to "Meena Madam",     "subject" to "Math",     "time" to "Sunday 8AM-11AM",   "location" to "Gadag"),
            hashMapOf("name" to "Prakash Sir",     "subject" to "Science",  "time" to "Saturday 10AM-1PM", "location" to "Koppal")
        )
        var done = 0
        for (guru in list) {
            db.collection("gurus").add(guru)
                .addOnSuccessListener { if (++done == list.size) loadGurus("") }
        }
    }

    // Load gurus from Firestore with optional filter
    private fun loadGurus(query: String) {
        db.collection("gurus").get()
            .addOnSuccessListener { result ->
                container.removeAllViews()
                var shown = 0
                var imgIdx = 1
                for (doc in result) {
                    val docId    = doc.id
                    val name     = doc.getString("name")     ?: ""
                    val subject  = doc.getString("subject")  ?: ""
                    val time     = doc.getString("time")     ?: ""
                    val location = doc.getString("location") ?: ""

                    val match = query.isEmpty()
                            || name.lowercase().contains(query.lowercase())
                            || subject.equals(query, ignoreCase = true)

                    if (match) {
                        container.addView(buildCard(docId, name, subject, time, location, imgIdx))
                        imgIdx = if (imgIdx >= 5) 1 else imgIdx + 1
                        shown++
                    }
                }
                if (shown == 0) {
                    val tv = TextView(this)
                    tv.text = if (query.isEmpty()) "No gurus yet." else "No gurus found for \"$query\""
                    tv.textSize = 15f
                    tv.setTextColor(Color.parseColor("#999999"))
                    tv.gravity = android.view.Gravity.CENTER
                    tv.setPadding(20, 60, 20, 20)
                    container.addView(tv)
                }
            }
            .addOnFailureListener { showHardcodedGurus() }
    }

    // Build one guru card
    private fun buildCard(
        docId: String, name: String, subject: String,
        time: String, location: String, imgIdx: Int
    ): CardView {
        val card = CardView(this)
        val cp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        cp.setMargins(0, 0, 0, 20)
        card.layoutParams = cp
        card.radius = 20f
        card.cardElevation = 6f
        card.setCardBackgroundColor(Color.WHITE)

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.setPadding(16, 16, 16, 16)

        // Image
        val img = ImageView(this)
        val imgRes = when (imgIdx) {
            1 -> R.drawable.guru1; 2 -> R.drawable.guru2
            3 -> R.drawable.guru3; 4 -> R.drawable.guru4
            else -> R.drawable.guru5
        }
        img.setImageResource(imgRes)
        val ip = LinearLayout.LayoutParams(190, 190)
        ip.setMargins(0, 0, 14, 0)
        img.layoutParams = ip
        img.scaleType = ImageView.ScaleType.CENTER_CROP

        // Text column
        val col = LinearLayout(this)
        col.orientation = LinearLayout.VERTICAL
        col.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        fun tv(txt: String, size: Float, color: String, bold: Boolean = false) = TextView(this).also {
            it.text = txt; it.textSize = size
            it.setTextColor(Color.parseColor(color))
            if (bold) it.setTypeface(null, android.graphics.Typeface.BOLD)
            it.setPadding(0, 3, 0, 3)
        }

        val btn = Button(this)
        btn.text = "View Profile"
        btn.setTextColor(Color.WHITE)
        btn.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#0D47A1"))
        val bp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        bp.topMargin = 8
        btn.layoutParams = bp
        btn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).apply {
                putExtra("guru_name", name); putExtra("guru_subject", subject)
                putExtra("guru_time", time); putExtra("guru_location", location)
                putExtra("docId", docId)
            })
        }

        col.addView(tv(name,     17f, "#0D47A1", true))
        col.addView(tv("📘 $subject", 14f, "#444444"))
        col.addView(tv("⏰ $time",    13f, "#666666"))
        col.addView(tv("📍 $location",13f, "#888888"))
        col.addView(btn)

        row.addView(img)
        row.addView(col)
        card.addView(row)
        return card
    }

    // Fallback when Firestore is unreachable
    private fun showHardcodedGurus() {
        container.removeAllViews()
        listOf(
            arrayOf("1","Vishwanath Sir","Math",    "Sunday 10AM-1PM",  "Gadag"),
            arrayOf("2","Savitha Madam", "Science", "Saturday 9AM-12PM","Hubli"),
            arrayOf("3","Raju Sir",      "English", "Sunday 2PM-5PM",   "Dharwad"),
            arrayOf("4","Anand Sir",     "Computer","Saturday 3PM-6PM", "Hulkoti")
        ).forEachIndexed { i, g ->
            container.addView(buildCard(g[0], g[1], g[2], g[3], g[4], i + 1))
        }
    }
}
