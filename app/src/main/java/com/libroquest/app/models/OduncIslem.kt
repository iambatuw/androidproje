package com.libroquest.app.models

import com.google.gson.annotations.SerializedName

data class OduncIslem(
    @SerializedName("IslemID") val islemId: Int = 0,
    @SerializedName("KullaniciID") val kullaniciId: Int = 0,
    @SerializedName("KitapID") val kitapId: Int = 0,
    @SerializedName("OduncTarihi") val oduncTarihi: String = "",
    @SerializedName("TeslimTarihi") val teslimTarihi: String = "",
    @SerializedName("GercekTeslimTarihi") val gercekTeslimTarihi: String? = null,
    @SerializedName("TeslimEdildiMi") val teslimEdildiMi: Boolean = false,
    @SerializedName("KitapAdi") val kitapAdi: String = "",
    @SerializedName("YazarAdSoyad") val yazarAdSoyad: String = "",
    @SerializedName("TurAdi") val turAdi: String? = null,
    @SerializedName("UyeAdSoyad") val uyeAdSoyad: String = "",
    @SerializedName("GecikmeGunu") val gecikmeGunu: Int = 0,
    @SerializedName("KalanGun") val kalanGun: Int = 0
)

data class OduncVerRequest(
    val kullaniciId: Int,
    val kitapId: Int,
    val teslimTarihi: String
)

data class OduncVerResponse(
    val mesaj: String,
    val islemId: Int
)

data class TeslimResponse(
    val mesaj: String,
    val puanDususu: Int,
    val gecikmeVar: Boolean
)
