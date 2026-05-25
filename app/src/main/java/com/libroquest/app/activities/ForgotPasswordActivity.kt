package com.libroquest.app.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.libroquest.app.R
import com.libroquest.app.api.RetrofitClient
import com.libroquest.app.models.ForgotPasswordRequest
import com.libroquest.app.models.GenericResponse
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etKullaniciAdi = findViewById<EditText>(R.id.etKullaniciAdi)
        val etGuvenlikAnahtari = findViewById<EditText>(R.id.etGuvenlikAnahtari)
        val etYeniSifre = findViewById<EditText>(R.id.etYeniSifre)

        val guvenlikKodu = com.libroquest.app.api.CihazBilgisi.guvenlikKoduOlustur(this)
        etGuvenlikAnahtari.setText(guvenlikKodu)
        etGuvenlikAnahtari.isFocusable = false
        etGuvenlikAnahtari.isFocusableInTouchMode = false
        val btnSifreGuncelle = findViewById<MaterialButton>(R.id.btnSifreGuncelle)
        val btnGeri = findViewById<MaterialButton>(R.id.btnGeri)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        btnGeri.setOnClickListener { finish() }

        btnSifreGuncelle.setOnClickListener {
            val kullaniciAdi = etKullaniciAdi.text.toString().trim()
            val guvenlikAnahtari = etGuvenlikAnahtari.text.toString().trim()
            val yeniSifre = etYeniSifre.text.toString().trim()

            if (kullaniciAdi.isEmpty()) { etKullaniciAdi.error = "Kullanıcı adı gerekli"; return@setOnClickListener }
            if (guvenlikAnahtari.isEmpty()) { etGuvenlikAnahtari.error = "Güvenlik anahtarı gerekli"; return@setOnClickListener }
            if (yeniSifre.length < 6) { etYeniSifre.error = "En az 6 karakter"; return@setOnClickListener }

            progressBar.visibility = View.VISIBLE
            btnSifreGuncelle.isEnabled = false

            val cihazParmakIzi = com.libroquest.app.api.CihazBilgisi.parmakIziOlustur(this@ForgotPasswordActivity)
            val request = ForgotPasswordRequest(kullaniciAdi, guvenlikAnahtari, cihazParmakIzi, yeniSifre)
            RetrofitClient.apiService.sifremiUnuttum(request).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    progressBar.visibility = View.GONE
                    btnSifreGuncelle.isEnabled = true
                    if (response.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity, "Şifre başarıyla güncellendi!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else if (response.code() == 403) {
                        Toast.makeText(this@ForgotPasswordActivity, "Güvenlik anahtarı yanlış veya farklı cihaz!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "Kullanıcı bulunamadı!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    btnSifreGuncelle.isEnabled = true
                    Toast.makeText(this@ForgotPasswordActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
