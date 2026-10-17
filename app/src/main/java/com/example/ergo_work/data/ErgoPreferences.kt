package com.example.ergo_work.data

import android.content.Context
import android.content.SharedPreferences

class ErgoPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ergo_care_prefs", Context.MODE_PRIVATE)

    // Auth States
    var authToken: String?
        get() = prefs.getString("auth_token", null)
        set(value) = prefs.edit().putString("auth_token", value).apply()

    var userId: Long
        get() = prefs.getLong("user_id", -1L)
        set(value) = prefs.edit().putLong("user_id", value).apply()

    var username: String?
        get() = prefs.getString("username", null)
        set(value) = prefs.edit().putString("username", value).apply()

    var fullName: String?
        get() = prefs.getString("full_name", null)
        set(value) = prefs.edit().putString("full_name", value).apply()

    fun clearAuthData() {
        prefs.edit()
            .remove("auth_token")
            .remove("user_id")
            .remove("username")
            .remove("full_name")
            .apply()
    }

    // App Configurations
    var neckAngleThreshold: Int
        get() = prefs.getInt("neck_angle_threshold", 15)
        set(value) = prefs.edit().putInt("neck_angle_threshold", value).apply()

    var reminderIntervalMinutes: Int
        get() = prefs.getInt("reminder_interval", 3)
        set(value) = prefs.edit().putInt("reminder_interval", value).apply()

    var isMeetingDndEnabled: Boolean
        get() = prefs.getBoolean("meeting_dnd", true)
        set(value) = prefs.edit().putBoolean("meeting_dnd", value).apply()

    var hapticFeedbackEnabled: Boolean
        get() = prefs.getBoolean("haptic_feedback", true)
        set(value) = prefs.edit().putBoolean("haptic_feedback", value).apply()

    var soundChimeEnabled: Boolean
        get() = prefs.getBoolean("sound_chime", true)
        set(value) = prefs.edit().putBoolean("sound_chime", value).apply()
}
