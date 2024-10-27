package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ActivityMainPage : AppCompatActivity() {
    private lateinit var gridLayout: GridLayout

    @SuppressLint("WrongViewCast", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, ActivityLogIn::class.java)
            startActivity(intent)
            finish()
        }

        val userName = intent.getStringExtra("USER_NAME")
        val welcomeTextView = findViewById<TextView>(R.id.textView5)
        welcomeTextView.text = "Bienvenido\n$userName !"

        gridLayout = findViewById(R.id.gridLayout)

        val addCardButton = findViewById<Button>(R.id.buttonAddPlace)
        addCardButton.setOnClickListener {
            val intent = Intent(this, ActivityEditPlace::class.java)
            startActivity(intent)
        }
    }
}


