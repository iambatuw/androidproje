package com.libroquest.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import com.libroquest.app.R
import com.libroquest.app.api.SessionManager

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        session = SessionManager(this)

        val tvMerhaba = findViewById<MaterialTextView>(R.id.tvMerhaba)
        tvMerhaba.text = getString(R.string.merhaba, session.getAd())

        findViewById<android.view.View>(R.id.btnKitapOduncVer).setOnClickListener {
            startActivity(Intent(this, KitapOduncVerActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btnKitapKayitEt).setOnClickListener {
            startActivity(Intent(this, KitapKayitActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btnYazarKaydi).setOnClickListener {
            startActivity(Intent(this, YazarKayitActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btnKitapListesi).setOnClickListener {
            startActivity(Intent(this, KitapListesiActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btnGecikenler).setOnClickListener {
            startActivity(Intent(this, GecikenlerActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btnUyeYonetimi).setOnClickListener {
            startActivity(Intent(this, UyeYonetimiActivity::class.java))
        }

        val cikisListener = android.view.View.OnClickListener {
            session.otumuKapat()
            Toast.makeText(this, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        findViewById<ImageButton>(R.id.btnCikis).setOnClickListener(cikisListener)
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCikisAlt).setOnClickListener(cikisListener)
    }
}
