package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var editNewPassword: TextInputEditText
    private lateinit var editConfirmNewPassword: TextInputEditText
    private lateinit var newPasswordInputLayout: TextInputLayout
    private lateinit var confirmNewPasswordInputLayout: TextInputLayout
    private lateinit var btnVerifyAccount: Button

    private var isNewPasswordVisible = false
    private var isConfirmNewPasswordVisible = false

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        auth = FirebaseAuth.getInstance()

        btnBack = findViewById(R.id.btnBack)
        editNewPassword = findViewById(R.id.editNewPassword)
        editConfirmNewPassword = findViewById(R.id.editConfirmNewPassword)
        newPasswordInputLayout = findViewById(R.id.newPasswordInputLayout)
        confirmNewPasswordInputLayout = findViewById(R.id.confirmNewPasswordInputLayout)
        btnVerifyAccount = findViewById(R.id.btnVerifyAccount)

        newPasswordInputLayout.setEndIconOnClickListener {
            isNewPasswordVisible = !isNewPasswordVisible
            editNewPassword.transformationMethod =
                if (isNewPasswordVisible)
                    HideReturnsTransformationMethod.getInstance()
                else
                    PasswordTransformationMethod.getInstance()
            editNewPassword.setSelection(editNewPassword.text?.length ?: 0)
        }

        confirmNewPasswordInputLayout.setEndIconOnClickListener {
            isConfirmNewPasswordVisible = !isConfirmNewPasswordVisible
            editConfirmNewPassword.transformationMethod =
                if (isConfirmNewPasswordVisible)
                    HideReturnsTransformationMethod.getInstance()
                else
                    PasswordTransformationMethod.getInstance()
            editConfirmNewPassword.setSelection(editConfirmNewPassword.text?.length ?: 0)
        }

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnVerifyAccount.setOnClickListener {
            performPasswordReset()
        }
    }

    private fun performPasswordReset() {
        val newPassword = editNewPassword.text.toString().trim()
        val confirmNewPassword = editConfirmNewPassword.text.toString().trim()

        newPasswordInputLayout.error = null
        confirmNewPasswordInputLayout.error = null

        if (newPassword.isEmpty()) {
            newPasswordInputLayout.error = "New password cannot be empty"
            return
        }
        if (confirmNewPassword.isEmpty()) {
            confirmNewPasswordInputLayout.error = "Confirm password cannot be empty"
            return
        }
        if (newPassword != confirmNewPassword) {
            confirmNewPasswordInputLayout.error = "Passwords do not match"
            return
        }

        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")
        if (!passwordRegex.matches(newPassword)) {
            newPasswordInputLayout.error =
                "Use 8+ chars with uppercase, lowercase, digit & special char"
            return
        }

        val user = auth.currentUser
        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, PasswordChangedSuccessActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to update password: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(this, "User not logged in or session expired. Please re-authenticate.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}