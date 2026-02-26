package com.example.loginmenu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.username_input)
        val etPassword = findViewById<EditText>(R.id.password_input)
        val btnLogin = findViewById<Button>(R.id.login_btn)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Cek apakah ini akun admin
                        if (email == "admin@gmail.com") {
                            SessionManager.isAdmin = true
                            val intent = Intent(this, HomeAdmin::class.java)
                            startActivity(intent)
                        } else {
                            SessionManager.isAdmin = false
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                        finish() // Selesaikan MainActivity agar tidak bisa kembali
                    } else {
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // The original activity_main.xml does not have a register button with R.id.btn_register.
        // If you add one, you can re-introduce the following lines:
        /*
        val btnRegister = findViewById<Button>(R.id.btn_register)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        */
    }
}