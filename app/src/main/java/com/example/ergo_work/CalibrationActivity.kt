package com.example.ergo_work

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ergo_work.data.ErgoPreferences
import com.google.android.material.button.MaterialButton

class CalibrationActivity : AppCompatActivity() {

    private lateinit var ergoPreferences: ErgoPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)

        ergoPreferences = ErgoPreferences(this)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnSaveCalibration = findViewById<MaterialButton>(R.id.btnSaveCalibration)
        val btnResetCamera = findViewById<MaterialButton>(R.id.btnResetCamera)

        btnSaveCalibration.setOnClickListener {
            Toast.makeText(this, "Baseline terkunci! Memulai sesi kerja...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LiveMonitorActivity::class.java))
            finish()
        }

        btnResetCamera.setOnClickListener {
            Toast.makeText(this, "Kalibrasi ulang posisi kamera diaktifkan.", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
