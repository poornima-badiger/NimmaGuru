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

class HomeActivity : BaseActivity() {

    private lateinit var container: LinearLayout
    private lateinit var searchGuru: EditText
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        container  = findViewById(R.id.layoutGuruContainer)
        searchGuru = findViewById(R.id.etSearchGuru)

        // ── NAV ──────────────────────────────────────────────────────
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
        findViewById<TextView>(R.id.btnNavStudentProfile).setOnClickListener {
            startActivity(Intent(this, StudentProfileActivity::class.java)) }

        // ── SUBJECT FILTERS ──────────────────────────────────────────
        findViewById<TextView>(R.id.chipAll).setOnClickListener       { loadGurus("") }
        findViewById<TextView>(R.id.chipMath).setOnClickListener      { loadGurus("Math") }
        findViewById<TextView>(R.id.chipScience).setOnClickListener   { loadGurus("Science") }
        findViewById<TextView>(R.id.chipEnglish).setOnClickListener   { loadGurus("English") }
        findViewById<TextView>(R.id.chipComputer).setOnClickListener  { loadGurus("Computer") }
        findViewById<TextView>(R.id.chipCarpentry).setOnClickListener { loadGurus("Carpentry") }

        // ── SEARCH ───────────────────────────────────────────────────
        searchGuru.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadGurus(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        checkAndLoadGurus()
    }

    private fun checkAndLoadGurus() {
        db.collection("gurus").get()
            .addOnSuccessListener { if (it.isEmpty) addSampleGurus() else loadGurus("") }
            .addOnFailureListener { showHardcodedGurus() }
    }

    private fun addSampleGurus() {
        val list = listOf(
            hashMapOf("name" to "Vishwanath Sir",  "subject" to "Math",      "time" to "Sunday 10AM-1PM",   "location" to "Gadag",    "experience" to "20 years", "phone" to "9876543210"),
            hashMapOf("name" to "Savitha Madam",   "subject" to "Science",   "time" to "Saturday 9AM-12PM", "location" to "Hubli",    "experience" to "15 years", "phone" to "9845012345"),
            hashMapOf("name" to "Raju Sir",        "subject" to "English",   "time" to "Sunday 2PM-5PM",    "location" to "Dharwad",  "experience" to "10 years", "phone" to "9731234567"),
            hashMapOf("name" to "Anand Sir",       "subject" to "Computer",  "time" to "Saturday 3PM-6PM",  "location" to "Hulkoti",  "experience" to "12 years", "phone" to "9900112233"),
            hashMapOf("name" to "Meena Madam",     "subject" to "Math",      "time" to "Sunday 8AM-11AM",   "location" to "Gadag",    "experience" to "18 years", "phone" to "9611223344"),
            hashMapOf("name" to "Prakash Sir",     "subject" to "Science",   "time" to "Saturday 10AM-1PM", "location" to "Koppal",   "experience" to "8 years",  "phone" to "9455667788"),
            hashMapOf("name" to "Ramesh Sir",      "subject" to "Carpentry", "time" to "Sunday 4PM-6PM",    "location" to "Gadag",    "experience" to "25 years", "phone" to "9344556677")
        )
        var done = 0
        for (guru in list) {
            db.collection("gurus").add(guru)
                .addOnSuccessListener { if (++done == list.size) loadGurus("") }
        }
    }

