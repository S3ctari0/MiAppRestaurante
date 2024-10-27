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
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class ActivityRegister : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_register)
        FirebaseApp.initializeApp(this)

        val buttonRegistro = findViewById<Button>(R.id.Button_Registro1)
        val name = findViewById<EditText>(R.id.UsuarioR)
        val password = findViewById<EditText>(R.id.ContrasenaR)
        val user = findViewById<EditText>(R.id.EmailR)
        val buttonLogIn = findViewById<Button>(R.id.ButtonLog1n)

        buttonLogIn.setOnClickListener {
            finish()
        }

        buttonRegistro.setOnClickListener {
            val email = user.text.toString()
            val pass = password.text.toString()
            val fullName = name.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && fullName.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(user.text.toString(), password.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, ActivityMainPage::class.java)
                            intent.putExtra("USER_NAME", fullName)
                            Toast.makeText(this, "Usuario creado!", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                        } else {
                            showAlert()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
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
