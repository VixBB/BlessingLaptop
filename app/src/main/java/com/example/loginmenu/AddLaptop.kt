package com.example.loginmenu

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddLaptop : AppCompatActivity() {

    private lateinit var etLaptopName: EditText
    private lateinit var etLaptopDescription: EditText
    private lateinit var etProcessor: EditText
    private lateinit var etGraphics: EditText
    private lateinit var etRam: EditText
    private lateinit var etStorage: EditText
    private lateinit var etScreen: EditText
    private lateinit var etOs: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerImage: Spinner
    private lateinit var btnAddLaptop: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_laptop)

        etLaptopName = findViewById(R.id.et_laptop_name)
        etLaptopDescription = findViewById(R.id.et_laptop_description)
        etProcessor = findViewById(R.id.et_processor)
        etGraphics = findViewById(R.id.et_graphics)
        etRam = findViewById(R.id.et_ram)
        etStorage = findViewById(R.id.et_storage)
        etScreen = findViewById(R.id.et_screen)
        etOs = findViewById(R.id.et_os)
        spinnerCategory = findViewById(R.id.spinner_category)
        spinnerImage = findViewById(R.id.spinner_image)
        btnAddLaptop = findViewById(R.id.btn_add_laptop)

        setupSpinners()

        btnAddLaptop.setOnClickListener { 
            addLaptopToFirestore()
        }
    }

    private fun setupSpinners() {
        // Setup Category Spinner
        val categories = arrayOf("High-End", "Mid-Range", "Low-End")
        val categoryAdapter = ArrayAdapter(this, R.layout.spinner_item_white, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        // Setup Image Spinner
        val imageNames = arrayOf("legionbg", "omen", "rog", "macm5", "lenovo", "tufa15", "airm2", "axiopongo", "victus", "axiohype")
        val imageAdapter = ArrayAdapter(this, R.layout.spinner_item_white, imageNames)
        imageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerImage.adapter = imageAdapter
    }

    private fun addLaptopToFirestore() {
        val name = etLaptopName.text.toString().trim()
        val description = etLaptopDescription.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val imageName = spinnerImage.selectedItem.toString()
        val processor = etProcessor.text.toString().trim()
        val graphics = etGraphics.text.toString().trim()
        val ram = etRam.text.toString().trim()
        val storage = etStorage.text.toString().trim()
        val screen = etScreen.text.toString().trim()
        val os = etOs.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || processor.isEmpty() || graphics.isEmpty() || ram.isEmpty() || storage.isEmpty() || screen.isEmpty() || os.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val newLaptop = Laptop(
            nama = name,
            deskripsi = description,
            category = category,
            url_gambar = imageName, // Storing the drawable name
            borrowed = false,
            peminjamNama = null,
            peminjamNis = null,
            peminjamKelas = null,
            processor = processor,
            graphics = graphics,
            ram = ram,
            storage = storage,
            screen = screen,
            os = os
        )

        db.collection("laptops")
            .add(newLaptop)
            .addOnSuccessListener { 
                Toast.makeText(this, "Laptop berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding laptop: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}