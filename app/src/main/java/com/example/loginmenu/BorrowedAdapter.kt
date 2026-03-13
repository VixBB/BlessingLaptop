package com.example.loginmenu

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BorrowedAdapter(private val listLaptop: List<Laptop>) :
    RecyclerView.Adapter<BorrowedAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgLaptop: ImageView = view.findViewById(R.id.img_laptop_pinjam)
        val tvNamaLaptop: TextView = view.findViewById(R.id.tv_nama_pinjam)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pinjam, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laptop = listLaptop[position]
        holder.tvNamaLaptop.text = laptop.nama

        // Handle image loading (consistent with LaptopAdapter)
        if (!laptop.imageData.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(laptop.imageData, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                Glide.with(holder.itemView.context)
                    .load(decodedImage)
                    .placeholder(R.drawable.skensa)
                    .into(holder.imgLaptop)
            } catch (e: Exception) {
                holder.imgLaptop.setImageResource(R.drawable.skensa)
            }
        } else {
            // Fallback for old data using laptop name as drawable name
            val imageId = holder.itemView.context.resources.getIdentifier(
                laptop.nama?.replace(" ", "")?.lowercase(), 
                "drawable", 
                holder.itemView.context.packageName
            )
            Glide.with(holder.itemView.context)
                .load(if (imageId != 0) imageId else R.drawable.skensa)
                .placeholder(R.drawable.skensa)
                .into(holder.imgLaptop)
        }

        // Set click listener to open DetailActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("NAMA_LAPTOP", laptop.nama)
            
            // Pass image info depending on which format is available
            if (!laptop.imageData.isNullOrEmpty()) {
                intent.putExtra("GAMBAR_LAPTOP_DATA", laptop.imageData)
            } else {
                intent.putExtra("GAMBAR_LAPTOP_NAMA", laptop.nama?.replace(" ", "")?.lowercase())
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listLaptop.size
}
