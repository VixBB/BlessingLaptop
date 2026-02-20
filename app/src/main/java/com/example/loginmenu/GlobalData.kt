package com.example.loginmenu

import com.google.firebase.firestore.IgnoreExtraProperties

// Model Laptop
@IgnoreExtraProperties
data class Laptop(
    val nama: String? = null,
    val url_gambar: String? = null,
    val deskripsi: String? = null,
    val category: String? = null,
    var borrowed: Boolean = false,
    var peminjamNama: String? = null,
    var peminjamNis: String? = null,
    var peminjamKelas: String? = null,

    // Data untuk spek
    val processor: String? = null,
    val graphics: String? = null,
    val ram: String? = null,
    val storage: String? = null,
    val screen: String? = null,
    val os: String? = null
)
