package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnBack: ImageButton
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var btnTogglePasswordVisibility: ImageButton
    private lateinit var btnToggleConfirmPasswordVisibility: ImageButton

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        btnBack = findViewById(R.id.btnBack)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        btnTogglePasswordVisibility = findViewById(R.id.btnTogglePasswordVisibility)
        btnToggleConfirmPasswordVisibility = findViewById(R.id.btnToggleConfirmPasswordVisibility)


        etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        etConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()


        btnTogglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnTogglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on)
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnTogglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
            }
            etPassword.setSelection(etPassword.text?.length ?: 0)
        }


        btnToggleConfirmPasswordVisibility.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            if (isConfirmPasswordVisible) {
                etConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnToggleConfirmPasswordVisibility.setImageResource(R.drawable.ic_visibility_on)
            } else {
                etConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnToggleConfirmPasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
            }
            etConfirmPassword.setSelection(etConfirmPassword.text?.length ?: 0)
        }

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            etEmail.error = null
            etPassword.error = null
            etConfirmPassword.error = null


            if (email.isEmpty()) {
                etEmail.error = "Email is required"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Enter a valid email address"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                etPassword.error = "Password is required"
                etPassword.requestFocus()
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                etConfirmPassword.error = "Confirm password is required"
                etConfirmPassword.requestFocus()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                etConfirmPassword.error = "Passwords do not match"
                etConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")
            if (!passwordRegex.matches(password)) {
                etPassword.error = "Password must be 8+ chars, include uppercase, lowercase, digit & special char"
                etPassword.requestFocus()
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}