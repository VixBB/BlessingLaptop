package com.example.loginmenu

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_IS_ADMIN = "is_admin"
    private const val KEY_NAMA = "nama"
    private const val KEY_NIS = "nis"
    private const val KEY_KELAS = "kelas"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var isAdmin: Boolean
        get() = prefs.getBoolean(KEY_IS_ADMIN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_ADMIN, value).apply()

    var nama: String?
        get() = prefs.getString(KEY_NAMA, null)
        set(value) = prefs.edit().putString(KEY_NAMA, value).apply()

    var nis: String?
        get() = prefs.getString(KEY_NIS, null)
        set(value) = prefs.edit().putString(KEY_NIS, value).apply()

    var kelas: String?
        get() = prefs.getString(KEY_KELAS, null)
        set(value) = prefs.edit().putString(KEY_KELAS, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
