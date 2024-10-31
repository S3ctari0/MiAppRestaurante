package com.example.myapplication

import android.content.Intent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Bundle
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ActivityPlace : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
        val ratingbar = findViewById<RatingBar>(R.id.estrellasCalificacion)
        val nombre = intent.getStringExtra("restaurante_nombre") ?: ""
        val descripcion = intent.getStringExtra("restaurante_descripcion") ?: ""
        val imagenUrl = intent.getStringExtra("restaurante_imagen_url") ?: ""

        val tvNombre = findViewById<TextView>(R.id.textTittlePlace)
        val tvDescripcion = findViewById<TextView>(R.id.placeDescription)
        val ivImagen = findViewById<ImageView>(R.id.imagePlace)

        tvNombre.text = nombre
        tvDescripcion.text = descripcion

        Glide.with(this)
            .load(imagenUrl)
            .into(ivImagen)

        }
    }