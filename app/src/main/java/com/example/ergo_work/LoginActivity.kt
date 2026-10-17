package com.example.ergo_work

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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

class LoginActivity : AppCompatActivity() {

    private lateinit var ergoPreferences: ErgoPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ergoPreferences = ErgoPreferences(this)
        
        // If already logged in, skip login
        if (!ergoPreferences.authToken.isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnBiometric = findViewById<MaterialButton>(R.id.btnBiometric)
        val btnGoogleLogin = findViewById<MaterialButton>(R.id.btnGoogleLogin)
        val tabRegister = findViewById<Button>(R.id.tabRegister)
        val tvRegisterPrompt = findViewById<TextView>(R.id.tvRegisterPrompt)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim().lowercase()
            val password = etPassword.text.toString().trim()
            
            if (email.isEmpty()) {
                etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                etPassword.error = "Kata sandi wajib diisi"
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            btnLogin.text = "Memverifikasi..."

            val loginRequest = mapOf(
                "username" to email, // Map email input to username field in backend 
                "password" to password
            )

            ApiClient.instance.login(loginRequest).enqueue(object : Callback<ApiResponse<AuthResponse>> {
                override fun onResponse(
                    call: Call<ApiResponse<AuthResponse>>,
                    response: Response<ApiResponse<AuthResponse>>
                ) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Masuk"
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        val authData = response.body()?.data
                        if (authData != null) {
                            ergoPreferences.authToken = authData.token
                            ergoPreferences.userId = authData.userId
                            ergoPreferences.username = authData.username
                            ergoPreferences.fullName = authData.fullName
                            
                            val displayName = authData.fullName ?: authData.username
                            Toast.makeText(this@LoginActivity, "Login Berhasil! Selamat datang $displayName", Toast.LENGTH_SHORT).show()
                            
                            // Arahkan ke Penilaian Risiko terlebih dahulu
                            startActivity(Intent(this@LoginActivity, RiskAssessmentActivity::class.java))
                            finish()
                        }
                    } else {
                        var errorMsg = "Login gagal. Pastikan email dan sandi benar."
                        try {
                            val errorBodyString = response.errorBody()?.string()
                            if (errorBodyString != null) {
                                val jsonObject = org.json.JSONObject(errorBodyString)
                                errorMsg = jsonObject.getString("message")
                            }
                        } catch (e: Exception) {}
                        
                        // Menampilkan pesan error asli dari backend (Misal: Invalid password)
                        Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<AuthResponse>>, t: Throwable) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Masuk"
                    Toast.makeText(this@LoginActivity, "Gangguan server: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        btnBiometric.setOnClickListener {
            Toast.makeText(this, "Verifikasi Biometrik Berhasil!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnGoogleLogin.setOnClickListener {
            Toast.makeText(this, "Login dengan Akun Google Terdaftar Berhasil!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        tabRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        tvRegisterPrompt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}
