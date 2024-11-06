package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActivityRegister : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val buttonRegistro = findViewById<Button>(R.id.Button_Registro1)
        val userInput = findViewById<EditText>(R.id.EmailR)
        val passwordInput = findViewById<EditText>(R.id.ContrasenaR)
        val nameInput = findViewById<EditText>(R.id.UsuarioR)

        buttonRegistro.setOnClickListener {
            val email = userInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()
            val fullName = nameInput.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && fullName.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val usuarioId = auth.currentUser?.uid ?: return@addOnCompleteListener
                            val usuarioData = hashMapOf(
                                "nombre" to fullName,
                                "correo" to email,
                                "favoritos" to mutableListOf<String>()
                            )

                            db.collection("usuarios").document(usuarioId)
                                .set(usuarioData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Usuario creado exitosamente!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, ActivityMainPage::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FirestoreError", "Error al guardar usuario: ${e.message}")
                                    showAlert()
                                }
                        } else {
                            Log.e("AuthError", "Error en autenticación: ${task.exception?.message}")
                            showAlert()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("AuthError", "Error al crear el usuario: ${e.message}")
                    }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}


