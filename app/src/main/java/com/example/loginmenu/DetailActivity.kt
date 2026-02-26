package com.example.loginmenu

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var currentLaptop: Laptop? = null
    private var laptopDocumentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        val tvNama = findViewById<TextView>(R.id.tv_detail_nama)
        val imgDetail = findViewById<ImageView>(R.id.img_detail_laptop)
        val btnPinjam = findViewById<Button>(R.id.btn_pinjam)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val namaLaptop = intent.getStringExtra("NAMA_LAPTOP") ?: ""
        val gambarNama = intent.getStringExtra("GAMBAR_LAPTOP_NAMA")

        tvNama.text = namaLaptop

        // Get resource ID from image name
        val imageId = resources.getIdentifier(gambarNama, "drawable", packageName)
        Glide.with(this)
            .load(if (imageId != 0) imageId else R.drawable.skensa) // Fallback to placeholder
            .placeholder(R.drawable.skensa)
            .into(imgDetail)

        fetchLaptopDetails(namaLaptop, btnPinjam)

        // Setup Bottom Nav
        bottomNav.selectedItemId = R.id.nav_laptop
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val activityClass = if (SessionManager.isAdmin) HomeAdmin::class.java else HomeActivity::class.java
                    startActivity(Intent(this, activityClass))
                    finish()
                    true
                }
                R.id.nav_laptop -> {
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    val activityClass = if (SessionManager.isAdmin) ProfileAdmin::class.java else ProfileActivity::class.java
                    startActivity(Intent(this, activityClass))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchLaptopDetails(namaLaptop: String, btnPinjam: Button) {
        db.collection("laptops").whereEqualTo("nama", namaLaptop).limit(1).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    laptopDocumentId = document.id
                    currentLaptop = document.toObject(Laptop::class.java)
                    updateUi(btnPinjam)
                } else {
                    Toast.makeText(this, "Laptop tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("DetailActivity", "Error getting documents: ", exception)
                Toast.makeText(this, "Gagal memuat data laptop", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUi(btnPinjam: Button) {
        currentLaptop?.let { laptop ->
            if (SessionManager.isAdmin) {
                btnPinjam.text = "Detail Peminjam"
                btnPinjam.setBackgroundColor(Color.parseColor("#002052"))
                btnPinjam.setOnClickListener {
                    if (laptop.borrowed) {
                        showBorrowerDetailDialog()
                    } else {
                        Toast.makeText(this, "Laptop ini belum ada yang meminjam", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                if (laptop.borrowed) {
                    btnPinjam.setBackgroundColor(Color.GRAY)
                    btnPinjam.text = "Laptop Tidak Tersedia"
                    btnPinjam.setOnClickListener { showFailedDialog() }
                } else {
                    btnPinjam.setBackgroundColor(Color.parseColor("#00AD00"))
                    btnPinjam.text = "Pinjam Sekarang"
                    btnPinjam.setOnClickListener { showSuccessDialog() }
                }
            }
        }
    }

    private fun showBorrowerDetailDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_detail_peminjam, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvNama = dialogView.findViewById<TextView>(R.id.tv_dialog_peminjam_nama)
        val tvNis = dialogView.findViewById<TextView>(R.id.tv_dialog_peminjam_nis)
        val tvKelas = dialogView.findViewById<TextView>(R.id.tv_dialog_peminjam_kelas)
        val btnKembalikan = dialogView.findViewById<Button>(R.id.btn_dialog_kembalikan)
        val btnClose = dialogView.findViewById<Button>(R.id.btn_dialog_close)

        currentLaptop?.let {
            tvNama.text = it.peminjamNama
            tvNis.text = it.peminjamNis
            tvKelas.text = it.peminjamKelas
        }

        btnKembalikan.setOnClickListener {
            laptopDocumentId?.let {
                db.collection("laptops").document(it)
                    .update(mapOf(
                        "borrowed" to false,
                        "peminjamNama" to "",
                        "peminjamNis" to "",
                        "peminjamKelas" to ""
                    ))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Status laptop telah dikembalikan", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        recreate()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal mengembalikan: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun showSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnOk = dialogView.findViewById<Button>(R.id.btn_dialog_ok)
        btnOk?.setOnClickListener {
            laptopDocumentId?.let {
                db.collection("laptops").document(it)
                    .update(mapOf(
                        "borrowed" to true,
                        "peminjamNama" to "User Student", // Placeholder
                        "peminjamNis" to "12345678",      // Placeholder
                        "peminjamKelas" to "XI RPL 2"   // Placeholder
                    ))
                    .addOnSuccessListener {
                        dialog.dismiss()
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal meminjam: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialog.show()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun showFailedDialog() {
        try {
            val dialogView = layoutInflater.inflate(R.layout.dialog_failed, null)
            val builder = AlertDialog.Builder(this)
            builder.setView(dialogView)

            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val btnClose = dialogView.findViewById<Button>(R.id.btn_dialog_close)
            btnClose?.setOnClickListener { dialog.dismiss() }

            dialog.show()
            val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        } catch (e: Exception) {
            Toast.makeText(this, "Laptop tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }
}