package com.example.ergo_work.data

data class ErgoTelemetry(
    val ergoScore: Int,
    val neckAngle: Float,
    val shoulderSymmetry: Float,
    val isPostureSafe: Boolean,
    val errorDescription: String?
)

object PostureAnalyzer {
    // Evaluates neck angle against threshold (e.g. 15 degrees)
    fun analyzePosture(currentNeckAngle: Float, threshold: Float): ErgoTelemetry {
        val safe = currentNeckAngle <= threshold
        val score = if (safe) 88 else 65
        val error = if (!safe) "Leher > ${currentNeckAngle.toInt()}° (Risiko Bungkuk)" else null
        return ErgoTelemetry(
            ergoScore = score,
            neckAngle = currentNeckAngle,
            shoulderSymmetry = 99.2f,
            isPostureSafe = safe,
            errorDescription = error
        )
    }
}
