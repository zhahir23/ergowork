package com.example.ergo_work

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class OtpVerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        val email = intent.getStringExtra("USER_EMAIL") ?: "user@example.com"
        
        val tvOtpMessage = findViewById<TextView>(R.id.tvOtpMessage)
        tvOtpMessage.text = "Kode 4 digit telah dikirim oleh ergowork.bot@gmail.com ke email\n$email"

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val etOtp1 = findViewById<EditText>(R.id.etOtp1)
        val etOtp2 = findViewById<EditText>(R.id.etOtp2)
        val etOtp3 = findViewById<EditText>(R.id.etOtp3)
        val etOtp4 = findViewById<EditText>(R.id.etOtp4)
        val btnVerifyOtp = findViewById<MaterialButton>(R.id.btnVerifyOtp)

        btnBack.setOnClickListener { finish() }

        btnVerifyOtp.setOnClickListener {
            val otp = "${etOtp1.text}${etOtp2.text}${etOtp3.text}${etOtp4.text}"
            
            if (otp == "1234") { // Simulasi kode valid
                val intent = Intent(this, ResetPasswordActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Kode OTP salah. Coba lagi.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
