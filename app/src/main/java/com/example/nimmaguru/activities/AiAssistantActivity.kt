package com.example.nimmaguru.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.nimmaguru.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AiAssistantActivity : BaseActivity() {

    private lateinit var etQuestion: EditText
    private lateinit var btnAsk: Button
    private lateinit var chatContainer: LinearLayout
    private lateinit var scrollChat: ScrollView

    // Replace with your actual Gemini API key from Google AI Studio
    // Get free key at: https://aistudio.google.com/app/apikey
    private val GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE"
    private val GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$GEMINI_API_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_assistant)

        etQuestion = findViewById(R.id.etQuestion)
        btnAsk = findViewById(R.id.btnAsk)
        chatContainer = findViewById(R.id.layoutChatContainer)
        scrollChat = findViewById(R.id.scrollChat)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        btnAsk.setOnClickListener {
            val question = etQuestion.text.toString().trim()
            if (question.isEmpty()) {
                Toast.makeText(this, "Please type a question", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            etQuestion.text.clear()
            addUserMessage(question)
            askGemini(question)
        }
    }

    private fun addUserMessage(text: String) {
        val card = CardView(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(80, 0, 0, 16)
        params.gravity = Gravity.END
        card.layoutParams = params
        card.radius = 16f
        card.cardElevation = 3f
        card.setCardBackgroundColor(Color.parseColor("#0D47A1"))

        val tv = TextView(this)
        tv.text = text
        tv.textSize = 15f
        tv.setTextColor(Color.WHITE)
        tv.setPadding(30, 20, 30, 20)
        card.addView(tv)

        val wrapper = LinearLayout(this)
        wrapper.orientation = LinearLayout.HORIZONTAL
        wrapper.gravity = Gravity.END
        val wParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        wParams.setMargins(0, 0, 0, 8)
        wrapper.layoutParams = wParams
        wrapper.addView(card)

        chatContainer.addView(wrapper)
        scrollToBottom()
    }

    private fun addAiMessage(text: String) {
        runOnUiThread {
            val card = CardView(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 80, 16)
            card.layoutParams = params
            card.radius = 16f
            card.cardElevation = 3f
            card.setCardBackgroundColor(Color.parseColor("#E3F2FD"))

            val inner = LinearLayout(this)
            inner.orientation = LinearLayout.VERTICAL
            inner.setPadding(30, 20, 30, 20)

            val tvLabel = TextView(this)
            tvLabel.text = "🤖 AI Assistant"
            tvLabel.textSize = 12f
            tvLabel.setTypeface(null, android.graphics.Typeface.BOLD)
            tvLabel.setTextColor(Color.parseColor("#0D47A1"))

            val tvMsg = TextView(this)
            tvMsg.text = text
            tvMsg.textSize = 15f
            tvMsg.setTextColor(Color.parseColor("#222222"))
            tvMsg.setPadding(0, 8, 0, 0)

            inner.addView(tvLabel)
            inner.addView(tvMsg)
            card.addView(inner)
            chatContainer.addView(card)
            scrollToBottom()
        }
    }

    private fun addLoadingMessage(): View {
        val tv = TextView(this)
        tv.text = "🤖 Thinking..."
        tv.textSize = 14f
        tv.setTextColor(Color.parseColor("#888888"))
        tv.setPadding(10, 10, 10, 10)
        chatContainer.addView(tv)
        scrollToBottom()
        return tv
    }

    private fun askGemini(question: String) {
        btnAsk.isEnabled = false
        val loadingView = addLoadingMessage()

        // Build the prompt with educational context
        val prompt = "You are a helpful study assistant for school students in Karnataka, India. " +
                "Answer the following question simply and clearly in 3-5 sentences. " +
                "If the question is in Kannada, answer in Kannada. Otherwise answer in English.\n\n" +
                "Question: $question"

        val requestBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(GEMINI_URL)
            .post(
                requestBody.toString()
                    .toRequestBody("application/json".toMediaType())
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    chatContainer.removeView(loadingView)
                    addAiMessage("Sorry, I could not connect. Please check your internet connection and try again.")
                    btnAsk.isEnabled = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    chatContainer.removeView(loadingView)
                    btnAsk.isEnabled = true
                }

                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    try {
                        val json = JSONObject(body)
                        val answer = json
                            .getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
                        addAiMessage(answer)
                    } catch (e: Exception) {
                        addAiMessage("Sorry, I could not understand the response. Please try again.")
                    }
                } else {
                    if (GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE") {
                        addAiMessage(
                            "⚠️ API key not set yet!\n\n" +
                                    "To enable AI answers:\n" +
                                    "1. Go to https://aistudio.google.com/app/apikey\n" +
                                    "2. Create a free API key\n" +
                                    "3. Replace YOUR_GEMINI_API_KEY_HERE in AiAssistantActivity.kt"
                        )
                    } else {
                        addAiMessage("Sorry, something went wrong. Please try again later.")
                    }
                }
            }
        })
    }

    private fun scrollToBottom() {
        scrollChat.post {
            scrollChat.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
}
