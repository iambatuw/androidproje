package com.libroquest.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.libroquest.app.R
import com.libroquest.app.models.Yazar

class YazarAdapter(
    private var yazarlar: List<Yazar> = emptyList(),
    private val onSil: ((Yazar) -> Unit)? = null
) : RecyclerView.Adapter<YazarAdapter.YazarViewHolder>() {

    class YazarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvYazarAdSoyad: TextView = view.findViewById(R.id.tvYazarAdSoyad)
        val btnSil: ImageButton = view.findViewById(R.id.btnSil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YazarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_yazar, parent, false)
        return YazarViewHolder(view)
    }

    override fun onBindViewHolder(holder: YazarViewHolder, position: Int) {
        val yazar = yazarlar[position]
        holder.tvYazarAdSoyad.text = "${yazar.ad} ${yazar.soyad}"
        holder.btnSil.setOnClickListener { onSil?.invoke(yazar) }
    }

    override fun getItemCount() = yazarlar.size

    fun guncelle(yeniListe: List<Yazar>) {
        yazarlar = yeniListe
        notifyDataSetChanged()
    }
}
