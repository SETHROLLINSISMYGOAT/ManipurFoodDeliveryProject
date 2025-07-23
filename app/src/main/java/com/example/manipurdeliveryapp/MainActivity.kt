package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()


        binding.btnTogglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility()
        }

        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }


        binding.btnGoogleLogin.setOnClickListener {
            Toast.makeText(this, "Google login clicked", Toast.LENGTH_SHORT).show()
        }

    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.btnTogglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
        } else {
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.btnTogglePasswordVisibility.setImageResource(R.drawable.ic_visibility)
        }
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        when {
            email.isEmpty() -> {
                binding.etEmail.error = "Email cannot be empty"
                binding.etEmail.requestFocus()
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Invalid email format"
                binding.etEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Password cannot be empty"
                binding.etPassword.requestFocus()
            }
            password.length < 6 -> {
                binding.etPassword.error = "Password must be at least 6 characters"
                binding.etPassword.requestFocus()
            }
            else -> {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(
                                this,
                                "Login failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }
}