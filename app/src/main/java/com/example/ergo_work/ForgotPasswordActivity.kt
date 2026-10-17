package com.example.ergo_work

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val etEmail = findViewById<EditText>(R.id.etEmailForgot)
        val btnSendOtp = findViewById<MaterialButton>(R.id.btnSendOtp)

        btnBack.setOnClickListener { finish() }

        btnSendOtp.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            // Simulasi pengiriman OTP dengan keterangan BOT
            Toast.makeText(this, "[SIMULASI EMAIL]\nDari: ergowork.bot@gmail.com\nKode OTP Anda: 1234", Toast.LENGTH_LONG).show()

            val intent = Intent(this, OtpVerificationActivity::class.java)
            intent.putExtra("USER_EMAIL", email)
            startActivity(intent)
        }
    }
}
