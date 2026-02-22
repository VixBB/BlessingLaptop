package com.example.loginmenu

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
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

        // Handle image loading
        if (!laptop.imageData.isNullOrEmpty()) {
            // New method: Decode Base64 string and load bitmap
            try {
                val imageBytes = Base64.decode(laptop.imageData, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                Glide.with(holder.itemView.context)
                    .load(decodedImage)
                    .placeholder(R.drawable.skensa)
                    .into(holder.imgLaptop)
            } catch (e: Exception) {
                holder.imgLaptop.setImageResource(R.drawable.skensa) // Fallback on error
            }
        } else {
            // Fallback for old data: Load from drawable name
            val imageId = holder.itemView.context.resources.getIdentifier(laptop.nama?.replace(" ", "")?.toLowerCase(), "drawable", holder.itemView.context.packageName)
            Glide.with(holder.itemView.context)
                .load(if (imageId != 0) imageId else R.drawable.skensa) // Fallback to placeholder
                .placeholder(R.drawable.skensa)
                .into(holder.imgLaptop)
        }

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
        // Pass image data if available, otherwise pass the old name
        if (!laptop.imageData.isNullOrEmpty()) {
            intent.putExtra("GAMBAR_LAPTOP_DATA", laptop.imageData)
        } else {
            intent.putExtra("GAMBAR_LAPTOP_NAMA", laptop.nama?.replace(" ", "")?.toLowerCase())
        }
        holder.itemView.context.startActivity(intent)
    }

    override fun getItemCount(): Int = listLaptop.size
}