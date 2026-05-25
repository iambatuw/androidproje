package com.libroquest.app.models

import com.google.gson.annotations.SerializedName

data class GecikenKitap(
    @SerializedName("IslemID") val islemId: Int = 0,
    @SerializedName("KullaniciID") val kullaniciId: Int = 0,
    @SerializedName("UyeAdSoyad") val uyeAdSoyad: String = "",
    @SerializedName("Sinif") val sinif: String = "",
    @SerializedName("Cinsiyet") val cinsiyet: String = "",
    @SerializedName("Puan") val puan: Int = 0,
    @SerializedName("KitapAdi") val kitapAdi: String = "",
    @SerializedName("KitapID") val kitapId: Int = 0,
    @SerializedName("YazarAdSoyad") val yazarAdSoyad: String = "",
    @SerializedName("OduncTarihi") val oduncTarihi: String = "",
    @SerializedName("TeslimTarihi") val teslimTarihi: String = "",
    @SerializedName("GecikmeGunu") val gecikmeGunu: Int = 0,
    @SerializedName("PuanDususu") val puanDususu: Int = 0
)
