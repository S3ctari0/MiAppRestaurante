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

        val button = findViewById<Button>(R.id.Button_Ingreso)
        val userInput = findViewById<EditText>(R.id.UsuarioText)
        val passwordInput = findViewById<EditText>(R.id.ContrasenaText)

        button.setOnClickListener {
            val userName = userInput.text.toString()
            val password = passwordInput.text.toString()
            Log.i("Prueba de credenciales","Username: $userName y Contrasena: $password")

            val intent = Intent(this,MainActivityPage::class.java)
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }
    }
}