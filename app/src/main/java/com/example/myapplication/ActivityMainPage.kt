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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class ActivityMainPage : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestauranteAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var restaurantes: List<Restaurante>

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

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
            startActivity(Intent(this, ActivityAddPlace::class.java))
        }

        findViewById<Button>(R.id.buttonRefresh).setOnClickListener {
            recreate()
        }

        findViewById<Button>(R.id.buttonFavoritePlace).setOnClickListener {
            obtenerFavoritos()
        }
    }

    private fun obtenerRestaurantes() {
        db.collection("restaurantes").get()
            .addOnSuccessListener { documents ->
                restaurantes = documents.map { document ->
                    val nombre = document.getString("nombre") ?: ""
                    val descripcion = document.getString("descripcion") ?: ""
                    val imagenUrl = document.getString("imagenUrl") ?: ""
                    val calificaciones = document.get("calificaciones") as? List<Float> ?: emptyList()
                    val creador = document.get("creadorUID") ?: ""

                    Restaurante(document.id, nombre, descripcion, imagenUrl,
                        creador.toString(), calificaciones)
                }

                adapter = RestauranteAdapter(restaurantes) { restaurante ->
                    val user = auth.currentUser
                    if (user != null) {
                        agregarAFavoritos(restaurante, user.uid)
                    }
                }
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e("ActivityMainPage", "Error al obtener los restaurantes: ${e.message}", e)
                Toast.makeText(this, "Error al cargar los restaurantes. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerFavoritos() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val usuarioRef = db.collection("usuarios").document(userId)

            usuarioRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoritosIds = document.get("favoritos") as? List<String> ?: emptyList()

                    // Filtrar los restaurantes favoritos
                    val restaurantesFavoritos = restaurantes.filter { favoritosIds.contains(it.id) }

                    // Actualizar el adaptador con la nueva lista de favoritos
                    adapter = RestauranteAdapter(restaurantesFavoritos) { restaurante ->
                        agregarAFavoritos(restaurante, userId)
                    }
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this, "No tienes restaurantes favoritos.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.e("ActivityMainPage", "Error al obtener los favoritos: ${e.message}", e)
                Toast.makeText(this, "Error al obtener los favoritos.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun agregarAFavoritos(restaurante: Restaurante, userId: String) {
        val usuarioRef = db.collection("usuarios").document(userId)

        usuarioRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val favoritos = document.get("favoritos") as? MutableList<String> ?: mutableListOf()

                if (!favoritos.contains(restaurante.id)) {
                    favoritos.add(restaurante.id)

                    usuarioRef.update("favoritos", favoritos)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Restaurante añadido a favoritos", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("ActivityMainPage", "Error al añadir a favoritos: ${e.message}", e)
                            Toast.makeText(this, "Error al añadir a favoritos", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Este restaurante ya está en tus favoritos", Toast.LENGTH_SHORT).show()
                }
            } else {
                val favoritos = mutableListOf(restaurante.id)

                usuarioRef.set(mapOf("favoritos" to favoritos))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Restaurante añadido a favoritos", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("ActivityMainPage", "Error al añadir a favoritos: ${e.message}", e)
                        Toast.makeText(this, "Error al añadir a favoritos", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener { e ->
            Log.e("ActivityMainPage", "Error al verificar el documento de favoritos: ${e.message}", e)
            Toast.makeText(this, "Error al verificar el documento de favoritos", Toast.LENGTH_SHORT).show()
        }
    }
}
