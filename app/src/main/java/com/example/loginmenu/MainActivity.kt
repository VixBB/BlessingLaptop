package com.example.loginmenu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance() // Tambahkan inisialisasi Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.username_input)
        val etPassword = findViewById<EditText>(R.id.password_input)
        val btnLogin = findViewById<Button>(R.id.login_btn)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""

                        // LOGIKA BARU: Ambil data user dari Firestore
                        fetchUserData(userId, email)
                    } else {
                        Toast.makeText(this, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun fetchUserData(userId: String, email: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Simpan data ke SessionManager agar bisa dipakai di DetailActivity
                    SessionManager.userId = userId
                    SessionManager.nama = document.getString("nama")
                    SessionManager.nis = document.getString("nis")
                    SessionManager.kelas = document.getString("kelas")

                    // Tentukan navigasi
                    if (email == "admin@gmail.com") {
                        SessionManager.isAdmin = true
                        startActivity(Intent(this, HomeAdmin::class.java))
                    } else {
                        SessionManager.isAdmin = false
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                    finish()
                } else {
                    // Jika data di koleksi 'users' tidak ada (biasanya untuk admin yang belum didaftarkan di firestore)
                    if (email == "admin@gmail.com") {
                        SessionManager.isAdmin = true
                        SessionManager.nama = "Administrator"
                        startActivity(Intent(this, HomeAdmin::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Data profil tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}