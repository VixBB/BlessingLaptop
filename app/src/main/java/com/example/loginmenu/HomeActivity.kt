package com.example.loginmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var rvLaptop: RecyclerView
    private lateinit var adapter: LaptopAdapter
    private val db = FirebaseFirestore.getInstance()
    private val laptopCollection = db.collection("laptops")
    private val laptopList = mutableListOf<Laptop>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rvLaptop = findViewById(R.id.rv_laptop)
        rvLaptop.layoutManager = LinearLayoutManager(this)
        adapter = LaptopAdapter(laptopList)
        rvLaptop.adapter = adapter

        fetchLaptops()

        //  Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_laptop -> {
                    startActivity(Intent(this, LaptopActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchLaptops() {
        laptopCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val laptop = document.toObject(Laptop::class.java)
                    laptopList.add(laptop)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("HomeActivity", "Error getting documents: ", exception)
            }
    }
}