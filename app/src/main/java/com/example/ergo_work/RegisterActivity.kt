package com.example.ergo_work

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ergo_work.data.ApiClient
import com.example.ergo_work.data.ApiResponse
import com.example.ergo_work.data.AuthResponse
import com.example.ergo_work.data.ErgoPreferences
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var ergoPreferences: ErgoPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ergoPreferences = ErgoPreferences(this)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmailReg = findViewById<EditText>(R.id.etEmailReg)
        val etPasswordReg = findViewById<EditText>(R.id.etPasswordReg)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        btnRegister.setOnClickListener {
            val name = etFullName.text.toString().trim()
            val email = etEmailReg.text.toString().trim().lowercase()
            val password = etPasswordReg.text.toString().trim()
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua bidang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            btnRegister.isEnabled = false
            btnRegister.text = "Membuat Profil..."

            val registerRequest = mapOf(
                "fullName" to name,
                "email" to email,
                "username" to email, // using email as username to simplify flow
                "password" to password
            )

            ApiClient.instance.register(registerRequest).enqueue(object : Callback<ApiResponse<AuthResponse>> {
                override fun onResponse(
                    call: Call<ApiResponse<AuthResponse>>,
                    response: Response<ApiResponse<AuthResponse>>
                ) {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Buat Akun & Mulai Kalibrasi"

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@RegisterActivity, "Registrasi Berhasil! Silakan Login untuk masuk.", Toast.LENGTH_LONG).show()
                        // Arahkan ke Layar Login, BUKAN otomatis masuk ke aplikasi
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        var errorMsg = "Registrasi gagal, coba email lain."
                        try {
                            val errorBodyString = response.errorBody()?.string()
                            if (errorBodyString != null) {
                                val jsonObject = org.json.JSONObject(errorBodyString)
                                errorMsg = jsonObject.getString("message")
                            }
                        } catch (e: Exception) {}
                        
                        Toast.makeText(this@RegisterActivity, "Gagal: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<AuthResponse>>, t: Throwable) {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Buat Akun & Mulai Kalibrasi"
                    Toast.makeText(this@RegisterActivity, "Gagal konek server: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
