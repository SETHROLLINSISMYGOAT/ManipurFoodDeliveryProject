package com.example.manipurdeliveryapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityPersonalInfoBinding

class PersonalInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backArrowItem.setOnClickListener {

            finish()
        }



        binding.tvUserName.text = "Vishal Khadok"
        binding.tvUserBio.text = "I love fast food"
        binding.tvFullNameValue.text = "Vishal Khadok"
        binding.tvEmailValue.text = "hello@halallab.co"
        binding.tvPhoneNumberValue.text = "408-841-0926"
    }
}
