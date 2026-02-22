package com.example.loginmenu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button

    // 1. Inisialisasi Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Snackbar.make(loginBtn, "Please fill in both fields!", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Trik: Jika user input "admin", kita anggap emailnya "admin@myapp.com"
            // Karena Firebase Auth wajib menggunakan format email.
            val emailFake = if (username.contains("@")) username else "$username@myapp.com"

            loginKeFirebase(emailFake, password)
        }
    }

    private fun loginKeFirebase(email: String, pass: String) {
        // 2. Proses Login ke Firebase Auth
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        // 3. Jika login sukses, cek Role di Firestore
                        checkUserRole(uid)
                    }
                } else {
                    Log.e("LoginError", "Error: ${task.exception?.message}")
                    Snackbar.make(loginBtn, "❌ Login Gagal: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun checkUserRole(uid: String) {
        // 4. Ambil dokumen berdasarkan UID yang sedang login
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role")

                    if (role == "admin") {
                        // JIKA ADMIN
                        GlobalData.isAdmin = true
                        Snackbar.make(loginBtn, "✅ Welcome Admin!", Snackbar.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeAdmin::class.java))
                    } else {
                        // JIKA USER BIASA
                        GlobalData.isAdmin = false
                        Snackbar.make(loginBtn, "✅ Welcome User!", Snackbar.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                    finish() // Tutup MainActivity agar tidak bisa back ke login
                } else {
                    Snackbar.make(loginBtn, "❌ Data role tidak ditemukan!", Snackbar.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Snackbar.make(loginBtn, "Error Firestore: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
    }

    fun openInstagram(view: View) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://www.instagram.com/smkn1denpasar/")
        startActivity(intent)
    }

    fun openFacebook(view: View) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://m.facebook.com/Smkn1dps/")
        startActivity(intent)
    }
}