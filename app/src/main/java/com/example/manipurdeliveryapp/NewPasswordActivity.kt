package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityNewPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPasswordBinding
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private var emailToReset: String? = null

    private var isNewPasswordVisible = false
    private var isConfirmNewPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        emailToReset = intent.getStringExtra("emailToReset")

        if (currentUser == null || emailToReset == null) {
            Toast.makeText(this, "Session expired or invalid. Please try again.", Toast.LENGTH_LONG).show()
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setupPasswordToggleListeners()

        binding.btnUpdatePassword.setOnClickListener {
            val newPassword = binding.etNewPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmNewPassword.text.toString().trim()

            if (!validatePasswords(newPassword, confirmPassword)) {
                return@setOnClickListener
            }

            binding.btnUpdatePassword.isEnabled = false

            currentUser?.updatePassword(newPassword)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password updated successfully for $emailToReset!", Toast.LENGTH_LONG).show()
                        auth.signOut()
                        val intent = Intent(this, PasswordChangedSuccessActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to update password: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        binding.btnUpdatePassword.isEnabled = true

                    }
                }
        }
    }

    private fun setupPasswordToggleListeners() {
        binding.newPasswordInputLayout.setEndIconOnClickListener {
            isNewPasswordVisible = !isNewPasswordVisible
            binding.etNewPassword.transformationMethod =
                if (isNewPasswordVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()
            binding.etNewPassword.setSelection(binding.etNewPassword.text?.length ?: 0)
        }

        binding.confirmNewPasswordInputLayout.setEndIconOnClickListener {
            isConfirmNewPasswordVisible = !isConfirmNewPasswordVisible
            binding.etConfirmNewPassword.transformationMethod =
                if (isConfirmNewPasswordVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()
            binding.etConfirmNewPassword.setSelection(binding.etConfirmNewPassword.text?.length ?: 0)
        }
    }

    private fun validatePasswords(newPassword: String, confirmNewPassword: String): Boolean {
        binding.newPasswordInputLayout.error = null
        binding.confirmNewPasswordInputLayout.error = null

        if (newPassword.isEmpty()) {
            binding.newPasswordInputLayout.error = "New password cannot be empty"
            return false
        }
        if (confirmNewPassword.isEmpty()) {
            binding.confirmNewPasswordInputLayout.error = "Confirm password cannot be empty"
            return false
        }
        if (newPassword != confirmNewPassword) {
            binding.confirmNewPasswordInputLayout.error = "Passwords do not match"
            return false
        }

        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")
        if (!passwordRegex.matches(newPassword)) {
            binding.newPasswordInputLayout.error =
                "Use 8+ chars with uppercase, lowercase, digit & special character (!@#$%^&*)"
            return false
        }
        return true
    }
}