package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ActivityMainPage : AppCompatActivity() {

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val userName = intent.getStringExtra("USER_NAME")
        val welcomeTextView = findViewById<TextView>(R.id.textView5)
        welcomeTextView.text = "Bienvenido, $userName!"

        val buttonExit = findViewById<Button>(R.id.button_exit)
        val buttonPlace = findViewById<ImageButton>(R.id.ButtonPlace)

        buttonExit.setOnClickListener {
            finish()
        }
        fun  onClickMenuBoton(v: View) {
            when ((v.id)) {
                R.id.ButtonPlace1->{
                    val intent = Intent(this, ActivityPlace::class.java)
                    startActivity(intent)
                }
                R.id.ButtonPlace2->{
                    val intent = Intent(this, ActivityPlace::class.java)
                    startActivity(intent)
                }
                R.id.ButtonPlace3->{
                    val intent = Intent(this, ActivityPlace::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}
    
