package com.example.myapplication

import android.content.Intent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ActivityPlace : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var restauranteId: String // ID del restaurante en Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        db = FirebaseFirestore.getInstance()
        restauranteId = intent.getStringExtra("restaurante_id") ?: ""

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener { finish() }

        val ratingBar = findViewById<RatingBar>(R.id.estrellasCalificacion)
        val nombre = intent.getStringExtra("restaurante_nombre") ?: ""
        val descripcion = intent.getStringExtra("restaurante_descripcion") ?: ""
        val imagenUrl = intent.getStringExtra("restaurante_imagen_url") ?: ""

        val tvNombre = findViewById<TextView>(R.id.textTittlePlace)
        val tvDescripcion = findViewById<TextView>(R.id.placeDescription)
        val ivImagen = findViewById<ImageView>(R.id.imagePlace)

        tvNombre.text = nombre
        tvDescripcion.text = descripcion

        Glide.with(this).load(imagenUrl).into(ivImagen)

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            guardarCalificacion(rating)
        }

        obtenerCalificacionPromedio()
    }

    private fun guardarCalificacion(nuevaCalificacion: Float) {
        val docRef = db.collection("restaurantes").document(restauranteId)

        // Añade la nueva calificación al campo "calificaciones" del restaurante
        docRef.update("calificaciones", FieldValue.arrayUnion(nuevaCalificacion))
            .addOnSuccessListener {
                Toast.makeText(this, "Calificación agregada con éxito", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al agregar calificación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerCalificacionPromedio() {
        val docRef = db.collection("restaurantes").document(restauranteId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val calificaciones = document.get("calificaciones") as? List<Double> ?: emptyList()
                    if (calificaciones.isNotEmpty()) {
                        val promedio = calificaciones.average().toFloat()
                        val ratingBar = findViewById<RatingBar>(R.id.estrellasCalificacion)
                        ratingBar.rating = promedio
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ActivityPlace", "Error al obtener el promedio de calificaciones: ${e.message}")
            }
    }
}
