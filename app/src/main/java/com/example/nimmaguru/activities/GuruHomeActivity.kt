package com.example.nimmaguru.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R
import com.google.firebase.firestore.FirebaseFirestore

class GuruHomeActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var searchBox: EditText
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guru_home)

        container = findViewById(R.id.layoutGuruContainer)
        searchBox = findViewById(R.id.etSearch)

        // Load all data initially
        loadAllGurus()

        // Search listener
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isEmpty()) {
                    loadAllGurus()
                } else {
                    searchGuru(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // 🔥 LOAD ALL GURUS
    private fun loadAllGurus() {

        db.collection("gurus")
            .get()
            .addOnSuccessListener { result ->

                container.removeAllViews()

                for (doc in result) {

                    val name = doc.getString("name") ?: ""
                    val subject = doc.getString("subject") ?: ""
                    val time = doc.getString("time") ?: ""

                    addGuruToUI(name, subject, time)
                }
            }
    }

    // 🔍 SEARCH FUNCTION
    private fun searchGuru(query: String) {

        db.collection("gurus")
            .get()
            .addOnSuccessListener { result ->

                container.removeAllViews()

                for (doc in result) {

                    val name = doc.getString("name") ?: ""
                    val subject = doc.getString("subject") ?: ""
                    val time = doc.getString("time") ?: ""

                    if (name.lowercase().contains(query.lowercase()) ||
                        subject.lowercase().contains(query.lowercase())
                    ) {
                        addGuruToUI(name, subject, time)
                    }
                }
            }
    }

    // 🧾 COMMON UI FUNCTION
    private fun addGuruToUI(name: String, subject: String, time: String) {

        val textView = TextView(this)

        textView.text = "👨‍🏫 $name\n📘 $subject\n⏰ $time"
        textView.textSize = 18f
        textView.setPadding(20, 20, 20, 20)

        container.addView(textView)
    }
}