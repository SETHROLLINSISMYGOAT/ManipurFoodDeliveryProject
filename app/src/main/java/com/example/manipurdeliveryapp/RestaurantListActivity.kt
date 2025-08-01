package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manipurdeliveryapp.databinding.ActivityRestaurantListBinding

data class Restaurant(
    val name: String,
    val rating: Double,
    val distance: Int,
    val imageResId: Int,
    val deliveryTime: String = "20-25 min",
    val freeDelivery: Boolean = true
)

class RestaurantListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantListBinding
    private lateinit var restaurantAdapter: RestaurantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.btnCart.setOnClickListener {
            val intent = Intent(this, MyCartActivity::class.java)
            startActivity(intent)
        }

        setupRestaurants()
    }

    private fun setupRestaurants() {
        val restaurants = listOf(
            Restaurant("The Burger Place", 4.8, 190, R.drawable.img_restaurant_banner, "20-25 min", true),
            Restaurant("Pizza Paradise", 4.5, 250, R.drawable.img_restaurant_banner, "20-25 min", true),
            Restaurant("Curry House", 4.2, 300, R.drawable.img_restaurant_banner, "20-25 min", true)
        )

        restaurantAdapter = RestaurantAdapter(restaurants) { restaurant ->

            Toast.makeText(this, "Opening cart for items from ${restaurant.name}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MyCartActivity::class.java)

            startActivity(intent)

        }

        binding.rvRestaurants.apply {
            layoutManager = LinearLayoutManager(this@RestaurantListActivity)
            adapter = restaurantAdapter
        }
    }
}