package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manipurdeliveryapp.databinding.ActivitySearchFoodBinding

class SearchFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchFoodBinding
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private lateinit var recentOrderAdapter: RecentOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivFilterIcon.setOnClickListener {
            Toast.makeText(this, "Filter clicked (Mock)", Toast.LENGTH_SHORT).show()
        }

        binding.btnDeleteAllSearches.setOnClickListener {
            Toast.makeText(this, "All recent searches deleted (Mock)", Toast.LENGTH_SHORT).show()

            (recentSearchAdapter.recentSearches as MutableList<String>).clear()
            recentSearchAdapter.notifyDataSetChanged()
        }

        setupCategories()
        setupRecentSearches()
        setupRecentOrders()


        recentOrderAdapter.setOnItemClickListener {
            Toast.makeText(this, "Navigating to Restaurant List...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, RestaurantListActivity::class.java))
        }
    }

    private fun setupCategories() {
        val categoryButtons = listOf(binding.btnBurger, binding.btnTaco, binding.btnDrink, binding.btnPizza)
        categoryButtons.forEach { button ->
            button.setOnClickListener {
                categoryButtons.forEach { b -> b.isSelected = false }
                it.isSelected = true
                Toast.makeText(this, "${(it as Button).text} category selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecentSearches() {
        val recentSearches = mutableListOf(
            "Burgers",
            "Fast food",
            "Dessert",
            "French",
            "Fastry"
        )

        recentSearchAdapter = RecentSearchAdapter(recentSearches) { query, action ->
            when (action) {
                "click" -> {
                    Toast.makeText(this, "Searched for: $query (Mock)", Toast.LENGTH_SHORT).show()

                }
                "delete" -> {
                    recentSearches.remove(query)
                    recentSearchAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "$query deleted (Mock)", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.rvRecentSearches.apply {
            layoutManager = LinearLayoutManager(this@SearchFoodActivity)
            adapter = recentSearchAdapter
        }
    }

    private fun setupRecentOrders() {
        val recentOrders = listOf(
            Restaurant("Ordinary Burgers", 4.9, 190, R.drawable.img_ordinary_burgers),
            Restaurant("Ordinary Burgers", 4.9, 190, R.drawable.img_ordinary_burgers)
        )

        recentOrderAdapter = RecentOrderAdapter(recentOrders)
        binding.rvRecentOrders.apply {
            layoutManager = LinearLayoutManager(this@SearchFoodActivity)
            adapter = recentOrderAdapter
        }
    }
}