package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class ActivityMainPage : AppCompatActivity() {
    private lateinit var gridLayout: GridLayout
    private var cardCount = 0

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
    private fun addCardView() {
        val inflater = LayoutInflater.from(this)
        val cardView = inflater.inflate(R.layout.card_item, gridLayout, false)

        val cardTextView = cardView.findViewById<TextView>(R.id.card_text_view)
        cardTextView.text = "Card ${++cardCount}"

        gridLayout.addView(cardView)
    }
}


