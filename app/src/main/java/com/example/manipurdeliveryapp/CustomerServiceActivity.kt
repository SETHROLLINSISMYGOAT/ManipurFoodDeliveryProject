package com.example.manipurdeliveryapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityCustomerServiceBinding

class CustomerServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCallCustomerService.setOnClickListener {
            Toast.makeText(this, "Calling Customer Service (Mock)", Toast.LENGTH_SHORT).show()
        }

        binding.btnAttach.setOnClickListener {
            Toast.makeText(this, "Attach clicked (Mock)", Toast.LENGTH_SHORT).show()
        }

        binding.btnSendMessage.setOnClickListener {
            val message = binding.etMessageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                Toast.makeText(this, "Sent: $message (Mock)", Toast.LENGTH_SHORT).show()
                binding.etMessageInput.text.clear()

            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

