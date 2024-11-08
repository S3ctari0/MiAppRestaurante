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

class ActivityLogIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val buttonIngreso = findViewById<Button>(R.id.Button_Ingreso)
        val userInput = findViewById<EditText>(R.id.UsuarioText)
        val passwordInput = findViewById<EditText>(R.id.ContrasenaText)
        val buttonRegistro = findViewById<Button>(R.id.Button_Registro)

        buttonRegistro.setOnClickListener {
            startActivity(Intent(this, ActivityRegister::class.java))
        }

        buttonIngreso.setOnClickListener {
            val userEmail = userInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()

            if (userEmail.isNotEmpty() && pass.isNotEmpty()) {
                auth.signInWithEmailAndPassword(userEmail, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val usuarioId = auth.currentUser?.uid ?: return@addOnCompleteListener

                            // Cargar los datos del usuario desde Firestore
                            db.collection("usuarios").document(usuarioId).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val favoritos = document.get("favoritos") as? List<String> ?: emptyList()
                                        val intent = Intent(this, ActivityMainPage::class.java).apply {
                                            putExtra("USER_NAME", userEmail)
                                            putExtra("FAVORITOS", favoritos.toTypedArray())
                                        }
                                        Toast.makeText(this, "Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show()
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "No se encontraron datos del usuario.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FirestoreError", "Error al cargar usuario: ${e.message}")
                                    showAlert()
                                }
                        } else {
                            Toast.makeText(this, "Error al iniciar sesión.", Toast.LENGTH_SHORT).show()
                            showAlert()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("LoginError", "Error en inicio de sesión: ${e.message}")
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
