package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ActivityMainPage : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestauranteAdapter // Adapter for restaurants

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        // Handle back button click (assuming logic exists in ActivityLogIn)
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, ActivityLogIn::class.java)
            startActivity(intent)
            finish()
        }

        // Welcome message
        val userName = intent.getStringExtra("USER_NAME")
        val welcomeTextView = findViewById<TextView>(R.id.textView5)
        welcomeTextView.text = "Bienvenido\n$userName !"

        // Configure RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load restaurants from Firebase
        obtenerRestaurantes()

        // Handle add card button click (assuming logic exists in ActivityEditPlace)
        val addCardButton = findViewById<Button>(R.id.buttonAddPlace)
        addCardButton.setOnClickListener {
            val intent = Intent(this, ActivityEditPlace::class.java)
            startActivity(intent)
        }
    }

    private fun obtenerRestaurantes() {
        val db = FirebaseFirestore.getInstance()
        db.collection("restaurantes").get()
            .addOnSuccessListener { documents ->
                val restaurantes = documents.map { document ->
                    val nombre = document.getString("nombre") ?: ""
                    val descripcion = document.getString("descripcion") ?: ""
                    val imagenUrl = document.getString("imagenUrl") ?: ""
                    Restaurante(nombre, descripcion, imagenUrl) // Ensure Restaurante class exists
                }

                // Create and set adapter to RecyclerView with click handling
                adapter = RestauranteAdapter(restaurantes) { restaurante ->
                    // Now redirect to ActivityPlace when a restaurant is clicked
                    val intent = Intent(this, ActivityPlace::class.java)
                    intent.putExtra("restaurante_nombre", restaurante.nombre)
                    intent.putExtra("restaurante_descripcion", restaurante.descripcion)
                    intent.putExtra("restaurante_imagen_url", restaurante.imagenUrl)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e("ActivityMainPage", "Error al obtener los restaurantes: ${e.message}")
            }
    }
}

