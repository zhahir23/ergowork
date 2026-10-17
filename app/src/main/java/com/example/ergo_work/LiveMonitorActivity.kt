package com.example.ergo_work

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ergo_work.data.ApiClient
import com.example.ergo_work.data.ApiResponse
import com.example.ergo_work.data.ErgoPreferences
import com.example.ergo_work.data.PostureAnalyzer
import com.example.ergo_work.data.PostureRequest
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveMonitorActivity : AppCompatActivity() {

    private lateinit var ergoPreferences: ErgoPreferences
    private lateinit var viewFinder: PreviewView
    private lateinit var dotRec: View
    private lateinit var tvRestTimer: TextView
    private var restTimer: CountDownTimer? = null

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_monitor)

        ergoPreferences = ErgoPreferences(this)
        val currentUserId = ergoPreferences.userId

        viewFinder = findViewById(R.id.viewFinder)
        dotRec = findViewById(R.id.dotRec)
        tvRestTimer = findViewById(R.id.tvRestTimer)

        // Start Rest Reminder Timer (45 minutes default)
        val minutesInMillis = 45L * 60 * 1000
        restTimer = object : CountDownTimer(minutesInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val mins = millisUntilFinished / 1000 / 60
                val secs = millisUntilFinished / 1000 % 60
                tvRestTimer.text = String.format("%02d:%02d", mins, secs)
            }

            override fun onFinish() {
                tvRestTimer.text = "00:00"
                Toast.makeText(this@LiveMonitorActivity, "Waktunya Istirahat!", Toast.LENGTH_LONG).show()
            }
        }.start()

        // Blink REC indicator
        dotRec.animate().alpha(0.2f).setDuration(800).withEndAction {
            dotRec.animate().alpha(1f).setDuration(800).withEndAction {
                // Loop recursively
                dotRec.animate().alpha(0.2f).setDuration(800).start()
            }.start()
        }.start()

        // Request Camera Permission
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        val btnBackLive = findViewById<ImageView>(R.id.btnBackLive)
        val btnSimulateError = findViewById<MaterialButton>(R.id.btnSimulateError)
        val popupOverlay = findViewById<View>(R.id.popupOverlay)
        val btnClosePopup = findViewById<MaterialButton>(R.id.btnClosePopup)

        btnBackLive.setOnClickListener {
            finish()
        }

        btnSimulateError.setOnClickListener {
            // Trigger AI posture error popup overlay over camera stream
            popupOverlay.visibility = View.VISIBLE
            Toast.makeText(this, "AI Mendeteksi Posisi Leher Buruk (>20°)", Toast.LENGTH_SHORT).show()
            
            // Generate telemetry data
            val telemetryData = PostureAnalyzer.analyzePosture(22.5f, 15.0f)
            
            if (currentUserId != -1L) {
                val request = PostureRequest(
                    userId = currentUserId,
                    ergoScore = telemetryData.ergoScore,
                    neckAngle = telemetryData.neckAngle,
                    shoulderSymmetry = telemetryData.shoulderSymmetry,
                    isPostureSafe = telemetryData.isPostureSafe,
                    errorDescription = telemetryData.errorDescription
                )

                ApiClient.instance.saveTelemetry(request).enqueue(object : Callback<ApiResponse<Any>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("Telemetry", "Data postur berhasil disinkronkan ke server")
                        } else {
                            Log.e("Telemetry", "Gagal sinkron data postur: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                        Log.e("Telemetry", "Gagal konek server saat simpan telemetry", t)
                    }
                })
            }
        }

        btnClosePopup.setOnClickListener {
            popupOverlay.visibility = View.GONE
            Toast.makeText(this, "Posisi diperbaiki. Kamera kembali merekam normal.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        restTimer?.cancel()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // Select front camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)

            } catch(exc: Exception) {
                Log.e("LiveMonitorActivity", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
