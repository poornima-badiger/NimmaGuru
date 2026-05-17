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

class NearbyGuruActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_guru)

        val search = findViewById<EditText>(R.id.etSearchGuru)

        loadGurus("")

        search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                loadGurus(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadGurus(query: String) {

        val db = FirebaseFirestore.getInstance()

        val container =
            findViewById<LinearLayout>(R.id.layoutGuruContainer)

        db.collection("gurus")
            .get()
            .addOnSuccessListener { result ->

                container.removeAllViews()

                for (doc in result) {

                    val name =
                        doc.getString("name") ?: ""

                    val subject =
                        doc.getString("subject") ?: ""

                    val time =
                        doc.getString("time") ?: ""

                    val location =
                        doc.getString("location") ?: ""

                    if (
                        name.lowercase().contains(query.lowercase()) ||
                        subject.lowercase().contains(query.lowercase())
                    ) {

                        val textView = TextView(this)

                        textView.text =
                            "👨‍🏫 $name\n\n📘 $subject\n⏰ $time\n📍 $location"

                        textView.textSize = 18f

                        textView.setPadding(
                            30,
                            30,
                            30,
                            30
                        )

                        container.addView(textView)
                    }
                }
            }
    }
}