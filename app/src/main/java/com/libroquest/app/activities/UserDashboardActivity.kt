package com.libroquest.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import com.libroquest.app.R
import com.libroquest.app.api.RetrofitClient
import com.libroquest.app.api.SessionManager
import com.libroquest.app.models.Kullanici
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var tvPuan: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        session = SessionManager(this)

        val tvMerhaba = findViewById<MaterialTextView>(R.id.tvMerhaba)
        tvMerhaba.text = getString(R.string.merhaba, session.getAd())

        tvPuan = findViewById(R.id.tvPuan)
        tvPuan.text = session.getPuan().toString()

        // Ödünç Kitaplarım - Kitap listesine yönlendir (kullanıcı ödünç listesi)
        findViewById<android.view.View>(R.id.btnOduncKitaplarim).setOnClickListener {
            val intent = Intent(this, KitapListesiActivity::class.java)
            intent.putExtra("mod", "odunc")
            startActivity(intent)
        }

        // Kitap Kataloğu
        findViewById<android.view.View>(R.id.btnKitapKatalogu).setOnClickListener {
            startActivity(Intent(this, KitapListesiActivity::class.java))
        }

        // Kitap Türleri
        findViewById<android.view.View>(R.id.btnKitapTurleri).setOnClickListener {
            turlerDialog()
        }

        // Puanlarım
        findViewById<android.view.View>(R.id.btnPuanlarim).setOnClickListener {
            puanBilgiDialog()
        }

        // Profilim
        findViewById<android.view.View>(R.id.btnProfilim).setOnClickListener {
            profilDialog()
        }

        // Çıkış Yap
        findViewById<android.view.View>(R.id.btnCikisYap).setOnClickListener {
            cikisYap()
        }

        findViewById<ImageButton>(R.id.btnCikis).setOnClickListener {
            cikisYap()
        }
    }

    override fun onResume() {
        super.onResume()
        puanGuncelle()
    }

    private fun puanGuncelle() {
        RetrofitClient.apiService.kullaniciGetir(session.getToken(), session.getUserId())
            .enqueue(object : Callback<Kullanici> {
                override fun onResponse(call: Call<Kullanici>, response: Response<Kullanici>) {
                    if (response.isSuccessful && response.body() != null) {
                        val puan = response.body()!!.puan
                        session.puanGuncelle(puan)
                        tvPuan.text = puan.toString()
                    }
                }
                override fun onFailure(call: Call<Kullanici>, t: Throwable) {}
            })
    }

    private fun turlerDialog() {
        RetrofitClient.apiService.turleriGetir(session.getToken())
            .enqueue(object : Callback<List<com.libroquest.app.models.KitapTuru>> {
                override fun onResponse(call: Call<List<com.libroquest.app.models.KitapTuru>>, response: Response<List<com.libroquest.app.models.KitapTuru>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val turler = response.body()!!.map { it.turAdi }.toTypedArray()
                        AlertDialog.Builder(this@UserDashboardActivity)
                            .setTitle("Kitap Türleri")
                            .setItems(turler, null)
                            .setPositiveButton("Tamam", null)
                            .show()
                    }
                }
                override fun onFailure(call: Call<List<com.libroquest.app.models.KitapTuru>>, t: Throwable) {
                    Toast.makeText(this@UserDashboardActivity, "Bağlantı hatası", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun puanBilgiDialog() {
        AlertDialog.Builder(this)
            .setTitle("Puan Bilgisi")
            .setMessage(
                "Mevcut Puanınız: ${session.getPuan()}\n\n" +
                "Puan Sistemi:\n" +
                "• Kayıt olunca 100 puan verilir\n" +
                "• 1-3 gün gecikme: Günlük -5 puan\n" +
                "• 4-7 gün gecikme: Günlük -10 puan\n" +
                "• 7+ gün gecikme: Hesap askıya alınır"
            )
            .setPositiveButton("Tamam", null)
            .show()
    }

    private fun profilDialog() {
        AlertDialog.Builder(this)
            .setTitle("Profil Bilgileri")
            .setMessage(
                "Ad: ${session.getAd()}\n" +
                "Soyad: ${session.getSoyad()}\n" +
                "Kullanıcı Adı: ${session.getKullaniciAdi()}\n" +
                "Cinsiyet: ${session.getCinsiyet()}\n" +
                "Sınıf: ${session.getSinif()}\n" +
                "Puan: ${session.getPuan()}"
            )
            .setPositiveButton("Tamam", null)
            .show()
    }

    private fun cikisYap() {
        session.otumuKapat()
        Toast.makeText(this, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
