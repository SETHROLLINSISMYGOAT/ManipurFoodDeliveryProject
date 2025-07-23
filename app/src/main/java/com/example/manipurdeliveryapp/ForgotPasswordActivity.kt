package com.example.manipurdeliveryapp


import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var editEmail: TextInputEditText
    private lateinit var editNewPassword: TextInputEditText
    private lateinit var editConfirmNewPassword: TextInputEditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var newPasswordInputLayout: TextInputLayout
    private lateinit var confirmNewPasswordInputLayout: TextInputLayout
    private lateinit var btnReset: MaterialButton

    private var isNewPasswordVisible = false
    private var isConfirmNewPasswordVisible = false

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        btnBack = findViewById(R.id.btnBack)
        editEmail = findViewById(R.id.editForgotEmail)
        editNewPassword = findViewById(R.id.editNewPassword)
        editConfirmNewPassword = findViewById(R.id.editConfirmNewPassword)
        emailLayout = findViewById(R.id.emailLayout)
        newPasswordInputLayout = findViewById(R.id.newPasswordInputLayout)
        confirmNewPasswordInputLayout = findViewById(R.id.confirmNewPasswordInputLayout)
        btnReset = findViewById(R.id.btn_reset_password)


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

        btnReset.setOnClickListener {
            performPasswordReset()
        }
    }

    private fun performPasswordReset() {
        val email = editEmail.text.toString().trim()
        val newPassword = editNewPassword.text.toString().trim()
        val confirmNewPassword = editConfirmNewPassword.text.toString().trim()

        emailLayout.error = null
        newPasswordInputLayout.error = null
        confirmNewPasswordInputLayout.error = null

        if (email.isEmpty()) {
            emailLayout.error = "Email is required"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = "Enter a valid email address"
            return
        }


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


        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Password reset process initiated. Please check your email for further instructions.",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this, PasswordChangedSuccessActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to initiate password reset: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}