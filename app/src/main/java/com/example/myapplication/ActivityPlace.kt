package com.example.myapplication

import android.content.Intent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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
            // Obtener los datos del restaurante desde el Intent
        val nombre = intent.getStringExtra("restaurante_nombre") ?: ""
        val descripcion = intent.getStringExtra("restaurante_descripcion") ?: ""
        val imagenUrl = intent.getStringExtra("restaurante_imagen_url") ?: ""

            // Enlazar los elementos de la interfaz de usuario con las variables de Kotlin
        val tvNombre = findViewById<TextView>(R.id.textTittlePlace)
        val tvDescripcion = findViewById<TextView>(R.id.placeDescription)
        val ivImagen = findViewById<ImageView>(R.id.imagePlace)

            // Asignar los datos a los elementos de la interfaz
        tvNombre.text = nombre
        tvDescripcion.text = descripcion

            // Cargar la imagen usando Glide
        Glide.with(this)
            .load(imagenUrl)
            .into(ivImagen)

        }
    }