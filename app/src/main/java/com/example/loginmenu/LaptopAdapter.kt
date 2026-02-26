package com.example.loginmenu

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LaptopAdapter(private val listLaptop: List<Laptop>) :
    RecyclerView.Adapter<LaptopAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgLaptop: ImageView = view.findViewById(R.id.img_laptop)
        val tvNamaLaptop: TextView = view.findViewById(R.id.tv_nama_laptop)
        val btnDetail: Button = view.findViewById(R.id.btn_detail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_laptop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laptop = listLaptop[position]
        holder.tvNamaLaptop.text = laptop.nama

        // Get resource ID from image name
        val imageName = laptop.url_gambar
        val imageId = holder.itemView.context.resources.getIdentifier(imageName, "drawable", holder.itemView.context.packageName)

        Glide.with(holder.itemView.context)
            .load(if (imageId != 0) imageId else R.drawable.skensa) // Fallback to placeholder
            .placeholder(R.drawable.skensa)
            .into(holder.imgLaptop)

        // --- LOGIC SINKRONISASI TOMBOL ---
        if (laptop.borrowed) {
            holder.btnDetail.text = "Dipinjam"
            holder.btnDetail.setBackgroundColor(Color.GRAY)
            holder.btnDetail.isEnabled = false
        } else {
            holder.btnDetail.text = "Detail"
            holder.btnDetail.setBackgroundColor(Color.parseColor("#002052"))
            holder.btnDetail.isEnabled = true
        }

        // Klik tombol Detail
        holder.btnDetail.setOnClickListener {
            bukaDetail(holder, laptop)
        }

        // Klik area item (seluruh kartu)
        holder.itemView.setOnClickListener {
            bukaDetail(holder, laptop)
        }
    }

    private fun bukaDetail(holder: ViewHolder, laptop: Laptop) {
        val intent = Intent(holder.itemView.context, DetailActivity::class.java)
        intent.putExtra("NAMA_LAPTOP", laptop.nama)
        intent.putExtra("GAMBAR_LAPTOP_NAMA", laptop.url_gambar) // Pass the image name
        holder.itemView.context.startActivity(intent)
    }

    override fun getItemCount(): Int = listLaptop.size
}