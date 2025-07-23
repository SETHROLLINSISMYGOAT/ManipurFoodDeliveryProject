package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityOtpVerificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var email: String
    private lateinit var correctOtp: String
    private lateinit var contactInfo: String
    private lateinit var verificationMethod: String
    private var countDownTimer: CountDownTimer? = null
    private val OTP_EXPIRY_SECONDS = 300L // 5 minutes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        extractIntentData()
        setupViews()
        startOtpTimer()
    }

    private fun extractIntentData() {
        email = intent.getStringExtra("user_email") ?: run {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        correctOtp = intent.getStringExtra("generated_otp") ?: run {
            Toast.makeText(this, "OTP not generated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        contactInfo = intent.getStringExtra("contact_info") ?: ""
        verificationMethod = intent.getStringExtra("verification_method") ?: "email"
    }

    private fun setupViews() {
        binding.tvVerificationSubtitle.text = "Code sent to ${maskContactInfo(contactInfo, verificationMethod)}"

        setupOtpTextWatchers()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnContinue.setOnClickListener { verifyOtp() }
        binding.tvResendCode.setOnClickListener {
            if (binding.tvResendCode.isEnabled) resendOtp()
        }
    }

    private fun setupOtpTextWatchers() {
        val otpFields = listOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)

        otpFields.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1) {
                        if (index < otpFields.size - 1) {
                            otpFields[index + 1].requestFocus()
                        } else {
                            verifyOtp() // Auto-submit when last digit entered
                        }
                    } else if (s?.isEmpty() == true && index > 0) {
                        otpFields[index - 1].requestFocus()
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun startOtpTimer() {
        countDownTimer?.cancel()
        binding.tvResendCode.isEnabled = false
        binding.tvResendCode.alpha = 0.5f

        countDownTimer = object : CountDownTimer(
            TimeUnit.SECONDS.toMillis(OTP_EXPIRY_SECONDS),
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.tvResendCode.isEnabled = true
                binding.tvResendCode.alpha = 1.0f
                binding.tvTimer.text = "00:00"
                db.collection("passwordResetOtps").document(email)
                    .update("expired", true)
            }
        }.start()
    }

    private fun resendOtp() {
        val newOtp = (100000..999999).random().toString()
        val otpData = hashMapOf(
            "otp" to newOtp,
            "timestamp" to System.currentTimeMillis(),
            "verified" to false,
            "expired" to false
        )

        db.collection("passwordResetOtps").document(email)
            .set(otpData)
            .addOnSuccessListener {
                correctOtp = newOtp
                when (verificationMethod) {
                    "email" -> resendEmailOtp()
                    "whatsapp" -> resendWhatsAppOtp()
                }
                clearOtpFields()
                startOtpTimer()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to resend OTP: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun resendEmailOtp() {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            val message = if (task.isSuccessful) {
                "New OTP sent to your email"
            } else {
                "Email sending failed: ${task.exception?.message}"
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun resendWhatsAppOtp() {
        Toast.makeText(this, "New OTP sent via WhatsApp", Toast.LENGTH_LONG).show()
    }

    private fun clearOtpFields() {
        binding.etOtp1.text?.clear()
        binding.etOtp2.text?.clear()
        binding.etOtp3.text?.clear()
        binding.etOtp4.text?.clear()
        binding.etOtp1.requestFocus()
    }

    private fun verifyOtp() {
        val enteredOtp = buildString {
            append(binding.etOtp1.text)
            append(binding.etOtp2.text)
            append(binding.etOtp3.text)
            append(binding.etOtp4.text)
        }

        when {
            enteredOtp.length != 4 -> {
                Toast.makeText(this, "Please enter complete OTP", Toast.LENGTH_SHORT).show()
            }
            enteredOtp != correctOtp.take(4) -> {
                Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                db.collection("passwordResetOtps").document(email)
                    .update("verified", true)
                    .addOnSuccessListener {
                        proceedToPasswordReset()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun proceedToPasswordReset() {
        Intent(this, ResetPasswordActivity::class.java).apply {
            putExtra("user_email", email)
            startActivity(this)
        }
        finish()
    }

    private fun maskContactInfo(contact: String, method: String): String {
        return when (method) {
            "email" -> {
                val atIndex = contact.indexOf('@')
                if (atIndex > 2) "${contact.substring(0, 2)}****${contact.substring(atIndex)}"
                else "****${contact.substring(maxOf(0, atIndex))}"
            }
            else -> { // whatsapp
                if (contact.length > 4) "*******${contact.takeLast(3)}" else "****"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}