package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityTrackOrderBinding

class TrackOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCallDriver.setOnClickListener {
            Toast.makeText(this, "Calling driver (Mock)", Toast.LENGTH_SHORT).show()
            // In a real app, you'd initiate a phone call intent here
        }

        binding.btnChatDriver.setOnClickListener {
            Toast.makeText(this, "Opening chat with driver (Mock)", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, CustomerServiceActivity::class.java))
        }
    }
}