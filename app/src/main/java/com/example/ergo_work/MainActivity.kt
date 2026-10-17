package com.example.ergo_work

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ergo_work.data.ErgoPreferences
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var ergoPreferences: ErgoPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ergoPreferences = ErgoPreferences(this)
        // Check if user is authenticated
        if (ergoPreferences.authToken.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set Dynamic Username (Using Full Name)
        val tvGreetingName = findViewById<android.widget.TextView>(R.id.tvGreetingName)
        val userName = ergoPreferences.fullName ?: ergoPreferences.username ?: "Kawan"
        tvGreetingName.text = "Halo, $userName! 👋"

        val btnStartLiveMonitor = findViewById<MaterialButton>(R.id.btnStartLiveMonitor)
        val btnProfile = findViewById<ImageView>(R.id.btnProfile)

        // Load Profile Image safely to Dashboard Top Bar
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

        btnStartLiveMonitor.setOnClickListener {
            // Launch Live AI Monitor recording session where posture popup alerts occur
            val intent = Intent(this, LiveMonitorActivity::class.java)
            startActivity(intent)
        }

        val btnStretchingGuide = findViewById<MaterialButton>(R.id.btnStretchingGuide)
        btnStretchingGuide.setOnClickListener {
            val dialog = StretchingGuideDialog()
            dialog.show(supportFragmentManager, "StretchingGuideDialog")
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
        
        // --- Bottom Navigation Logic ---
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_home // Set default tab

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_analysis -> {
                    startActivity(Intent(this, AnalysisActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_monitor -> {
                    startActivity(Intent(this, LiveMonitorActivity::class.java))
                    false // Don't select it permanently since LiveMonitor doesn't have a navbar
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0) // Seamless transition
                    finish() // Close main to avoid backstack pileup
                    true
                }
                else -> false
            }
        }
    }
}
