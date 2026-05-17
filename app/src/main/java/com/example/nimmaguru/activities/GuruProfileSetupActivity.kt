package com.example.nimmaguru.activities

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.nimmaguru.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class GuruProfileSetupActivity : BaseActivity() {

    private val db      = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                findViewById<ImageView>(R.id.imgGuruPhoto).setImageURI(uri)
                findViewById<TextView>(R.id.tvPhotoStatus).text =
                    "✅ Photo selected — will be uploaded on save"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guru_profile_setup)

        val imgGuruPhoto     = findViewById<ImageView>(R.id.imgGuruPhoto)
        val btnPickPhoto     = findViewById<TextView>(R.id.btnPickPhoto)
        val edtName          = findViewById<EditText>(R.id.edtName)
        val edtAge           = findViewById<EditText>(R.id.edtAge)
        val edtProfession    = findViewById<EditText>(R.id.edtProfession)
        val edtExperience    = findViewById<EditText>(R.id.edtExperience)
        val edtSubject       = findViewById<EditText>(R.id.edtSubject)
        val edtLanguages     = findViewById<EditText>(R.id.edtLanguages)
        val edtAvailableDays = findViewById<EditText>(R.id.edtAvailableDays)
        val edtTime          = findViewById<EditText>(R.id.edtTime)
        val edtTeachingPlace = findViewById<EditText>(R.id.edtTeachingPlace)
        val edtLocation      = findViewById<EditText>(R.id.edtLocation)
        val edtPhone         = findViewById<EditText>(R.id.edtPhone)
        val edtAbout         = findViewById<EditText>(R.id.edtAbout)
        val btnSave          = findViewById<Button>(R.id.btnSaveGuru)
        val btnGoHome        = findViewById<Button>(R.id.btnGoHome)

        // Photo picker
        imgGuruPhoto.setOnClickListener { pickImageLauncher.launch("image/*") }
        btnPickPhoto.setOnClickListener { pickImageLauncher.launch("image/*") }

        btnGoHome.setOnClickListener {
            startActivity(android.content.Intent(this, HomeActivity::class.java))
            finish()
        }

        btnSave.setOnClickListener {
            val name          = edtName.text.toString().trim()
            val age           = edtAge.text.toString().trim()
            val profession    = edtProfession.text.toString().trim()
            val experience    = edtExperience.text.toString().trim()
            val subject       = edtSubject.text.toString().trim()
            val languages     = edtLanguages.text.toString().trim()
            val availableDays = edtAvailableDays.text.toString().trim()
            val time          = edtTime.text.toString().trim()
            val teachingPlace = edtTeachingPlace.text.toString().trim()
            val location      = edtLocation.text.toString().trim()
            val phone         = edtPhone.text.toString().trim()
            val about         = edtAbout.text.toString().trim()

            // Validation
            if (name.isEmpty())          { edtName.error          = "Required"; return@setOnClickListener }
            if (age.isEmpty())           { edtAge.error           = "Required"; return@setOnClickListener }
            if (profession.isEmpty())    { edtProfession.error    = "Required"; return@setOnClickListener }
            if (subject.isEmpty())       { edtSubject.error       = "Required"; return@setOnClickListener }
            if (availableDays.isEmpty()) { edtAvailableDays.error = "Required"; return@setOnClickListener }
            if (time.isEmpty())          { edtTime.error          = "Required"; return@setOnClickListener }
            if (location.isEmpty())      { edtLocation.error      = "Required"; return@setOnClickListener }

            btnSave.isEnabled = false
            btnSave.text = "Saving..."

            if (selectedImageUri != null) {
                uploadPhotoAndSave(
                    selectedImageUri!!, name, age, profession, experience,
                    subject, languages, availableDays, time, teachingPlace,
                    location, phone, about, btnSave
                )
            } else {
                val avatarUrl = "https://ui-avatars.com/api/?name=${name.replace(" ", "+")}&background=F4B400&color=fff&size=200"
                saveToFirestore(
                    name, age, profession, experience, subject, languages,
                    availableDays, time, teachingPlace, location, phone, about,
                    avatarUrl, btnSave
                )
            }
        }
    }

    private fun uploadPhotoAndSave(
        uri: Uri, name: String, age: String, profession: String,
        experience: String, subject: String, languages: String,
        availableDays: String, time: String, teachingPlace: String,
        location: String, phone: String, about: String, btnSave: Button
    ) {
        btnSave.text = "Uploading photo..."
        val ref = storage.reference.child("guru_photos/${System.currentTimeMillis()}.jpg")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->
                    saveToFirestore(
                        name, age, profession, experience, subject, languages,
                        availableDays, time, teachingPlace, location, phone, about,
                        url.toString(), btnSave
                    )
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Photo upload failed, saving without photo.", Toast.LENGTH_SHORT).show()
                val avatarUrl = "https://ui-avatars.com/api/?name=${name.replace(" ", "+")}&background=F4B400&color=fff&size=200"
                saveToFirestore(
                    name, age, profession, experience, subject, languages,
                    availableDays, time, teachingPlace, location, phone, about,
                    avatarUrl, btnSave
                )
            }
    }

    private fun saveToFirestore(
        name: String, age: String, profession: String, experience: String,
        subject: String, languages: String, availableDays: String, time: String,
        teachingPlace: String, location: String, phone: String, about: String,
        imageUrl: String, btnSave: Button
    ) {
        btnSave.text = "Saving profile..."
        val guru = hashMapOf(
            "name"          to name,
            "age"           to age,
            "profession"    to profession,
            "experience"    to experience,
            "subject"       to subject,
            "languages"     to languages,
            "availableDays" to availableDays,
            "time"          to time,
            "teachingPlace" to teachingPlace,
            "location"      to location,
            "phone"         to phone,
            "about"         to about,
            "image"         to imageUrl
        )
        db.collection("gurus").add(guru)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile saved! Welcome, $name 🎉", Toast.LENGTH_LONG).show()
                startActivity(android.content.Intent(this, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                btnSave.isEnabled = true
                btnSave.text = "✅  Save Guru Profile"
                Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
