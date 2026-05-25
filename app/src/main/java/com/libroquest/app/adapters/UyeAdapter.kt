package com.libroquest.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.libroquest.app.R
import com.libroquest.app.models.Kullanici

class UyeAdapter(
    private var uyeler: List<Kullanici> = emptyList()
) : RecyclerView.Adapter<UyeAdapter.UyeViewHolder>() {

    class UyeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAdSoyad: TextView = view.findViewById(R.id.tvAdSoyad)
        val tvDetay: TextView = view.findViewById(R.id.tvDetay)
        val tvPuan: TextView = view.findViewById(R.id.tvPuan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UyeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_uye, parent, false)
        return UyeViewHolder(view)
    }

    override fun onBindViewHolder(holder: UyeViewHolder, position: Int) {
        val uye = uyeler[position]
        holder.tvAdSoyad.text = "${uye.ad} ${uye.soyad}"
        holder.tvDetay.text = "${uye.sinif} • ${uye.cinsiyet} • @${uye.kullaniciAdi}"
        holder.tvPuan.text = "${uye.puan}"
    }

    override fun getItemCount() = uyeler.size

    fun guncelle(yeniListe: List<Kullanici>) {
        uyeler = yeniListe
        notifyDataSetChanged()
    }
}
