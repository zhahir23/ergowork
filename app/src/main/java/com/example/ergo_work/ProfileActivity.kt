package com.example.ergo_work

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ergo_work.data.ErgoPreferences
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class ProfileActivity : AppCompatActivity() {

    private lateinit var ivProfileAvatar: ShapeableImageView
    private lateinit var ergoPreferences: ErgoPreferences
    private lateinit var tvProfileName: TextView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                try {
                    // Minta izin akses permanen ke gambar tersebut (Persistable Permission)
                    contentResolver.takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    
                    ivProfileAvatar.setImageURI(imageUri)
                    
                    // Save URI to SharedPreferences
                    val prefs = getSharedPreferences("ergo_care_prefs", MODE_PRIVATE)
                    prefs.edit().putString("profile_image_uri", imageUri.toString()).apply()
                    
                    Toast.makeText(this, "Foto profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Gagal memproses gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ergoPreferences = ErgoPreferences(this)

        ivProfileAvatar = findViewById(R.id.ivProfileAvatar)
        tvProfileName = findViewById(R.id.tvProfileName)
        val btnEditName = findViewById<ImageView>(R.id.btnEditName)
        val avatarContainer = findViewById<View>(R.id.avatarContainer)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        // Load Real Full Name
        tvProfileName.text = ergoPreferences.fullName ?: ergoPreferences.username ?: "Nama Pengguna"

        // Load Profile Image safely
        val prefs = getSharedPreferences("ergo_care_prefs", MODE_PRIVATE)
        val savedImageUriString = prefs.getString("profile_image_uri", null)
        if (savedImageUriString != null) {
            try {
                val savedImageUri = android.net.Uri.parse(savedImageUriString)
                ivProfileAvatar.setImageURI(savedImageUri)
            } catch (e: Exception) {
                // Ignore if permission denied or file deleted
            }
        }

        avatarContainer.setOnClickListener {
            // Gunakan ACTION_OPEN_DOCUMENT agar bisa meminta izin permanen
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            pickImageLauncher.launch(intent)
        }

        btnEditName.setOnClickListener {
            showEditNameDialog()
        }

        btnLogout.setOnClickListener {
            ergoPreferences.clearAuthData()
            Toast.makeText(this, "Keluar dari sesi ErgoWork", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        
        // --- Bottom Navigation Logic ---
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_profile // Set Profile as active tab

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0) // Seamless transition
                    finish()
                    true
                }
                R.id.nav_analysis -> {
                    startActivity(Intent(this, AnalysisActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_monitor -> {
                    startActivity(Intent(this, LiveMonitorActivity::class.java))
                    false // Don't select it, monitor is full-screen
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    private fun showEditNameDialog() {
        val editText = EditText(this).apply {
            setText(ergoPreferences.fullName ?: ergoPreferences.username)
            setPadding(50, 50, 50, 50)
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Nama Pengguna")
            .setView(editText)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    ergoPreferences.fullName = newName // Update Full Name
                    tvProfileName.text = newName
                    Toast.makeText(this, "Nama berhasil diubah", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
