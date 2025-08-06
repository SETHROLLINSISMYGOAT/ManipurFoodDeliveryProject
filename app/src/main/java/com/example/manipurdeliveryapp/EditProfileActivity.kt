
package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        val currentUser = getCurrentUser()
        binding.etFullName.setText(currentUser.fullName)
        binding.etEmail.setText(currentUser.email)
        binding.etPhoneNumber.setText(currentUser.phoneNumber)
        binding.etBio.setText(currentUser.bio)


        binding.btnSave.setOnClickListener {

            val fullName = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val phoneNumber = binding.etPhoneNumber.text.toString()
            val bio = binding.etBio.text.toString()

            val intent = Intent(this, PersonalInfoActivity::class.java)

            startActivity(intent)

        }
    }


    private fun getCurrentUser(): User {

        return User("Vishal Khadok", "hello@halallab.co", "408-841-0926", "I love fast food")
    }
}


data class User(
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val bio: String
)