package com.libroquest.app.api

import com.libroquest.app.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/giris")
    fun girisYap(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/kayit")
    fun kayitOl(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/sifremi-unuttum")
    fun sifremiUnuttum(@Body request: ForgotPasswordRequest): Call<GenericResponse>

    // Kullanıcılar
    @GET("api/kullanicilar")
    fun kullanicilariGetir(@Header("Authorization") token: String): Call<List<Kullanici>>

    @GET("api/kullanicilar/{id}")
    fun kullaniciGetir(@Header("Authorization") token: String, @Path("id") id: Int): Call<Kullanici>

    @PUT("api/kullanicilar/{id}")
    fun kullaniciGuncelle(@Header("Authorization") token: String, @Path("id") id: Int, @Body kullanici: Kullanici): Call<GenericResponse>

    @DELETE("api/kullanicilar/{id}")
    fun kullaniciSil(@Header("Authorization") token: String, @Path("id") id: Int): Call<GenericResponse>

    // Yazarlar
    @GET("api/yazarlar")
    fun yazarlariGetir(@Header("Authorization") token: String): Call<List<Yazar>>

    @POST("api/yazarlar")
    fun yazarEkle(@Header("Authorization") token: String, @Body request: YazarEkleRequest): Call<YazarEkleResponse>

    @DELETE("api/yazarlar/{id}")
    fun yazarSil(@Header("Authorization") token: String, @Path("id") id: Int): Call<GenericResponse>

    // Kitap Türleri
    @GET("api/turler")
    fun turleriGetir(@Header("Authorization") token: String): Call<List<KitapTuru>>

    @POST("api/turler")
    fun turEkle(@Header("Authorization") token: String, @Body request: TurEkleRequest): Call<TurEkleResponse>

    // Kitaplar
    @GET("api/kitaplar")
    fun kitaplariGetir(@Header("Authorization") token: String): Call<List<Kitap>>

    @GET("api/kitaplar/{id}")
    fun kitapGetir(@Header("Authorization") token: String, @Path("id") id: Int): Call<Kitap>

    @POST("api/kitaplar")
    fun kitapEkle(@Header("Authorization") token: String, @Body request: KitapEkleRequest): Call<KitapEkleResponse>

    @DELETE("api/kitaplar/{id}")
    fun kitapSil(@Header("Authorization") token: String, @Path("id") id: Int): Call<GenericResponse>

    // Ödünç İşlemleri
    @GET("api/odunc")
    fun oduncIslemleriGetir(@Header("Authorization") token: String): Call<List<OduncIslem>>

    @GET("api/odunc/kullanici/{id}")
    fun kullaniciOduncGetir(@Header("Authorization") token: String, @Path("id") id: Int): Call<List<OduncIslem>>

    @POST("api/odunc")
    fun oduncVer(@Header("Authorization") token: String, @Body request: OduncVerRequest): Call<OduncVerResponse>

    @PUT("api/odunc/{id}/teslim")
    fun teslimAl(@Header("Authorization") token: String, @Path("id") id: Int): Call<TeslimResponse>

    // Gecikenler
    @GET("api/gecikenler")
    fun gecikenleriGetir(@Header("Authorization") token: String): Call<List<GecikenKitap>>

    // İstatistikler
    @GET("api/istatistikler")
    fun istatistikleriGetir(@Header("Authorization") token: String): Call<Map<String, Int>>
}
