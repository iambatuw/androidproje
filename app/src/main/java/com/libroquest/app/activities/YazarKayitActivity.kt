package com.libroquest.app.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.libroquest.app.R
import com.libroquest.app.adapters.YazarAdapter
import com.libroquest.app.api.RetrofitClient
import com.libroquest.app.api.SessionManager
import com.libroquest.app.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YazarKayitActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var adapter: YazarAdapter
    private lateinit var rvYazarlar: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yazar_kayit)

        session = SessionManager(this)
        rvYazarlar = findViewById(R.id.rvYazarlar)
        rvYazarlar.layoutManager = LinearLayoutManager(this)

        adapter = YazarAdapter(emptyList()) { yazar -> yazarSil(yazar) }
        rvYazarlar.adapter = adapter

        findViewById<ImageButton>(R.id.btnGeri).setOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnKaydet).setOnClickListener { yazarKaydet() }

        yazarlariYukle()
    }

    private fun yazarlariYukle() {
        RetrofitClient.apiService.yazarlariGetir(session.getToken()).enqueue(object : Callback<List<Yazar>> {
            override fun onResponse(call: Call<List<Yazar>>, response: Response<List<Yazar>>) {
                if (response.isSuccessful && response.body() != null) {
                    adapter.guncelle(response.body()!!)
                }
            }
            override fun onFailure(call: Call<List<Yazar>>, t: Throwable) {
                Toast.makeText(this@YazarKayitActivity, "Bağlantı hatası", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun yazarKaydet() {
        val ad = findViewById<EditText>(R.id.etYazarAd).text.toString().trim()
        val soyad = findViewById<EditText>(R.id.etYazarSoyad).text.toString().trim()

        if (ad.isEmpty()) { Toast.makeText(this, "Yazar adı gerekli", Toast.LENGTH_SHORT).show(); return }
        if (soyad.isEmpty()) { Toast.makeText(this, "Yazar soyadı gerekli", Toast.LENGTH_SHORT).show(); return }

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        RetrofitClient.apiService.yazarEkle(session.getToken(), YazarEkleRequest(ad, soyad))
            .enqueue(object : Callback<YazarEkleResponse> {
                override fun onResponse(call: Call<YazarEkleResponse>, response: Response<YazarEkleResponse>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        Toast.makeText(this@YazarKayitActivity, "Yazar eklendi!", Toast.LENGTH_SHORT).show()
                        findViewById<EditText>(R.id.etYazarAd).text.clear()
                        findViewById<EditText>(R.id.etYazarSoyad).text.clear()
                        yazarlariYukle()
                    } else {
                        Toast.makeText(this@YazarKayitActivity, "Yazar eklenemedi", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<YazarEkleResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@YazarKayitActivity, "Bağlantı hatası", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun yazarSil(yazar: Yazar) {
        AlertDialog.Builder(this)
            .setTitle("Yazar Sil")
            .setMessage("${yazar.ad} ${yazar.soyad} silinecek. Emin misiniz?")
            .setPositiveButton("Sil") { _, _ ->
                RetrofitClient.apiService.yazarSil(session.getToken(), yazar.yazarId)
                    .enqueue(object : Callback<GenericResponse> {
                        override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@YazarKayitActivity, "Yazar silindi", Toast.LENGTH_SHORT).show()
                                yazarlariYukle()
                            } else {
                                Toast.makeText(this@YazarKayitActivity, "Silinemedi (kitaplara bağlı olabilir)", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                            Toast.makeText(this@YazarKayitActivity, "Bağlantı hatası", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("İptal", null)
            .show()
    }
}
