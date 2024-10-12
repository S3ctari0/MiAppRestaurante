package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.net.HttpURLConnection
import java.net.URL

class MainActivityRegister : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_register)

        val buttonRegistro = findViewById<Button>(R.id.Button_Registro1)
        val name = findViewById<EditText>(R.id.UsuarioR)
        val password = findViewById<EditText>(R.id.ContrasenaR)
        val user = findViewById<EditText>(R.id.UserName)

        buttonRegistro.setOnClickListener {
            val userName = user.text.toString()
            val pass = password.text.toString()
            val fullName = name.text.toString()

            if (userName.isNotEmpty() && pass.isNotEmpty() && fullName.isNotEmpty()) {
                Thread {
                    try {
                        val url = URL("http://192.168.0.6/Register.php")
                        val postData = "name=$fullName&username=$userName&password=$pass"

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
                                    Log.e("MainActivityRegister", "Error en el registro: $response")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } else {
                Log.e("MainActivityRegister", "Por favor, completa todos los campos.")
            }
        }
    }
}