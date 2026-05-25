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
import com.libroquest.app.adapters.GecikenAdapter
import com.libroquest.app.api.RetrofitClient
import com.libroquest.app.api.SessionManager
import com.libroquest.app.models.GecikenKitap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GecikenlerActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var adapter: GecikenAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvBos: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gecikenler)

        session = SessionManager(this)
        progressBar = findViewById(R.id.progressBar)
        tvBos = findViewById(R.id.tvBos)

        val rv = findViewById<RecyclerView>(R.id.rvGecikenler)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = GecikenAdapter()
        rv.adapter = adapter

        findViewById<ImageButton>(R.id.btnGeri).setOnClickListener { finish() }

        gecikenleriYukle()
    }

    private fun gecikenleriYukle() {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.apiService.gecikenleriGetir(session.getToken()).enqueue(object : Callback<List<GecikenKitap>> {
            override fun onResponse(call: Call<List<GecikenKitap>>, response: Response<List<GecikenKitap>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val liste = response.body()!!
                    if (liste.isEmpty()) {
                        tvBos.visibility = View.VISIBLE
                    } else {
                        adapter.guncelle(liste)
                    }
                } else {
                    tvBos.visibility = View.VISIBLE
                }
            }
            override fun onFailure(call: Call<List<GecikenKitap>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@GecikenlerActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
