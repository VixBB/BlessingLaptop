package com.example.loginmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var auth: FirebaseAuth

    override fun onResume() {
        super.onResume()
        fetchBorrowedLaptops()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        val btnLogout = findViewById<TextView>(R.id.tv_logout)
        val tvUsername = findViewById<TextView>(R.id.tv_username)
        val tvEmail = findViewById<TextView>(R.id.tv_email)

        val currentUser = auth.currentUser
        tvUsername.text = SessionManager.nama ?: currentUser?.displayName ?: "Pengguna"
        tvEmail.text = currentUser?.email ?: "email@example.com"

        btnLogout.setOnClickListener {
            auth.signOut()
            SessionManager.clear() // Clear session data on logout
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        rvPinjam = findViewById(R.id.rv_pinjaman_saya)
        rvPinjam.layoutManager = LinearLayoutManager(this)
        adapter = LaptopAdapter(borrowedLaptops)
        rvPinjam.adapter = adapter

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
        val currentUser = auth.currentUser
        if (currentUser == null) {
            borrowedLaptops.clear()
            adapter.notifyDataSetChanged()
            return
        }

        val uid = currentUser.uid

        laptopCollection
            .whereEqualTo("borrowed", true)
            .whereEqualTo("borrowedBy", uid)
            .get()
            .addOnSuccessListener { documents ->
                borrowedLaptops.clear()
                for (document in documents) {
                    val laptop = document.toObject(Laptop::class.java)
                    borrowedLaptops.add(laptop)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileActivity", "Error getting documents: ", exception)
                Toast.makeText(this, "Failed to load borrowed items. Check logs.", Toast.LENGTH_LONG).show()
            }
    }
}
