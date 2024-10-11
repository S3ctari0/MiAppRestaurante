package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonIngreso = findViewById<Button>(R.id.Button_Ingreso)
        val userInput = findViewById<EditText>(R.id.UsuarioText)
        val passwordInput = findViewById<EditText>(R.id.ContrasenaText)
        val buttonRegistro = findViewById<Button>(R.id.Button_Registro)

        buttonRegistro.setOnClickListener {
            val intent = Intent(this,MainActivityRegister::class.java)
            startActivity(intent)
        }

        buttonIngreso.setOnClickListener {
            val userName = userInput.text.toString()
            val password = passwordInput.text.toString()
            Log.i("Prueba de credenciales","Username: $userName y Contrasena: $password")

            val intent = Intent(this,MainActivityPage::class.java)
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }
    }
}