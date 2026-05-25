package com.libroquest.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.libroquest.app.R
import com.libroquest.app.models.GecikenKitap
import java.text.SimpleDateFormat
import java.util.Locale

class GecikenAdapter(
    private var gecikenler: List<GecikenKitap> = emptyList()
) : RecyclerView.Adapter<GecikenAdapter.GecikenViewHolder>() {

    class GecikenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvKitapAdi: TextView = view.findViewById(R.id.tvKitapAdi)
        val tvUyeAdi: TextView = view.findViewById(R.id.tvUyeAdi)
        val tvSinif: TextView = view.findViewById(R.id.tvSinif)
        val tvGecikme: TextView = view.findViewById(R.id.tvGecikme)
        val tvPuanDususu: TextView = view.findViewById(R.id.tvPuanDususu)
        val tvTeslimTarihi: TextView = view.findViewById(R.id.tvTeslimTarihi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GecikenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_geciken, parent, false)
        return GecikenViewHolder(view)
    }

    override fun onBindViewHolder(holder: GecikenViewHolder, position: Int) {
        val geciken = gecikenler[position]
        holder.tvKitapAdi.text = geciken.kitapAdi
        holder.tvUyeAdi.text = geciken.uyeAdSoyad
        holder.tvSinif.text = "Sınıf: ${geciken.sinif} • ${geciken.cinsiyet} • Puan: ${geciken.puan}"
        holder.tvGecikme.text = "${geciken.gecikmeGunu} Gün Gecikme"
        holder.tvPuanDususu.text = "↓ -${geciken.puanDususu} Puan"

        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("tr"))
            val date = inputFormat.parse(geciken.teslimTarihi)
            holder.tvTeslimTarihi.text = "Teslim: ${outputFormat.format(date!!)}"
        } catch (e: Exception) {
            holder.tvTeslimTarihi.text = "Teslim: ${geciken.teslimTarihi}"
        }
    }

    override fun getItemCount() = gecikenler.size

    fun guncelle(yeniListe: List<GecikenKitap>) {
        gecikenler = yeniListe
        notifyDataSetChanged()
    }
}
