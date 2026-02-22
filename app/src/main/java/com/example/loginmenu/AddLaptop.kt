package com.example.loginmenu

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

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
    private lateinit var btnAddLaptop: Button
    private lateinit var ivLaptopPreview: ImageView
    private lateinit var btnUploadImage: Button

    private var selectedImageBitmap: Bitmap? = null
    private val db = FirebaseFirestore.getInstance()
    private val PICK_IMAGE_REQUEST = 1

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
        btnAddLaptop = findViewById(R.id.btn_add_laptop)
        ivLaptopPreview = findViewById(R.id.iv_laptop_preview)
        btnUploadImage = findViewById(R.id.btn_upload_image)

        setupSpinners()

        btnUploadImage.setOnClickListener {
            openFileChooser()
        }

        btnAddLaptop.setOnClickListener { 
            addLaptopToFirestore()
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            try {
                selectedImageBitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                } else {
                    val source = ImageDecoder.createSource(contentResolver, imageUri)
                    ImageDecoder.decodeBitmap(source)
                }
                // Resize the bitmap
                selectedImageBitmap = resizeBitmap(selectedImageBitmap!!, 800) 
                ivLaptopPreview.setImageBitmap(selectedImageBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupSpinners() {
        val categories = arrayOf("High-End", "Mid-Range", "Low-End")
        val categoryAdapter = ArrayAdapter(this, R.layout.spinner_item_white, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
    }
    
    private fun addLaptopToFirestore() {
        val name = etLaptopName.text.toString().trim()
        val description = etLaptopDescription.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val processor = etProcessor.text.toString().trim()
        val graphics = etGraphics.text.toString().trim()
        val ram = etRam.text.toString().trim()
        val storage = etStorage.text.toString().trim()
        val screen = etScreen.text.toString().trim()
        val os = etOs.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || processor.isEmpty() || graphics.isEmpty() || ram.isEmpty() || storage.isEmpty() || screen.isEmpty() || os.isEmpty() || selectedImageBitmap == null) {
            Toast.makeText(this, "Semua field harus diisi dan gambar harus dipilih", Toast.LENGTH_SHORT).show()
            return
        }
        
        val imageData = bitmapToBase64(selectedImageBitmap!!)

        val newLaptop = Laptop(
            nama = name,
            deskripsi = description,
            category = category,
            imageData = imageData,
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
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding laptop: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
    
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}