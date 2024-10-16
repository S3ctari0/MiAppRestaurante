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

class ActivityRegister : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_register)

        val buttonRegistro = findViewById<Button>(R.id.Button_Registro1)
        val name = findViewById<EditText>(R.id.UsuarioR)
        val password = findViewById<EditText>(R.id.ContrasenaR)
        val user = findViewById<EditText>(R.id.UserName)
        val buttonLogIn = findViewById<Button>(R.id.ButtonLog1n)

        buttonLogIn.setOnClickListener {
            finish()
        }

        buttonRegistro.setOnClickListener {
            val userName = user.text.toString()
            val pass = password.text.toString()
            val fullName = name.text.toString()

            if (userName.isNotEmpty() && pass.isNotEmpty() && fullName.isNotEmpty()) {
                val url = "http://192.168.0.6/Register.php"
                val postData = mapOf(
                    "name" to fullName,
                    "username" to userName,
                    "password" to pass
                )

                val requestQueue: RequestQueue = Volley.newRequestQueue(this)
                val stringRequest = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        if (response.contains("\"success\":true")) {
                            val intent = Intent(this, ActivityMainPage::class.java)
                            Toast.makeText(this, "Usuario creado!", Toast.LENGTH_SHORT).show()
                            intent.putExtra("USER_NAME", userName)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("MainActivityRegister", "Error en el registro: $response")
                            if (response.contains("Usuario ya existe")) {
                                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    Response.ErrorListener { error ->
                        Log.e("MainActivityRegister", "Volley error: ${error.message}")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return postData
                    }
                }
                requestQueue.add(stringRequest)
            } else {
                Toast.makeText(this, "LLena todos los campos solicitados.", Toast.LENGTH_SHORT).show()
                Log.e("MainActivityRegister", "Por favor, completa todos los campos.")
            }
        }
    }
}
