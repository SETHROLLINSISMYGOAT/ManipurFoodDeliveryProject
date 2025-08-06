package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityProfileBinding


class ProfileActivity : AppCompatActivity() {


    private lateinit var binding: ActivityProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.personalInfoLayout.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.addressesLayout.setOnClickListener {

            val intent = Intent(this, MyAddressesActivity::class.java)
            startActivity(intent)
        }


        binding.favouriteLayout.setOnClickListener {

            Toast.makeText(this, "Favourite clicked!", Toast.LENGTH_SHORT).show()

        }

        binding.notificationsLayout.setOnClickListener {

            Toast.makeText(this, "Notifications clicked!", Toast.LENGTH_SHORT).show()

        }

        binding.paymentLayout.setOnClickListener {

            Toast.makeText(this, "Payment Method clicked!", Toast.LENGTH_SHORT).show()

        }

        binding.settingsLayout.setOnClickListener {

            Toast.makeText(this, "Settings clicked!", Toast.LENGTH_SHORT).show()

        }

        binding.logoutLayout.setOnClickListener {

            Toast.makeText(this, "Log Out clicked!", Toast.LENGTH_SHORT).show()

        }


    }
}
