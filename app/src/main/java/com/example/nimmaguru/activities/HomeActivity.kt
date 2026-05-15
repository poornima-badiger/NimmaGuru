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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var searchGuru: EditText

    // CHIP VARIABLES
    private lateinit var chipGroup: ChipGroup
    private lateinit var chipMath: Chip
    private lateinit var chipScience: Chip
    private lateinit var chipEnglish: Chip
    private lateinit var chipComputer: Chip
    private lateinit var chipAll: Chip

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // INIT UI
        container = findViewById(R.id.layoutGuruContainer)
        searchGuru = findViewById(R.id.etSearchGuru)

        // INIT CHIPS
        chipGroup = findViewById(R.id.chipGroup)
        chipMath = findViewById(R.id.chipMath)
        chipScience = findViewById(R.id.chipScience)
        chipEnglish = findViewById(R.id.chipEnglish)
        chipComputer = findViewById(R.id.chipComputer)
        chipAll = findViewById(R.id.chipAll)

        // NAVIGATION BUTTONS
        val btnSearch = findViewById<Button>(R.id.btnNavSearch)
        val btnCalendar = findViewById<Button>(R.id.btnNavCalendar)
        val btnAppreciation = findViewById<Button>(R.id.btnNavAppreciation)
        val btnAI = findViewById<Button>(R.id.btnNavAI)
        val btnSettings = findViewById<Button>(R.id.btnNavSettings)
        val btnNotifications = findViewById<Button>(R.id.btnNavNotifications)

        btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        btnCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
        btnAppreciation.setOnClickListener {
            startActivity(Intent(this, AppreciationActivity::class.java))
        }
        btnAI.setOnClickListener {
            startActivity(Intent(this, AiAssistantActivity::class.java))
        }
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        btnNotifications.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        // LOAD ALL DATA FIRST
        loadGurus("")

        // SEARCH FUNCTION
        searchGuru.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadGurus(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // CHIP FILTERS
        chipAll.setOnClickListener { loadGurus("") }
        chipMath.setOnClickListener { loadGurus("Math") }
        chipScience.setOnClickListener { loadGurus("Science") }
        chipEnglish.setOnClickListener { loadGurus("English") }
        chipComputer.setOnClickListener { loadGurus("Computer") }
    }

    // LOAD DATA FUNCTION
    private fun loadGurus(query: String) {
        db.collection("gurus")
            .get()
            .addOnSuccessListener { result ->
                container.removeAllViews()

                var imageIndex = 1

                for (doc in result) {
                    val docId = doc.id
                    val name = doc.getString("name") ?: ""
                    val subject = doc.getString("subject") ?: ""
                    val time = doc.getString("time") ?: ""
                    val location = doc.getString("location") ?: ""

                    // FILTER LOGIC (SEARCH + CHIP)
                    if (
                        query.isEmpty() ||
                        name.lowercase().contains(query.lowercase()) ||
                        subject.equals(query, ignoreCase = true)
                    ) {
                        // CARD
                        val card = CardView(this)
                        val cardParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        cardParams.setMargins(0, 25, 0, 0)
                        card.layoutParams = cardParams
                        card.radius = 30f
                        card.cardElevation = 12f
                        card.setContentPadding(25, 25, 25, 25)

                        val mainLayout = LinearLayout(this)
                        mainLayout.orientation = LinearLayout.HORIZONTAL

                        // IMAGE
                        val imageView = ImageView(this)
                        val imageId = when (imageIndex) {
                            1 -> R.drawable.guru1
                            2 -> R.drawable.guru2
                            3 -> R.drawable.guru3
                            4 -> R.drawable.guru4
                            5 -> R.drawable.guru5
                            else -> R.drawable.guru1
                        }
                        imageView.setImageResource(imageId)
                        imageView.layoutParams = LinearLayout.LayoutParams(250, 250)
                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

                        imageIndex++
                        if (imageIndex > 5) imageIndex = 1

                        // RIGHT SIDE
                        val rightLayout = LinearLayout(this)
                        rightLayout.orientation = LinearLayout.VERTICAL
                        val rightParams = LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                        )
                        rightParams.setMargins(20, 0, 0, 0)
                        rightLayout.layoutParams = rightParams

                        val tvName = TextView(this)
                        tvName.text = name
                        tvName.textSize = 20f
                        tvName.setTypeface(null, android.graphics.Typeface.BOLD)
                        tvName.setTextColor(Color.parseColor("#0D47A1"))

                        val tvSubject = TextView(this)
                        tvSubject.text = "📘 $subject"
                        tvSubject.textSize = 15f
                        tvSubject.setTextColor(Color.parseColor("#444444"))

                        val tvTime = TextView(this)
                        tvTime.text = "⏰ $time"
                        tvTime.textSize = 14f
                        tvTime.setTextColor(Color.parseColor("#666666"))

                        val tvLocation = TextView(this)
                        tvLocation.text = "📍 $location"
                        tvLocation.textSize = 13f
                        tvLocation.setTextColor(Color.parseColor("#888888"))

                        val btnProfile = Button(this)
                        btnProfile.text = "View Profile"
                        btnProfile.setTextColor(Color.WHITE)
                        btnProfile.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(
                                Color.parseColor("#0D47A1")
                            )
                        val btnParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        btnParams.topMargin = 10
                        btnProfile.layoutParams = btnParams

                        btnProfile.setOnClickListener {
                            val intent = Intent(this, ProfileActivity::class.java)
                            intent.putExtra("guru_name", name)
                            intent.putExtra("guru_subject", subject)
                            intent.putExtra("guru_time", time)
                            intent.putExtra("guru_location", location)
                            intent.putExtra("docId", docId)
                            startActivity(intent)
                        }

                        rightLayout.addView(tvName)
                        rightLayout.addView(tvSubject)
                        rightLayout.addView(tvTime)
                        rightLayout.addView(tvLocation)
                        rightLayout.addView(btnProfile)

                        mainLayout.addView(imageView)
                        mainLayout.addView(rightLayout)
                        card.addView(mainLayout)
                        container.addView(card)
                    }
                }
            }
    }
}