    private fun loadGurus(query: String) {
        db.collection("gurus").get()
            .addOnSuccessListener { result ->
                container.removeAllViews()
                var shown = 0; var imgIdx = 1
                for (doc in result) {
                    val docId      = doc.id
                    val name       = doc.getString("name")       ?: ""
                    val subject    = doc.getString("subject")    ?: ""
                    val time       = doc.getString("time")       ?: ""
                    val location   = doc.getString("location")   ?: ""
                    val experience = doc.getString("experience") ?: ""

                    val match = query.isEmpty()
                            || name.lowercase().contains(query.lowercase())
                            || subject.equals(query, ignoreCase = true)
                            || location.lowercase().contains(query.lowercase())

                    if (match) {
                        container.addView(buildCard(
                            docId, name, subject, time, location, experience,
                            doc.getString("age")           ?: "",
                            doc.getString("phone")         ?: "",
                            doc.getString("image")         ?: "",
                            doc.getString("profession")    ?: "",
                            doc.getString("languages")     ?: "",
                            doc.getString("availableDays") ?: "",
                            doc.getString("teachingPlace") ?: "",
                            doc.getString("about")         ?: "",
                            imgIdx
                        ))
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

    private fun buildCard(
        docId: String, name: String, subject: String,
        time: String, location: String, experience: String,
        age: String, phone: String, imageUrl: String,
        profession: String, languages: String, availableDays: String,
        teachingPlace: String, about: String, imgIdx: Int
    ): CardView {
        val card = CardView(this)
        val cp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cp.setMargins(0, 0, 0, 16)
        card.layoutParams = cp
        card.radius = 20f
        card.cardElevation = 6f
        card.setCardBackgroundColor(Color.WHITE)

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.setPadding(16, 16, 16, 16)

        // Image
        val img = ImageView(this)
        img.setImageResource(when (imgIdx) {
            1 -> R.drawable.guru1; 2 -> R.drawable.guru2
            3 -> R.drawable.guru3; 4 -> R.drawable.guru4
            else -> R.drawable.guru5
        })
        val ip = LinearLayout.LayoutParams(180, 180); ip.setMargins(0, 0, 14, 0)
        img.layoutParams = ip; img.scaleType = ImageView.ScaleType.CENTER_CROP

        // Text column
        val col = LinearLayout(this)
        col.orientation = LinearLayout.VERTICAL
        col.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        fun tv(txt: String, size: Float, color: String, bold: Boolean = false) = TextView(this).also {
            it.text = txt; it.textSize = size
            it.setTextColor(Color.parseColor(color))
            if (bold) it.setTypeface(null, android.graphics.Typeface.BOLD)
            it.setPadding(0, 2, 0, 2)
        }

        val btn = Button(this)
        btn.text = "View Profile"
        btn.setTextColor(Color.WHITE)
        btn.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#0D47A1"))
        val bp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        bp.topMargin = 8; btn.layoutParams = bp
        btn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).apply {
                putExtra("guru_name",       name)
                putExtra("guru_subject",    subject)
                putExtra("guru_time",       time)
                putExtra("guru_location",   location)
                putExtra("guru_experience", experience)
                putExtra("docId",           docId)
                putExtra("guru_age",        age)
                putExtra("guru_phone",      phone)
                putExtra("guru_image",      imageUrl)
                putExtra("guru_profession", profession)
                putExtra("guru_languages",  languages)
                putExtra("guru_days",       availableDays)
                putExtra("guru_place",      teachingPlace)
                putExtra("guru_about",      about)
            })
        }

        col.addView(tv(name, 17f, "#0D47A1", true))
        col.addView(tv("📘 $subject", 14f, "#444444"))
        if (experience.isNotEmpty()) col.addView(tv("🏅 $experience", 13f, "#2E7D32"))
        col.addView(tv("⏰ $time", 13f, "#666666"))
        col.addView(tv("📍 $location", 13f, "#888888"))
        col.addView(btn)

        row.addView(img); row.addView(col)
        card.addView(row)
        return card
    }

    private fun showHardcodedGurus() {
        container.removeAllViews()
        listOf(
            arrayOf("1","Vishwanath Sir","Math",    "20 years","Sunday 10AM-1PM",  "Gadag",   "58","9876543210","","Retired Math Teacher","Kannada, English","Sunday","Samudaya Bhavana","Helping students with Math free of cost."),
            arrayOf("2","Savitha Madam", "Science", "15 years","Saturday 9AM-12PM","Hubli",   "52","9845012345","","Retired Science Teacher","Kannada","Saturday","Community Hall","Teaching Science to village students."),
            arrayOf("3","Raju Sir",      "English", "10 years","Sunday 2PM-5PM",   "Dharwad", "60","9731234567","","Retired English Teacher","Kannada, English","Sunday","School Ground","Improving English skills of students."),
            arrayOf("4","Anand Sir",     "Computer","12 years","Saturday 3PM-6PM", "Hulkoti", "55","9900112233","","Retired Engineer","Kannada, English","Saturday","Samudaya Bhavana","Teaching Computer Basics to all ages.")
        ).forEachIndexed { i, g ->
            container.addView(buildCard(g[0],g[1],g[2],g[3],g[4],g[5],g[6],g[7],g[8],g[9],g[10],g[11],g[12],g[13],i+1))
        }
    }
}
