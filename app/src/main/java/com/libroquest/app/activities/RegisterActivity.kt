package com.libroquest.app.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.libroquest.app.R
import com.libroquest.app.api.RetrofitClient
import com.libroquest.app.models.RegisterRequest
import com.libroquest.app.models.RegisterResponse
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etAd: EditText
    private lateinit var etSoyad: EditText
    private lateinit var spinnerCinsiyet: Spinner
    private lateinit var etSinif: EditText
    private lateinit var etKullaniciAdi: EditText
    private lateinit var etSifre: EditText
    private lateinit var etGuvenlikAnahtari: EditText
    private lateinit var btnKayitOl: MaterialButton
    private lateinit var progressOverlay: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etAd = findViewById(R.id.etAd)
        etSoyad = findViewById(R.id.etSoyad)
        spinnerCinsiyet = findViewById(R.id.spinnerCinsiyet)
        etSinif = findViewById(R.id.etSinif)
        etKullaniciAdi = findViewById(R.id.etKullaniciAdi)
        etSifre = findViewById(R.id.etSifre)
        etGuvenlikAnahtari = findViewById(R.id.etGuvenlikAnahtari)
        btnKayitOl = findViewById(R.id.btnKayitOl)
        progressOverlay = findViewById(R.id.progressOverlay)

        val guvenlikKodu = com.libroquest.app.api.CihazBilgisi.guvenlikKoduOlustur(this)
        etGuvenlikAnahtari.setText(guvenlikKodu)
        etGuvenlikAnahtari.isFocusable = false
        etGuvenlikAnahtari.isFocusableInTouchMode = false

        val cinsiyetler = arrayOf("Seçiniz", "Kadın", "Erkek", "Belirtmek İstemiyorum")
        spinnerCinsiyet.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cinsiyetler)

        findViewById<ImageButton>(R.id.btnGeri).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvGirisYap).setOnClickListener { finish() }

        btnKayitOl.setOnClickListener { kayitOl() }
    }

    private fun kayitOl() {
        val ad = etAd.text.toString().trim()
        val soyad = etSoyad.text.toString().trim()
        val cinsiyet = if (spinnerCinsiyet.selectedItemPosition > 0) spinnerCinsiyet.selectedItem.toString() else ""
        val sinif = etSinif.text.toString().trim()
        val kullaniciAdi = etKullaniciAdi.text.toString().trim()
        val sifre = etSifre.text.toString().trim()

        if (ad.isEmpty()) { etAd.error = "Ad gerekli"; return }
        if (soyad.isEmpty()) { etSoyad.error = "Soyad gerekli"; return }
        if (cinsiyet.isEmpty()) { Toast.makeText(this, "Cinsiyet seçiniz", Toast.LENGTH_SHORT).show(); return }
        if (sinif.isEmpty()) { etSinif.error = "Sınıf gerekli"; return }
        if (kullaniciAdi.isEmpty()) { etKullaniciAdi.error = "Kullanıcı adı gerekli"; return }
        if (sifre.length < 6) { etSifre.error = "En az 6 karakter"; return }
        val guvenlikAnahtari = etGuvenlikAnahtari.text.toString().trim()
        if (guvenlikAnahtari.length < 4) { etGuvenlikAnahtari.error = "En az 4 karakter"; return }

        progressOverlay.visibility = View.VISIBLE
        btnKayitOl.isEnabled = false

        val cihazParmakIzi = com.libroquest.app.api.CihazBilgisi.parmakIziOlustur(this)
        val request = RegisterRequest(ad, soyad, cinsiyet, sinif, kullaniciAdi, sifre, guvenlikAnahtari, cihazParmakIzi)
        RetrofitClient.apiService.kayitOl(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                progressOverlay.visibility = View.GONE
                btnKayitOl.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@RegisterActivity, "Kayıt başarılı! 100 puan tanımlandı. Giriş yapabilirsiniz.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Kayıt başarısız! Bu kullanıcı adı zaten kullanılıyor olabilir.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                progressOverlay.visibility = View.GONE
                btnKayitOl.isEnabled = true
                Toast.makeText(this@RegisterActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
