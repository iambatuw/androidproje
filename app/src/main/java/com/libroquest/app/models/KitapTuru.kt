package com.libroquest.app.models

import com.google.gson.annotations.SerializedName

data class KitapTuru(
    @SerializedName("TurID") val turId: Int = 0,
    @SerializedName("TurAdi") val turAdi: String = ""
)

data class TurEkleRequest(
    val turAdi: String
)

data class TurEkleResponse(
    val mesaj: String,
    val turId: Int
)
