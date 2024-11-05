package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ActivityAddPlace : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_place)
        FirebaseApp.initializeApp(this)

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()

        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            uri?.let {
                val imageView = findViewById<ImageView>(R.id.imagePreview)
                imageView.setImageURI(it)
                Log.d("ActivityEditPlace", "URI de la imagen seleccionada: $uri")
            }
        }

        findViewById<Button>(R.id.button_select_image).setOnClickListener {
            getContent.launch("image/*")
        }

        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            val nombre = findViewById<EditText>(R.id.editTextName).text.toString()
            val descripcion = findViewById<EditText>(R.id.editTextDescription).text.toString()

            if (nombre.isEmpty() || descripcion.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Por favor, completa todos los campos y selecciona una imagen.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            subirImagen(imageUri!!)
        }
    }

    private fun subirImagen(uri: Uri) {
        val imageName = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
        val imageRef = storageRef.child("images/$imageName")

        Log.d("ActivityEditPlace", "Subiendo imagen a: $imageRef")

        val mimeType = contentResolver.getType(uri)
        if (mimeType != "image/jpeg" && mimeType != "image/png") {
            Toast.makeText(this, "Por favor, selecciona una imagen en formato JPG o PNG.", Toast.LENGTH_SHORT).show()
            return
        }

        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                Log.d("ActivityEditPlace", "Imagen subida con éxito.")
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val restaurante = obtenerDatosRestaurante().copy(imagenUrl = downloadUri.toString())
                    guardarRestauranteEnFirestore(restaurante)
                }
            }
            .addOnFailureListener { e ->
                Log.e("ActivityEditPlace", "Error al subir la imagen: ${e.message}", e)
                Toast.makeText(this, "Error al subir la imagen. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarRestauranteEnFirestore(restaurante: Restaurante) {
        val docRef = db.collection("restaurantes").document() // Crea un nuevo documento

        val usuarioUID = auth.currentUser?.uid

        if (usuarioUID != null) {
            docRef.set(restaurante.copy(id = docRef.id, creadorUID = usuarioUID))
                .addOnSuccessListener {
                    Toast.makeText(this, "Restaurante agregado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al agregar el restaurante: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerDatosRestaurante(): Restaurante {
        val nombre = findViewById<EditText>(R.id.editTextName).text.toString()
        val descripcion = findViewById<EditText>(R.id.editTextDescription).text.toString()
        return Restaurante(id = "", nombre = nombre, descripcion = descripcion, imagenUrl = "", creadorUID = "")
    }
}

data class Restaurante(
    val id: String = "",
    val nombre: String,
    val descripcion: String,
    val imagenUrl: String,
    val creadorUID: String = "",
    val calificaciones: List<Float> = emptyList()
) {
    val calificacionPromedio: Float
        get() = if (calificaciones.isNotEmpty()) {
            calificaciones.average().toFloat()
        } else {
            0f
        }
}

