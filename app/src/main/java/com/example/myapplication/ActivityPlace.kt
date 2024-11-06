package com.example.myapplication

import android.content.Intent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ActivityPlace : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var restauranteId: String
    private lateinit var comentariosList: MutableList<Comentario>
    private lateinit var adapter: ComentariosAdapter
    private var nombreUsuarioActual: String = "Nombre no disponible" // Valor predeterminado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        db = FirebaseFirestore.getInstance()
        restauranteId = intent.getStringExtra("restaurante_id") ?: ""

        // Configuración de UI existente
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
        obtenerNombreUsuarioDesdeFirestore()

        // Inicializar la lista de comentarios y el adaptador
        comentariosList = mutableListOf()
        adapter = ComentariosAdapter(comentariosList)
        val recyclerViewComentarios = findViewById<RecyclerView>(R.id.recyclerViewComentarios)
        recyclerViewComentarios.layoutManager = LinearLayoutManager(this)
        recyclerViewComentarios.adapter = adapter

        // Cargar los comentarios existentes
        cargarComentarios()

        // Configurar el botón para añadir comentarios
        val editTextComentario = findViewById<EditText>(R.id.textAddComment)
        val botonAñadirComentario = findViewById<Button>(R.id.buttonAddComments)
        botonAñadirComentario.setOnClickListener {
            val comentarioTexto = editTextComentario.text.toString()
            if (comentarioTexto.isNotEmpty()) {
                añadirComentario(comentarioTexto)
                editTextComentario.text.clear()
            } else {
                Toast.makeText(this, "Por favor, escribe un comentario.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para obtener el nombre del usuario desde Firestore
    private fun obtenerNombreUsuarioDesdeFirestore() {
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        if (usuarioActual != null) {
            val userId = usuarioActual.uid

            db.collection("usuarios").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        nombreUsuarioActual = document.getString("nombre") ?: "Nombre no disponible"
                        Log.d("UsuarioActual", "Nombre obtenido: $nombreUsuarioActual")
                    } else {
                        Log.e("UsuarioActual", "Documento de usuario no encontrado")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("UsuarioActual", "Error al obtener el nombre de usuario: ${e.message}")
                }
        } else {
            Log.e("UsuarioActual", "No hay usuario autenticado")
        }
    }

    private fun guardarCalificacion(nuevaCalificacion: Float) {
        val docRef = db.collection("restaurantes").document(restauranteId)
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

    private fun cargarComentarios() {
        db.collection("restaurantes")
            .document(restauranteId)
            .collection("comentarios")
            .orderBy("fecha", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                comentariosList.clear()
                for (document in result) {
                    val comentario = document.toObject(Comentario::class.java)
                    comentariosList.add(comentario)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("ActivityPlace", "Error al cargar comentarios: ${e.message}")
            }
    }

    private fun añadirComentario(comentarioTexto: String) {
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        if (usuarioActual != null) {
            val userId = usuarioActual.uid

            val nuevoComentario = Comentario(
                id = "",
                texto = comentarioTexto,
                autorId = userId,
                autorNombre = nombreUsuarioActual,
                fecha = System.currentTimeMillis()
            )

            db.collection("restaurantes")
                .document(restauranteId)
                .collection("comentarios")
                .add(nuevoComentario)
                .addOnSuccessListener {
                    cargarComentarios()
                }
                .addOnFailureListener { e ->
                    Log.e("ActivityPlace", "Error al añadir comentario: ${e.message}")
                }
        } else {
            Toast.makeText(this, "No estás autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}