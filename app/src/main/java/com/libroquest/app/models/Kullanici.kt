package com.libroquest.app.models

import com.google.gson.annotations.SerializedName

data class Kullanici(
    @SerializedName("KullaniciID") val kullaniciId: Int = 0,
    @SerializedName("Ad") val ad: String = "",
    @SerializedName("Soyad") val soyad: String = "",
    @SerializedName("Cinsiyet") val cinsiyet: String = "",
    @SerializedName("Sinif") val sinif: String = "",
    @SerializedName("KullaniciAdi") val kullaniciAdi: String = "",
    @SerializedName("Rol") val rol: String = "kullanici",
    @SerializedName("Puan") val puan: Int = 100,
    @SerializedName("KayitTarihi") val kayitTarihi: String = ""
)

data class LoginRequest(
    val kullaniciAdi: String,
    val sifre: String
)

data class LoginResponse(
    val mesaj: String,
    val token: String,
    val kullanici: KullaniciInfo
)

data class KullaniciInfo(
    val id: Int,
    val ad: String,
    val soyad: String,
    val kullaniciAdi: String,
    val rol: String,
    val puan: Int,
    val cinsiyet: String?,
    val sinif: String?
)

data class RegisterRequest(
    val ad: String,
    val soyad: String,
    val cinsiyet: String,
    val sinif: String,
    val kullaniciAdi: String,
    val sifre: String,
    val guvenlikAnahtari: String,
    val cihazParmakIzi: String
)

data class RegisterResponse(
    val mesaj: String,
    val kullaniciId: Int
)

data class ForgotPasswordRequest(
    val kullaniciAdi: String,
    val guvenlikAnahtari: String,
    val cihazParmakIzi: String,
    val yeniSifre: String
)

data class GenericResponse(
    val mesaj: String
)
