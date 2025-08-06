package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityFoodDetailsBinding

class FoodDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFoodDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foodName = intent.getStringExtra("FOOD_NAME")
        val foodDescription = intent.getStringExtra("FOOD_DESCRIPTION")


        binding.itemTitle.text = foodName
        binding.itemDescription.text = foodDescription


        binding.addToCartButton.setOnClickListener {

            val intent = Intent(this, MyCartActivity::class.java)

            startActivity(intent)
        }


        binding.backArrowItem.setOnClickListener {

            finish()
        }
    }
}