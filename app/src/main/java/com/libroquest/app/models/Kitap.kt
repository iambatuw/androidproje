package com.libroquest.app.models

import com.google.gson.annotations.SerializedName

data class Kitap(
    @SerializedName("KitapID") val kitapId: Int = 0,
    @SerializedName("KitapAdi") val kitapAdi: String = "",
    @SerializedName("YazarID") val yazarId: Int = 0,
    @SerializedName("TurID") val turId: Int = 0,
    @SerializedName("ISBN") val isbn: String = "",
    @SerializedName("SayfaSayisi") val sayfaSayisi: Int = 0,
    @SerializedName("StokAdedi") val stokAdedi: Int = 0,
    @SerializedName("YazarAdSoyad") val yazarAdSoyad: String = "",
    @SerializedName("TurAdi") val turAdi: String = "",
    @SerializedName("KayitTarihi") val kayitTarihi: String = ""
)

data class KitapEkleRequest(
    val kitapAdi: String,
    val yazarId: Int,
    val turId: Int,
    val isbn: String,
    val sayfaSayisi: Int,
    val stokAdedi: Int
)

data class KitapEkleResponse(
    val mesaj: String,
    val kitapId: Int
)
