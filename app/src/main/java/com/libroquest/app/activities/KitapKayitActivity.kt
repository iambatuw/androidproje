package com.libroquest.app.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.libroquest.app.R
import com.libroquest.app.api.RetrofitClient
import com.libroquest.app.api.SessionManager
import com.libroquest.app.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KitapKayitActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var spinnerYazar: Spinner
    private lateinit var spinnerTur: Spinner
    private var yazarListesi = listOf<Yazar>()
    private var turListesi = listOf<KitapTuru>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitap_kayit)

        session = SessionManager(this)
        spinnerYazar = findViewById(R.id.spinnerYazar)
        spinnerTur = findViewById(R.id.spinnerTur)

        findViewById<ImageButton>(R.id.btnGeri).setOnClickListener { finish() }

        yazarlariYukle()
        turleriYukle()

        findViewById<MaterialButton>(R.id.btnKaydet).setOnClickListener { kitapKaydet() }
    }

    private fun yazarlariYukle() {
        RetrofitClient.apiService.yazarlariGetir(session.getToken()).enqueue(object : Callback<List<Yazar>> {
            override fun onResponse(call: Call<List<Yazar>>, response: Response<List<Yazar>>) {
                if (response.isSuccessful && response.body() != null) {
                    yazarListesi = response.body()!!
                    val isimler = mutableListOf("Yazar Seçiniz")
                    isimler.addAll(yazarListesi.map { "${it.ad} ${it.soyad}" })
                    spinnerYazar.adapter = ArrayAdapter(this@KitapKayitActivity, android.R.layout.simple_spinner_dropdown_item, isimler)
                }
            }
            override fun onFailure(call: Call<List<Yazar>>, t: Throwable) {
                Toast.makeText(this@KitapKayitActivity, "Yazarlar yüklenemedi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun turleriYukle() {
        RetrofitClient.apiService.turleriGetir(session.getToken()).enqueue(object : Callback<List<KitapTuru>> {
            override fun onResponse(call: Call<List<KitapTuru>>, response: Response<List<KitapTuru>>) {
                if (response.isSuccessful && response.body() != null) {
                    turListesi = response.body()!!
                    val turAdlari = mutableListOf("Tür Seçiniz")
                    turAdlari.addAll(turListesi.map { it.turAdi })
                    spinnerTur.adapter = ArrayAdapter(this@KitapKayitActivity, android.R.layout.simple_spinner_dropdown_item, turAdlari)
                }
            }
            override fun onFailure(call: Call<List<KitapTuru>>, t: Throwable) {
                Toast.makeText(this@KitapKayitActivity, "Türler yüklenemedi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun kitapKaydet() {
        val kitapAdi = findViewById<EditText>(R.id.etKitapAdi).text.toString().trim()
        val isbn = findViewById<EditText>(R.id.etISBN).text.toString().trim()
        val sayfaStr = findViewById<EditText>(R.id.etSayfaSayisi).text.toString().trim()
        val stokStr = findViewById<EditText>(R.id.etStokAdedi).text.toString().trim()

        if (kitapAdi.isEmpty()) { Toast.makeText(this, "Kitap adı gerekli", Toast.LENGTH_SHORT).show(); return }
        if (spinnerYazar.selectedItemPosition == 0) { Toast.makeText(this, "Yazar seçiniz", Toast.LENGTH_SHORT).show(); return }
        if (spinnerTur.selectedItemPosition == 0) { Toast.makeText(this, "Tür seçiniz", Toast.LENGTH_SHORT).show(); return }

        val yazarId = yazarListesi[spinnerYazar.selectedItemPosition - 1].yazarId
        val turId = turListesi[spinnerTur.selectedItemPosition - 1].turId
        val sayfaSayisi = sayfaStr.toIntOrNull() ?: 0
        val stokAdedi = stokStr.toIntOrNull() ?: 1

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val btnKaydet = findViewById<MaterialButton>(R.id.btnKaydet)
        progressBar.visibility = View.VISIBLE
        btnKaydet.isEnabled = false

        val request = KitapEkleRequest(kitapAdi, yazarId, turId, isbn, sayfaSayisi, stokAdedi)
        RetrofitClient.apiService.kitapEkle(session.getToken(), request).enqueue(object : Callback<KitapEkleResponse> {
            override fun onResponse(call: Call<KitapEkleResponse>, response: Response<KitapEkleResponse>) {
                progressBar.visibility = View.GONE
                btnKaydet.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(this@KitapKayitActivity, "Kitap başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@KitapKayitActivity, "Kitap kaydedilemedi", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<KitapEkleResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                btnKaydet.isEnabled = true
                Toast.makeText(this@KitapKayitActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
