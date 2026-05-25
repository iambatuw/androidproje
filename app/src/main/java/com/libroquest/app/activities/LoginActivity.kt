package com.libroquest.app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.libroquest.app.R
import com.libroquest.app.api.RetrofitClient
import com.libroquest.app.api.SessionManager
import com.libroquest.app.models.LoginRequest
import com.libroquest.app.models.LoginResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.FrameLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var etKullaniciAdi: TextInputEditText
    private lateinit var etSifre: TextInputEditText
    private lateinit var btnGirisYap: MaterialButton
    private lateinit var btnKayitOl: MaterialButton
    private lateinit var btnSifremiUnuttum: MaterialButton
    private lateinit var progressOverlay: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        session = SessionManager(this)

        // Zaten giriş yapılmışsa yönlendir
        if (session.isLoggedIn()) {
            yonlendir()
            return
        }

        etKullaniciAdi = findViewById(R.id.etKullaniciAdi)
        etSifre = findViewById(R.id.etSifre)
        btnGirisYap = findViewById(R.id.btnGirisYap)
        btnKayitOl = findViewById(R.id.btnKayitOl)
        btnSifremiUnuttum = findViewById(R.id.btnSifremiUnuttum)
        progressOverlay = findViewById(R.id.progressOverlay)

        btnGirisYap.setOnClickListener { girisYap() }

        btnKayitOl.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnSifremiUnuttum.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun girisYap() {
        val kullaniciAdi = etKullaniciAdi.text.toString().trim()
        val sifre = etSifre.text.toString().trim()

        if (kullaniciAdi.isEmpty()) {
            etKullaniciAdi.error = "Kullanıcı adı gerekli"
            return
        }
        if (sifre.isEmpty()) {
            etSifre.error = "Şifre gerekli"
            return
        }

        progressOverlay.visibility = View.VISIBLE
        btnGirisYap.isEnabled = false

        val request = LoginRequest(kullaniciAdi, sifre)
        RetrofitClient.apiService.girisYap(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                progressOverlay.visibility = View.GONE
                btnGirisYap.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val k = body.kullanici

                    session.oturumKaydet(
                        token = body.token,
                        id = k.id,
                        ad = k.ad,
                        soyad = k.soyad,
                        kullaniciAdi = k.kullaniciAdi,
                        rol = k.rol,
                        puan = k.puan,
                        cinsiyet = k.cinsiyet,
                        sinif = k.sinif
                    )

                    Toast.makeText(this@LoginActivity, "Merhaba ${k.ad}, Hoşgeldin!", Toast.LENGTH_SHORT).show()
                    yonlendir()
                } else {
                    Toast.makeText(this@LoginActivity, "Kullanıcı adı veya şifre hatalı!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                progressOverlay.visibility = View.GONE
                btnGirisYap.isEnabled = true
                Toast.makeText(this@LoginActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun yonlendir() {
        val intent = if (session.isAdmin()) {
            Intent(this, AdminDashboardActivity::class.java)
        } else {
            Intent(this, UserDashboardActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
