package com.libroquest.app.api

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import java.security.MessageDigest

object CihazBilgisi {

    @SuppressLint("HardwareIds")
    fun parmakIziOlustur(context: Context): String {
        return sha256(cihazBilgileriTopla(context))
    }

    @SuppressLint("HardwareIds")
    fun guvenlikKoduOlustur(context: Context): String {
        val hash = sha256(cihazBilgileriTopla(context) + ":guvenlik")
        return hash.substring(0, 8).uppercase()
    }

    @SuppressLint("HardwareIds")
    private fun cihazBilgileriTopla(context: Context): String {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
        val brand = Build.BRAND ?: ""
        val model = Build.MODEL ?: ""
        val device = Build.DEVICE ?: ""
        val hardware = Build.HARDWARE ?: ""
        val manufacturer = Build.MANUFACTURER ?: ""

        val values = listOf(androidId, brand, model, device, hardware, manufacturer)
            .filter { it.isNotEmpty() }
        return values.joinToString("|")
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
