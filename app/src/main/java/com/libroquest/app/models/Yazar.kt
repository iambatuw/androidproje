package com.libroquest.app.models

import com.google.gson.annotations.SerializedName

data class Yazar(
    @SerializedName("YazarID") val yazarId: Int = 0,
    @SerializedName("Ad") val ad: String = "",
    @SerializedName("Soyad") val soyad: String = ""
)

data class YazarEkleRequest(
    val ad: String,
    val soyad: String
)

data class YazarEkleResponse(
    val mesaj: String,
    val yazarId: Int
)
