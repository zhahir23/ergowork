package com.example.ergo_work

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RiskAssessmentActivity : AppCompatActivity() {

    private var currentQuestion = 1
    private val totalQuestions = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_assessment)

        val btnNext = findViewById<MaterialButton>(R.id.btnNextAssessment)
        val btnBack = findViewById<MaterialButton>(R.id.btnBackAssessment)
        val tvProgress = findViewById<TextView>(R.id.tvQuestionProgress)
        val tvQuestion = findViewById<TextView>(R.id.tvQuestion)
        val tvCategory = findViewById<TextView>(R.id.tvQuestionCategory)
        val pbProgress = findViewById<ProgressBar>(R.id.progressBar)
        val rgOptions = findViewById<RadioGroup>(R.id.rgOptions)
        val rbOpt1 = findViewById<RadioButton>(R.id.rbOpt1)
        val rbOpt2 = findViewById<RadioButton>(R.id.rbOpt2)
        val rbOpt3 = findViewById<RadioButton>(R.id.rbOpt3)
        val rbOpt4 = findViewById<RadioButton>(R.id.rbOpt4)

        btnBack.setOnClickListener {
            if (currentQuestion > 1) {
                currentQuestion--
                updateQuestionUI(tvProgress, tvQuestion, tvCategory, pbProgress, rgOptions, btnNext, rbOpt1, rbOpt2, rbOpt3, rbOpt4)
            } else {
                Toast.makeText(this, "Ini adalah pertanyaan pertama.", Toast.LENGTH_SHORT).show()
            }
        }

        btnNext.setOnClickListener {
            if (rgOptions.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Silakan pilih salah satu jawaban", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentQuestion < totalQuestions) {
                currentQuestion++
                updateQuestionUI(tvProgress, tvQuestion, tvCategory, pbProgress, rgOptions, btnNext, rbOpt1, rbOpt2, rbOpt3, rbOpt4)
            } else {
                // Selesai penilaian, lempar ke Calibration
                Toast.makeText(this, "Penilaian Risiko Selesai", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, CalibrationActivity::class.java))
                finish()
            }
        }
    }

    private fun updateQuestionUI(
        tvProgress: TextView, tvQuestion: TextView, tvCategory: TextView, pbProgress: ProgressBar,
        rgOptions: RadioGroup, btnNext: MaterialButton, 
        rbOpt1: RadioButton, rbOpt2: RadioButton, rbOpt3: RadioButton, rbOpt4: RadioButton
    ) {
        tvProgress.text = "PERTANYAAN $currentQuestion DARI $totalQuestions"
        pbProgress.progress = currentQuestion
        rgOptions.clearCheck()
        
        when (currentQuestion) {
            1 -> {
                tvCategory.text = "Layar & Meja"
                tvQuestion.text = "Bagaimana posisi layar terhadap mata Anda saat bekerja?"
                rbOpt1.text = "Sejajar dengan mata"
                rbOpt2.text = "Terlalu rendah (leher menunduk)"
                rbOpt3.text = "Terlalu tinggi (leher mendongak)"
                rbOpt4.text = "Tidak tentu / berubah-ubah"
            }
            2 -> {
                tvCategory.text = "Durasi & Aktivitas"
                tvQuestion.text = "Berapa lama rata-rata Anda duduk tanpa berdiri atau peregangan?"
                rbOpt1.text = "Kurang dari 30 menit"
                rbOpt2.text = "30 - 60 menit"
                rbOpt3.text = "1 - 2 jam"
                rbOpt4.text = "Lebih dari 2 jam non-stop"
            }
            3 -> {
                tvCategory.text = "Dukungan Kursi"
                tvQuestion.text = "Apakah kursi Anda memiliki penopang punggung bawah (lumbar support)?"
                rbOpt1.text = "Ya, dan sangat pas"
                rbOpt2.text = "Ada, tapi kurang menopang"
                rbOpt3.text = "Hanya sandaran biasa (lurus)"
                rbOpt4.text = "Tidak ada sandaran punggung"
            }
            4 -> {
                tvCategory.text = "Gejala Fisik"
                tvQuestion.text = "Seberapa sering Anda merasakan pegal di area leher atau bahu?"
                rbOpt1.text = "Jarang / Hampir tidak pernah"
                rbOpt2.text = "Kadang-kadang (saat lelah)"
                rbOpt3.text = "Cukup sering (1-2x seminggu)"
                rbOpt4.text = "Hampir setiap hari"
            }
            5 -> {
                tvCategory.text = "Posisi Tubuh"
                tvQuestion.text = "Bagaimana posisi kaki Anda saat duduk bekerja?"
                rbOpt1.text = "Rata menapak lantai (90 derajat)"
                rbOpt2.text = "Menggantung / Jinjit"
                rbOpt3.text = "Sering menyilang (cross-legged)"
                rbOpt4.text = "Duduk bersila / posisi asimetris"
            }
        }

        if (currentQuestion == totalQuestions) {
            btnNext.text = "Selesai"
        } else {
            btnNext.text = "Lanjut"
        }
    }
}
