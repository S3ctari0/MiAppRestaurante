package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class ActivityLogIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonIngreso = findViewById<Button>(R.id.Button_Ingreso)
        val userInput = findViewById<EditText>(R.id.UsuarioText)
        val passwordInput = findViewById<EditText>(R.id.ContrasenaText)
        val buttonRegistro = findViewById<Button>(R.id.Button_Registro)

        buttonRegistro.setOnClickListener {
            val intent = Intent(this, ActivityRegister::class.java)
            startActivity(intent)
        }

        buttonIngreso.setOnClickListener {
            val userName = userInput.text.toString()
            val password = passwordInput.text.toString()

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                val url = "http://192.168.0.6/Login.php"
                val requestQueue: RequestQueue = Volley.newRequestQueue(this)
                val stringRequest = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        if (response.contains("\"success\":true")) {
                            val intent = Intent(this, ActivityMainPage::class.java)
                            Toast.makeText(this, "Inicio de sesión completado.", Toast.LENGTH_SHORT).show()
                            intent.putExtra("USER_NAME", userName)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("MainActivity", "Error en el inicio de sesión: $response")
                            Toast.makeText(this, "Usuario o contraseña incorrect@.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener { error ->
                        Log.e("MainActivity", "Volley error: ${error.message}")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return mapOf(
                            "username" to userName,
                            "password" to password
                        )
                    }
                }
                requestQueue.add(stringRequest)
            } else {
                Log.e("MainActivity", "Por favor, completa todos los campos.")
                Toast.makeText(this, "LLena todos los campos solicitados.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}