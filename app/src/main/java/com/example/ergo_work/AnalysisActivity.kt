package com.example.ergo_work

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView

import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import android.view.View

class AnalysisActivity : AppCompatActivity() {

    private var selectedPainArea: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        // Load Profile Image safely to Dashboard Top Bar
        val btnProfile = findViewById<ShapeableImageView>(R.id.btnProfile)
        val prefs = getSharedPreferences("ergo_care_prefs", MODE_PRIVATE)
        val savedImageUriString = prefs.getString("profile_image_uri", null)
        if (savedImageUriString != null) {
            try {
                val savedImageUri = android.net.Uri.parse(savedImageUriString)
                btnProfile.setImageURI(savedImageUri)
            } catch (e: Exception) {
                // Ignore if permission denied
            }
        }

        // --- Pain Journal Logic ---
        val btnPainNeck = findViewById<View>(R.id.btnPainNeck)
        val btnPainLeftShoulder = findViewById<View>(R.id.btnPainLeftShoulder)
        val btnPainRightShoulder = findViewById<View>(R.id.btnPainRightShoulder)
        val btnPainLowerBack = findViewById<View>(R.id.btnPainLowerBack)
        
        val tvSelectedArea = findViewById<TextView>(R.id.tvSelectedArea)
        val sliderPain = findViewById<Slider>(R.id.sliderPain)
        val tvPainLevel = findViewById<TextView>(R.id.tvPainLevel)
        val btnSavePainJournal = findViewById<MaterialButton>(R.id.btnSavePainJournal)
        val etPainNotes = findViewById<EditText>(R.id.etPainNotes)

        // Area Selection Logic
        val updateArea = { areaName: String ->
            selectedPainArea = areaName
            tvSelectedArea.text = areaName
            
            // Reset slider when new area is picked
            sliderPain.value = 0f
            tvPainLevel.text = "0 / 10 • Tidak Nyeri"
            tvPainLevel.setTextColor(getColor(R.color.ergo_primary))
        }

        btnPainNeck.setOnClickListener { updateArea("Leher (Cervical)") }
        btnPainLeftShoulder.setOnClickListener { updateArea("Bahu Kiri") }
        btnPainRightShoulder.setOnClickListener { updateArea("Bahu Kanan") }
        btnPainLowerBack.setOnClickListener { updateArea("Punggung Bawah (Lumbar)") }

        // Slider Logic
        sliderPain.addOnChangeListener { slider, value, fromUser ->
            val intValue = value.toInt()
            val textPain = when {
                intValue == 0 -> "Tidak Nyeri"
                intValue in 1..3 -> "Ringan"
                intValue in 4..6 -> "Sedang"
                intValue in 7..8 -> "Berat"
                else -> "Sangat Nyeri"
            }
            tvPainLevel.text = "$intValue / 10 • $textPain"
            
            // Ubah warna teks indikator jika nyeri parah
            if (intValue > 6) {
                tvPainLevel.setTextColor(getColor(R.color.ergo_warning_red))
            } else {
                tvPainLevel.setTextColor(getColor(R.color.ergo_primary))
            }
        }

        btnSavePainJournal.setOnClickListener {
            if (selectedPainArea.isEmpty()) {
                Toast.makeText(this, "Silakan ketuk area yang nyeri pada gambar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val notes = etPainNotes.text.toString()
            val intensity = sliderPain.value.toInt()
            
            // Simulasi kirim data ke API (Nantinya akan jadi AI training data)
            Toast.makeText(this, "Data Nyeri $selectedPainArea ($intensity/10) berhasil disimpan!", Toast.LENGTH_LONG).show()
            
            // Reset Form
            selectedPainArea = ""
            tvSelectedArea.text = "Belum ada"
            sliderPain.value = 0f
            etPainNotes.text.clear()
        }

        // --- Bottom Navigation Logic ---
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_analysis // Set Analysis as active tab

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_analysis -> true
                R.id.nav_monitor -> {
                    startActivity(Intent(this, LiveMonitorActivity::class.java))
                    false
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
