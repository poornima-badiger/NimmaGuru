package com.example.nimmaguru.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R
import java.net.URL
import java.util.concurrent.Executors

class ProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // ── Read all intent extras ────────────────────────────────────
        val docId         = intent.getStringExtra("docId")            ?: ""
        val guruName      = intent.getStringExtra("guru_name")        ?: ""
        val guruSubject   = intent.getStringExtra("guru_subject")     ?: ""
        val guruProfession= intent.getStringExtra("guru_profession")  ?: ""
        val guruAge       = intent.getStringExtra("guru_age")         ?: ""
        val guruExp       = intent.getStringExtra("guru_experience")  ?: ""
        val guruLanguages = intent.getStringExtra("guru_languages")   ?: ""
        val guruDays      = intent.getStringExtra("guru_days")        ?: ""
        val guruTime      = intent.getStringExtra("guru_time")        ?: ""
        val guruPlace     = intent.getStringExtra("guru_place")       ?: ""
        val guruLoc       = intent.getStringExtra("guru_location")    ?: ""
        val guruPhone     = intent.getStringExtra("guru_phone")       ?: ""
        val guruAbout     = intent.getStringExtra("guru_about")       ?: ""
        val guruImage     = intent.getStringExtra("guru_image")       ?: ""

        // ── Hero section ──────────────────────────────────────────────
        val imgGuru        = findViewById<ImageView>(R.id.imgGuru)
        val tvName         = findViewById<TextView>(R.id.tvGuruName)
        val tvProfession   = findViewById<TextView>(R.id.tvGuruProfession)
        val tvSubject      = findViewById<TextView>(R.id.tvGuruSubject)
        val tvAbout        = findViewById<TextView>(R.id.tvGuruDescription)

        tvName.text       = guruName
        tvProfession.text = if (guruProfession.isNotEmpty()) guruProfession else "Mentor"
        tvSubject.text    = guruSubject
        tvAbout.text      = if (guruAbout.isNotEmpty()) guruAbout
                            else "Experienced mentor helping village students learn $guruSubject effectively."

        // Load profile photo
        if (guruImage.isNotEmpty()) loadImageFromUrl(guruImage, imgGuru)
        else imgGuru.setImageResource(R.drawable.guru1)

        // ── Detail rows ───────────────────────────────────────────────
        setRow(R.id.rowAge,           "🎂", "Age",             if (guruAge.isNotEmpty()) "$guruAge years old" else "—")
        setRow(R.id.rowExperience,    "🏅", "Experience",      if (guruExp.isNotEmpty()) guruExp else "—")
        setRow(R.id.rowLanguages,     "🗣️", "Languages",       if (guruLanguages.isNotEmpty()) guruLanguages else "—")
        setRow(R.id.rowAvailableDays, "📆", "Available Days",  if (guruDays.isNotEmpty()) guruDays else "—")
        setRow(R.id.rowTime,          "⏰", "Available Time",  if (guruTime.isNotEmpty()) guruTime else "—")
        setRow(R.id.rowTeachingPlace, "🏫", "Teaching Place",  if (guruPlace.isNotEmpty()) guruPlace else "—")
        setRow(R.id.rowLocation,      "📍", "Village / Area",  if (guruLoc.isNotEmpty()) guruLoc else "—")
        setRow(R.id.rowPhone,         "📞", "Contact Number",  if (guruPhone.isNotEmpty()) guruPhone else "—")

        // ── Buttons ───────────────────────────────────────────────────
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnBookSession).setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java).apply {
                putExtra("guru_name", guruName)
            })
        }

        findViewById<Button>(R.id.btnThankYou).setOnClickListener {
            startActivity(Intent(this, AppreciationActivity::class.java).apply {
                putExtra("guru_name_prefill", guruName)
            })
        }

        findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java).apply {
                putExtra("docId", docId)
                putExtra("name", guruName)
                putExtra("subject", guruSubject)
                putExtra("time", guruTime)
                putExtra("location", guruLoc)
            })
        }
    }

    // Helper: set icon, label, value on a row view
    private fun setRow(rowId: Int, icon: String, label: String, value: String) {
        val row = findViewById<View>(rowId)
        row.findViewById<TextView>(R.id.tvRowIcon).text  = icon
        row.findViewById<TextView>(R.id.tvRowLabel).text = label
        row.findViewById<TextView>(R.id.tvRowValue).text = value
    }

    // Load image from URL on background thread
    private fun loadImageFromUrl(url: String, imageView: ImageView) {
        val executor = Executors.newSingleThreadExecutor()
        val handler  = Handler(Looper.getMainLooper())
        executor.execute {
            try {
                val bmp: Bitmap = BitmapFactory.decodeStream(
                    URL(url).openConnection().apply {
                        connectTimeout = 5000; readTimeout = 5000
                    }.getInputStream()
                )
                handler.post { imageView.setImageBitmap(bmp) }
            } catch (e: Exception) {
                handler.post { imageView.setImageResource(R.drawable.guru1) }
            }
        }
    }
}
