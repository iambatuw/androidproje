package com.libroquest.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.libroquest.app.R
import com.libroquest.app.models.Kitap

class KitapAdapter(
    private var kitaplar: List<Kitap> = emptyList()
) : RecyclerView.Adapter<KitapAdapter.KitapViewHolder>() {

    class KitapViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvKitapAdi: TextView = view.findViewById(R.id.tvKitapAdi)
        val tvYazar: TextView = view.findViewById(R.id.tvYazar)
        val tvTur: TextView = view.findViewById(R.id.tvTur)
        val tvStok: TextView = view.findViewById(R.id.tvStok)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kitap, parent, false)
        return KitapViewHolder(view)
    }

    override fun onBindViewHolder(holder: KitapViewHolder, position: Int) {
        val kitap = kitaplar[position]
        holder.tvKitapAdi.text = kitap.kitapAdi
        holder.tvYazar.text = kitap.yazarAdSoyad
        holder.tvTur.text = kitap.turAdi
        holder.tvStok.text = "Stok: ${kitap.stokAdedi}"
    }

    override fun getItemCount() = kitaplar.size

    fun guncelle(yeniListe: List<Kitap>) {
        kitaplar = yeniListe
        notifyDataSetChanged()
    }
}
