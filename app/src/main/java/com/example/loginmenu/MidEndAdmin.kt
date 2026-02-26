package com.example.loginmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class MidEndAdmin : AppCompatActivity() {

    private lateinit var rvMidEnd: RecyclerView
    private lateinit var adapter: LaptopAdapter
    private val db = FirebaseFirestore.getInstance()
    private val laptopCollection = db.collection("laptops")
    private val midEndLaptops = mutableListOf<Laptop>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.laptop_mid_class_admin)

        val btnBack: ImageButton = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        val fabAdd: FloatingActionButton = findViewById(R.id.fab_add)
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddLaptop::class.java))
        }

        rvMidEnd = findViewById(R.id.rv_mid_end)
        rvMidEnd.layoutManager = GridLayoutManager(this, 2)
        adapter = LaptopAdapter(midEndLaptops)
        rvMidEnd.adapter = adapter

        fetchMidEndLaptops()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_laptop

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeAdmin::class.java))
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                    true
                }
                R.id.nav_laptop -> {
                    startActivity(Intent(this, LaptopActivityAdmin::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileAdmin::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchMidEndLaptops() {
        laptopCollection.whereEqualTo("category", "Mid-Range").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val laptop = document.toObject(Laptop::class.java)
                    midEndLaptops.add(laptop)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("MidEndAdmin", "Error getting documents: ", exception)
            }
    }
}