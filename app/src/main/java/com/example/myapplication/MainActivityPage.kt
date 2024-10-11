package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivityPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val userName = intent.getStringExtra("USER_NAME")

        val welcomeTextView = findViewById<TextView>(R.id.textView5)
        welcomeTextView.text = "Bienvenido, $userName!"

        val button = findViewById<Button>(R.id.button_exit)
        button.setOnClickListener {
            finishAffinity()
        }
    }
}