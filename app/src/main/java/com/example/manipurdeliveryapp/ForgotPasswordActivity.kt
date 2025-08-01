package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnBackForget.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etForgetEmail.text.toString().trim()

            if (email.isEmpty()) {
                binding.etForgetEmail.error = "Email is required"
                binding.etForgetEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etForgetEmail.error = "Invalid email format"
                binding.etForgetEmail.requestFocus()
                return@setOnClickListener
            }

            binding.btnResetPassword.isEnabled = false
            binding.btnResetPassword.text = "Sending..."


            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Password reset link sent to your email. Please check your inbox (and spam folder).",
                            Toast.LENGTH_LONG
                        ).show()

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        val errorMessage = task.exception?.message ?: "Failed to send reset email."
                        Toast.makeText(
                            this,
                            "Error: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()

                        if (errorMessage.contains("There is no user record", ignoreCase = true) ||
                            errorMessage.contains("no user corresponding", ignoreCase = true)) {
                            Toast.makeText(this, "No account found for this email address.", Toast.LENGTH_LONG).show()
                        }
                    }
                    binding.btnResetPassword.isEnabled = true
                    binding.btnResetPassword.text = "Send Reset Link"
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::binding.isInitialized) {
            binding.btnResetPassword.isEnabled = true
            binding.btnResetPassword.text = "Send Reset Link"
        }
    }
}