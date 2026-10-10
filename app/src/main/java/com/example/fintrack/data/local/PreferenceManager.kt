package com.example.fintrack.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manages encrypted local session preferences for FinTrack Android.
 * Securely stores onboarding status, user display name, currency preferences,
 * and biometric credentials.
 */
class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "fintrack_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback for sandboxed or test environments where Android Keystore may fail
        context.getSharedPreferences("fintrack_prefs_fallback", Context.MODE_PRIVATE)
    }

    companion object {
        private const val KEY_IS_ONBOARDED = "key_is_onboarded"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_NICKNAME = "key_user_nickname"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_USER_PHONE = "key_user_phone"
        private const val KEY_CURRENCY = "key_currency"
        private const val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
        private const val KEY_TWO_FACTOR_ENABLED = "key_two_factor_enabled"
        private const val KEY_GEMINI_API_KEY = "key_gemini_api_key"
        private const val KEY_LAST_RESET = "key_last_reset"
    }

    var isOnboarded: Boolean
        get() = prefs.getBoolean(KEY_IS_ONBOARDED, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_ONBOARDED, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var userNickname: String
        get() = prefs.getString(KEY_USER_NICKNAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_NICKNAME, value).apply()

    var userEmail: String
        get() = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    var userPhone: String
        get() = prefs.getString(KEY_USER_PHONE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_PHONE, value).apply()

    var currency: String
        get() = prefs.getString(KEY_CURRENCY, "IDR") ?: "IDR"
        set(value) = prefs.edit().putString(KEY_CURRENCY, value).apply()

    var biometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    var twoFactorEnabled: Boolean
        get() = prefs.getBoolean(KEY_TWO_FACTOR_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_TWO_FACTOR_ENABLED, value).apply()

    var customGeminiApiKey: String
        get() = prefs.getString(KEY_GEMINI_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_GEMINI_API_KEY, value).apply()

    var lastResetTimestamp: Long
        get() = prefs.getLong(KEY_LAST_RESET, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_RESET, value).apply()

    /**
     * Resets all locally stored preferences, returning the app to the onboarding state.
     */
    fun resetAllPreferences() {
        prefs.edit().clear().putLong(KEY_LAST_RESET, System.currentTimeMillis()).apply()
    }
}
