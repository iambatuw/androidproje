package com.libroquest.app.api

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("LibroQuestSession", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_AD = "ad"
        private const val KEY_SOYAD = "soyad"
        private const val KEY_KULLANICI_ADI = "kullanici_adi"
        private const val KEY_ROL = "rol"
        private const val KEY_PUAN = "puan"
        private const val KEY_CINSIYET = "cinsiyet"
        private const val KEY_SINIF = "sinif"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun oturumKaydet(token: String, id: Int, ad: String, soyad: String,
                     kullaniciAdi: String, rol: String, puan: Int,
                     cinsiyet: String?, sinif: String?) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, id)
            putString(KEY_AD, ad)
            putString(KEY_SOYAD, soyad)
            putString(KEY_KULLANICI_ADI, kullaniciAdi)
            putString(KEY_ROL, rol)
            putInt(KEY_PUAN, puan)
            putString(KEY_CINSIYET, cinsiyet ?: "")
            putString(KEY_SINIF, sinif ?: "")
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getToken(): String = "Bearer ${prefs.getString(KEY_TOKEN, "") ?: ""}"
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, 0)
    fun getAd(): String = prefs.getString(KEY_AD, "") ?: ""
    fun getSoyad(): String = prefs.getString(KEY_SOYAD, "") ?: ""
    fun getKullaniciAdi(): String = prefs.getString(KEY_KULLANICI_ADI, "") ?: ""
    fun getRol(): String = prefs.getString(KEY_ROL, "kullanici") ?: "kullanici"
    fun getPuan(): Int = prefs.getInt(KEY_PUAN, 100)
    fun getCinsiyet(): String = prefs.getString(KEY_CINSIYET, "") ?: ""
    fun getSinif(): String = prefs.getString(KEY_SINIF, "") ?: ""
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    fun isAdmin(): Boolean = getRol() == "admin"

    fun puanGuncelle(yeniPuan: Int) {
        prefs.edit().putInt(KEY_PUAN, yeniPuan).apply()
    }

    fun otumuKapat() {
        prefs.edit().clear().apply()
    }
}
