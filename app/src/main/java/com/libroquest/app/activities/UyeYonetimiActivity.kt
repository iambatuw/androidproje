package com.libroquest.app.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.libroquest.app.R
import com.libroquest.app.adapters.UyeAdapter
import com.libroquest.app.api.RetrofitClient
import com.libroquest.app.api.SessionManager
import com.libroquest.app.models.Kullanici
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UyeYonetimiActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var adapter: UyeAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uye_yonetimi)

        session = SessionManager(this)
        progressBar = findViewById(R.id.progressBar)

        val rv = findViewById<RecyclerView>(R.id.rvUyeler)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = UyeAdapter()
        rv.adapter = adapter

        findViewById<ImageButton>(R.id.btnGeri).setOnClickListener { finish() }

        uyeleriYukle()
    }

    private fun uyeleriYukle() {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.apiService.kullanicilariGetir(session.getToken()).enqueue(object : Callback<List<Kullanici>> {
            override fun onResponse(call: Call<List<Kullanici>>, response: Response<List<Kullanici>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    adapter.guncelle(response.body()!!)
                } else {
                    Toast.makeText(this@UyeYonetimiActivity, "Üyeler yüklenemedi", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Kullanici>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@UyeYonetimiActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
