package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ActivityMainPage : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestauranteAdapter
    private lateinit var db: FirebaseFirestore

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        db = FirebaseFirestore.getInstance()

        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            val intent = Intent(this, ActivityLogIn::class.java)
            startActivity(intent)
            finish()
        }

        val userName = intent.getStringExtra("USER_NAME") ?: "Usuario"
        findViewById<TextView>(R.id.textBienvenida).text = "Bienvenido\n$userName !"

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        obtenerRestaurantes()

        findViewById<Button>(R.id.buttonAddPlace).setOnClickListener {
            startActivity(Intent(this, ActivityEditPlace::class.java))
        }

        findViewById<Button>(R.id.buttonRefresh).setOnClickListener {
            recreate()
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
                    val calificaciones = document.get("calificaciones") as? List<Float> ?: emptyList()

                    Restaurante(document.id, nombre, descripcion, imagenUrl, calificaciones)
                }

                adapter = RestauranteAdapter(restaurantes) { restaurante ->
                    val intent = Intent(this, ActivityPlace::class.java).apply {
                        putExtra("restaurante_id", restaurante.id)
                        putExtra("restaurante_nombre", restaurante.nombre)
                        putExtra("restaurante_descripcion", restaurante.descripcion)
                        putExtra("restaurante_imagen_url", restaurante.imagenUrl)
                    }
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e("ActivityMainPage", "Error al obtener los restaurantes: ${e.message}", e)
                Toast.makeText(this, "Error al cargar los restaurantes. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
            }
    }
}

