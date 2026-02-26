package com.example.loginmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var rvPinjam: RecyclerView
    private lateinit var adapter: LaptopAdapter
    private val db = FirebaseFirestore.getInstance()
    private val laptopCollection = db.collection("laptops")
    private val borrowedLaptops = mutableListOf<Laptop>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnLogout = findViewById<TextView>(R.id.tv_logout)
        val tvUsername = findViewById<TextView>(R.id.tv_username)
        val tvEmail = findViewById<TextView>(R.id.tv_email)

        val currentUser = FirebaseAuth.getInstance().currentUser
        tvUsername.text = currentUser?.displayName ?: "Pengguna"
        tvEmail.text = currentUser?.email ?: "email@example.com"

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        rvPinjam = findViewById(R.id.rv_pinjaman_saya)
        rvPinjam.layoutManager = LinearLayoutManager(this)
        adapter = LaptopAdapter(borrowedLaptops)
        rvPinjam.adapter = adapter

        fetchBorrowedLaptops()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                    true
                }
                R.id.nav_laptop -> {
                    startActivity(Intent(this, LaptopActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchBorrowedLaptops() {
        // This is a simplified query. In a real app, you'd filter by the current user.
        laptopCollection.whereEqualTo("borrowed", true).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val laptop = document.toObject(Laptop::class.java)
                    borrowedLaptops.add(laptop)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("ProfileActivity", "Error getting documents: ", exception)
            }
    }
}