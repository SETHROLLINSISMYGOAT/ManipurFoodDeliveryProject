package com.example.manipurdeliveryapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityAddAddressBinding

class MyAddressesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.btnSaveLocation.setOnClickListener {

            val selectedAddressType = when (binding.toggleAddressType.checkedButtonId) {
                R.id.btn_home -> "Home"
                R.id.btn_work -> "Work"
                R.id.btn_other -> "Other"
                else -> "None"
            }


            val addressLabel = binding.etAddressLabel.text.toString().trim()
            val fullAddress = binding.etFullAddress.text.toString().trim()
            val street = binding.etStreet.text.toString().trim()
            val city = binding.etCity.text.toString().trim()
            val zipCode = binding.etZipCode.text.toString().trim()


            val isDefaultAddress = binding.switchDefaultAddress.isChecked


            if (addressLabel.isEmpty() || fullAddress.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val message = """
                Address saved!
                Type: $selectedAddressType
                Label: $addressLabel
                Full Address: $fullAddress
                Street: $street
                City: $city
                Zip Code: $zipCode
                Default: $isDefaultAddress
            """.trimIndent()

            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}
