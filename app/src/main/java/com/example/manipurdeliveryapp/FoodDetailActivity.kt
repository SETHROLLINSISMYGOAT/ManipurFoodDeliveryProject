package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.manipurdeliveryapp.databinding.ActivityFoodDetailBinding

class FoodDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.cheeseBurgerItemInclude.root.setOnClickListener {
            val intent = Intent(this, FoodDetailsActivity::class.java)
            intent.putExtra("FOOD_NAME", "Cheese burger")
            intent.putExtra("FOOD_DESCRIPTION", "A cheeseburger is a hamburger with one or more slices of cheese placed on the meat patty.")
            startActivity(intent)
        }
    }
}
