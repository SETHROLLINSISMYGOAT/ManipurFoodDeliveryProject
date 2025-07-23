package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityForgotPasswordMethodSelectionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ForgotPasswordMethodSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordMethodSelectionBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var email: String
    private lateinit var otp: String
    private lateinit var whatsappNumber: String
    private var selectedMethod = "email"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordMethodSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        email = intent.getStringExtra("user_email") ?: run {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        otp = intent.getStringExtra("generated_otp") ?: run {
            Toast.makeText(this, "OTP not generated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        whatsappNumber = intent.getStringExtra("whatsapp_number") ?: ""

        setupViews()
        setupClickListeners()
        updateSelectionUI()
    }

    private fun setupViews() {
        binding.tvEmailAddress.text = email
        if (whatsappNumber.isEmpty()) {
            binding.cardWhatsApp.isEnabled = false
            binding.cardWhatsApp.alpha = 0.5f
            binding.tvWhatsAppNumber.text = "WhatsApp number not registered"
        } else {
            binding.tvWhatsAppNumber.text = whatsappNumber
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.cardEmail.setOnClickListener {
            selectedMethod = "email"
            updateSelectionUI()
        }

        binding.cardWhatsApp.setOnClickListener {
            if (whatsappNumber.isNotEmpty()) {
                selectedMethod = "whatsapp"
                updateSelectionUI()
            }
        }

        binding.btnContinue.setOnClickListener {
            when (selectedMethod) {
                "email" -> resendEmailOtp()
                "whatsapp" -> sendWhatsAppOtp()
            }
        }
    }

    private fun updateSelectionUI() {
        binding.ivEmailCheck.visibility = if (selectedMethod == "email") android.view.View.VISIBLE else android.view.View.INVISIBLE
        binding.ivWhatsAppCheck.visibility = if (selectedMethod == "whatsapp") android.view.View.VISIBLE else android.view.View.INVISIBLE
    }

    private fun resendEmailOtp() {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                proceedToOtpVerification()
            } else {
                Toast.makeText(
                    this,
                    "Failed to resend email: ${task.exception?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun sendWhatsAppOtp() {

        Toast.makeText(this, "OTP sent to WhatsApp: $otp", Toast.LENGTH_LONG).show()
        proceedToOtpVerification()
    }

    private fun proceedToOtpVerification() {
        Intent(this, OtpVerificationActivity::class.java).apply {
            putExtra("user_email", email)
            putExtra("generated_otp", otp)
            putExtra("contact_info", if (selectedMethod == "email") email else whatsappNumber)
            putExtra("verification_method", selectedMethod)
            startActivity(this)
        }
    }
}