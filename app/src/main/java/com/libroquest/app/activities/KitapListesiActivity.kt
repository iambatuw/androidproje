package com.libroquest.app.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.libroquest.app.R
import com.libroquest.app.adapters.KitapAdapter
import com.libroquest.app.api.RetrofitClient
import com.libroquest.app.api.SessionManager
import com.libroquest.app.models.Kitap
import com.libroquest.app.models.OduncIslem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KitapListesiActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var adapter: KitapAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvBos: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitap_listesi)

        session = SessionManager(this)
        progressBar = findViewById(R.id.progressBar)
        tvBos = findViewById(R.id.tvBos)

        val rv = findViewById<RecyclerView>(R.id.rvKitaplar)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = KitapAdapter()
        rv.adapter = adapter

        findViewById<ImageButton>(R.id.btnGeri).setOnClickListener { finish() }

        val mod = intent.getStringExtra("mod")
        if (mod == "odunc") {
            oduncKitaplariYukle()
        } else {
            kitaplariYukle()
        }
    }

    private fun kitaplariYukle() {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.apiService.kitaplariGetir(session.getToken()).enqueue(object : Callback<List<Kitap>> {
            override fun onResponse(call: Call<List<Kitap>>, response: Response<List<Kitap>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val liste = response.body()!!
                    if (liste.isEmpty()) {
                        tvBos.visibility = View.VISIBLE
                    } else {
                        adapter.guncelle(liste)
                    }
                }
            }
            override fun onFailure(call: Call<List<Kitap>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@KitapListesiActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun oduncKitaplariYukle() {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.apiService.kullaniciOduncGetir(session.getToken(), session.getUserId())
            .enqueue(object : Callback<List<OduncIslem>> {
                override fun onResponse(call: Call<List<OduncIslem>>, response: Response<List<OduncIslem>>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body() != null) {
                        val islemler = response.body()!!.filter { !it.teslimEdildiMi }
                        if (islemler.isEmpty()) {
                            tvBos.visibility = View.VISIBLE
                            tvBos.text = "Ödünç aldığınız kitap bulunmuyor."
                        } else {
                            val kitaplar = islemler.map { islem ->
                                Kitap(
                                    kitapId = islem.kitapId,
                                    kitapAdi = islem.kitapAdi,
                                    yazarAdSoyad = islem.yazarAdSoyad,
                                    turAdi = if (islem.kalanGun > 0) "${islem.kalanGun} gün kaldı" else "${islem.gecikmeGunu} gün gecikme!",
                                    stokAdedi = 0
                                )
                            }
                            adapter.guncelle(kitaplar)
                        }
                    }
                }
                override fun onFailure(call: Call<List<OduncIslem>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@KitapListesiActivity, "Bağlantı hatası", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
