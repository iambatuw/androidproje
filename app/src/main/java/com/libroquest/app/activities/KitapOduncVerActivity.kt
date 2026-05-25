package com.libroquest.app.activities

import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*

class KitapOduncVerActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var spinnerUye: Spinner
    private lateinit var spinnerKitap: Spinner
    private lateinit var btnTarihSec: MaterialButton
    private var kullaniciListesi = listOf<Kullanici>()
    private var kitapListesi = listOf<Kitap>()
    private var secilenTarih: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitap_odunc_ver)

        session = SessionManager(this)
        spinnerUye = findViewById(R.id.spinnerUye)
        spinnerKitap = findViewById(R.id.spinnerKitap)
        btnTarihSec = findViewById(R.id.btnTarihSec)

        findViewById<ImageButton>(R.id.btnGeri).setOnClickListener { finish() }

        uyeleriYukle()
        kitaplariYukle()

        btnTarihSec.setOnClickListener { tarihSec() }
        findViewById<MaterialButton>(R.id.btnOduncVer).setOnClickListener { oduncVer() }
    }

    private fun uyeleriYukle() {
        RetrofitClient.apiService.kullanicilariGetir(session.getToken()).enqueue(object : Callback<List<Kullanici>> {
            override fun onResponse(call: Call<List<Kullanici>>, response: Response<List<Kullanici>>) {
                if (response.isSuccessful && response.body() != null) {
                    kullaniciListesi = response.body()!!.filter { it.rol != "admin" }
                    val isimler = mutableListOf("Üye Seçiniz")
                    isimler.addAll(kullaniciListesi.map { "${it.ad} ${it.soyad} (${it.sinif})" })
                    spinnerUye.adapter = ArrayAdapter(this@KitapOduncVerActivity, android.R.layout.simple_spinner_dropdown_item, isimler)
                }
            }
            override fun onFailure(call: Call<List<Kullanici>>, t: Throwable) {
                Toast.makeText(this@KitapOduncVerActivity, "Üyeler yüklenemedi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun kitaplariYukle() {
        RetrofitClient.apiService.kitaplariGetir(session.getToken()).enqueue(object : Callback<List<Kitap>> {
            override fun onResponse(call: Call<List<Kitap>>, response: Response<List<Kitap>>) {
                if (response.isSuccessful && response.body() != null) {
                    kitapListesi = response.body()!!
                    val kitaplar = mutableListOf("Kitap Seçiniz")
                    kitaplar.addAll(kitapListesi.map { "${it.kitapAdi} - ${it.yazarAdSoyad}" })
                    spinnerKitap.adapter = ArrayAdapter(this@KitapOduncVerActivity, android.R.layout.simple_spinner_dropdown_item, kitaplar)
                }
            }
            override fun onFailure(call: Call<List<Kitap>>, t: Throwable) {
                Toast.makeText(this@KitapOduncVerActivity, "Kitaplar yüklenemedi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun tarihSec() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, 14)
        DatePickerDialog(this, { _, year, month, day ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displaySdf = SimpleDateFormat("dd MMMM yyyy", Locale("tr"))
            cal.set(year, month, day)
            secilenTarih = sdf.format(cal.time)
            btnTarihSec.text = displaySdf.format(cal.time)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun oduncVer() {
        if (spinnerUye.selectedItemPosition == 0) { Toast.makeText(this, "Üye seçiniz", Toast.LENGTH_SHORT).show(); return }
        if (spinnerKitap.selectedItemPosition == 0) { Toast.makeText(this, "Kitap seçiniz", Toast.LENGTH_SHORT).show(); return }
        if (secilenTarih.isEmpty()) { Toast.makeText(this, "Teslim tarihi seçiniz", Toast.LENGTH_SHORT).show(); return }

        val kullaniciId = kullaniciListesi[spinnerUye.selectedItemPosition - 1].kullaniciId
        val kitapId = kitapListesi[spinnerKitap.selectedItemPosition - 1].kitapId

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val btnOduncVer = findViewById<MaterialButton>(R.id.btnOduncVer)
        progressBar.visibility = View.VISIBLE
        btnOduncVer.isEnabled = false

        val request = OduncVerRequest(kullaniciId, kitapId, secilenTarih)
        RetrofitClient.apiService.oduncVer(session.getToken(), request).enqueue(object : Callback<OduncVerResponse> {
            override fun onResponse(call: Call<OduncVerResponse>, response: Response<OduncVerResponse>) {
                progressBar.visibility = View.GONE
                btnOduncVer.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(this@KitapOduncVerActivity, "Kitap ödünç verildi!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@KitapOduncVerActivity, "İşlem başarısız (stok yetersiz olabilir)", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<OduncVerResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                btnOduncVer.isEnabled = true
                Toast.makeText(this@KitapOduncVerActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
