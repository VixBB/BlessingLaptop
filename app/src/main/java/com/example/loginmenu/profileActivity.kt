package com.example.loginmenu

import android.annotation.SuppressLint
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
    private lateinit var adapter: BorrowedAdapter
    private val db = FirebaseFirestore.getInstance()
    private val laptopCollection = db.collection("laptops")
    private val borrowedLaptops = mutableListOf<Laptop>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvUsernameTop = findViewById<TextView>(R.id.tv_username)
        val tvEmailTop = findViewById<TextView>(R.id.tv_email)
        val btnLogout = findViewById<TextView>(R.id.tv_logout)

        // Initial fetch from Auth
        val currentUser = FirebaseAuth.getInstance().currentUser
        tvUsernameTop.text = currentUser?.displayName ?: "Pengguna"
        tvEmailTop.text = currentUser?.email ?: "email@example.com"

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Setup RecyclerView
        rvPinjam = findViewById(R.id.rv_pinjaman_saya)
        rvPinjam.layoutManager = LinearLayoutManager(this)
        adapter = BorrowedAdapter(borrowedLaptops)
        rvPinjam.adapter = adapter

        // Fetch dynamic data
        fetchUserData(tvUsernameTop)
        fetchBorrowedLaptops()

        // Setup Navigation
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
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    private fun fetchUserData(tvUsernameTop: TextView) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val username = document.getString("nama") ?: "-"
                    val nis = document.getString("nis") ?: "-"
                    val kelas = document.getString("kelas") ?: "-"

                    tvUsernameTop.text = username
                    findViewById<TextView>(R.id.tv_username_data).text = username
                    findViewById<TextView>(R.id.tv_nis).text = nis
                    findViewById<TextView>(R.id.tv_kelas).text = kelas
                }
            }
            .addOnFailureListener { e ->
                Log.w("ProfileActivity", "Error fetching user data", e)
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchBorrowedLaptops() {
        laptopCollection.whereEqualTo("borrowed", true).get()
            .addOnSuccessListener { documents ->
                borrowedLaptops.clear()
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