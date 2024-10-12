package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

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

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                Thread {
                    try {
                        val url = URL("http://192.168.0.6/Login.php")
                        val postData = "username=$userName&password=$password"

                        val conn = url.openConnection() as HttpURLConnection
                        conn.requestMethod = "POST"
                        conn.doOutput = true
                        val outputStream = conn.outputStream
                        outputStream.write(postData.toByteArray())
                        outputStream.flush()
                        outputStream.close()

                        val responseCode = conn.responseCode
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val inputStream = conn.inputStream
                            val response = inputStream.bufferedReader().use { it.readText() }
                            inputStream.close()

                            runOnUiThread {
                                if (response.contains("\"success\":true")) {
                                    val intent = Intent(this, MainActivityPage::class.java)
                                    intent.putExtra("USER_NAME", userName)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Log.e("MainActivity", "Error en el inicio de sesión: $response")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } else {
                Log.e("MainActivity", "Por favor, completa todos los campos.")

                val intent = Intent(this, MainActivityPage::class.java)
                intent.putExtra("USER_NAME", userName)
                startActivity(intent)
            }
        }
    }
}