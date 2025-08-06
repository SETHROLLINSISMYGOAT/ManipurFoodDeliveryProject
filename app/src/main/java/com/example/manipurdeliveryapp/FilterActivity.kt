package com.example.manipurdeliveryapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class FilterActivity : AppCompatActivity() {

    private lateinit var btnCloseFilter: ImageButton
    private lateinit var btnDelivery: MaterialButton
    private lateinit var btnPickUp: MaterialButton
    private lateinit var btnOffer: MaterialButton
    private lateinit var btnOnlinePayment: MaterialButton

    private lateinit var btn10min: MaterialButton
    private lateinit var btn20min: MaterialButton
    private lateinit var btn30min: MaterialButton

    private lateinit var btnPricing1: MaterialButton
    private lateinit var btnPricing2: MaterialButton
    private lateinit var btnPricing3: MaterialButton

    private lateinit var starViews: List<ImageView>

    private lateinit var btnDone: MaterialButton

    private var selectedStars = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_filter_search)

        btnCloseFilter = findViewById(R.id.btnCloseFilter)
        btnDelivery = findViewById(R.id.btnDelivery)
        btnPickUp = findViewById(R.id.btnPickUp)
        btnOffer = findViewById(R.id.btnOffer)
        btnOnlinePayment = findViewById(R.id.btnOnlinePayment)

        btn10min = findViewById(R.id.btn10min)
        btn20min = findViewById(R.id.btn20min)
        btn30min = findViewById(R.id.btn30min)

        btnPricing1 = findViewById(R.id.btnPricing1)
        btnPricing2 = findViewById(R.id.btnPricing2)
        btnPricing3 = findViewById(R.id.btnPricing3)

        btnDone = findViewById(R.id.btnDone)

        starViews = listOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )


        btnDone.setOnClickListener {

            val intent = Intent(this, PaymentSuccessActivity::class.java)

            startActivity(intent)
        }

        setupListeners()
    }

    private fun setupListeners() {
        btnCloseFilter.setOnClickListener {
            finish()
        }

        val offerButtons = listOf(btnDelivery, btnPickUp, btnOffer, btnOnlinePayment)
        offerButtons.forEach { button ->
            button.setOnClickListener {
                toggleSelection(button)
            }
        }

        val deliveryTimeButtons = listOf(btn10min, btn20min, btn30min)
        deliveryTimeButtons.forEach { button ->
            button.setOnClickListener {
                deliveryTimeButtons.forEach { it.isSelected = false }
                button.isSelected = true
                updateDeliveryTimeButtonStyles(deliveryTimeButtons)
            }
        }

        val pricingButtons = listOf(btnPricing1, btnPricing2, btnPricing3)
        pricingButtons.forEach { button ->
            button.setOnClickListener {
                pricingButtons.forEach { it.isSelected = false }
                button.isSelected = true
                updatePricingButtonStyles(pricingButtons)
            }
        }

        starViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                selectedStars = index + 1
                updateStarUI()
            }
        }


    }

    private fun toggleSelection(button: MaterialButton) {
        button.isSelected = !button.isSelected
        button.setStrokeColorResource(
            if (button.isSelected) android.R.color.holo_blue_light
            else android.R.color.darker_gray
        )
    }

    private fun updateDeliveryTimeButtonStyles(buttons: List<MaterialButton>) {
        val selectedColor = ContextCompat.getColor(this, R.color.colorAccent)
        val unselectedText = ContextCompat.getColor(this, android.R.color.black)
        val unselectedBg = ContextCompat.getColor(this, android.R.color.transparent)

        buttons.forEach { button ->
            if (button.isSelected) {
                button.setBackgroundColor(selectedColor)
                button.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            } else {
                button.setBackgroundColor(unselectedBg)
                button.setTextColor(unselectedText)
            }
        }
    }

    private fun updatePricingButtonStyles(buttons: List<MaterialButton>) {
        val selectedColor = ContextCompat.getColor(this, R.color.colorAccent)
        val unselectedText = ContextCompat.getColor(this, android.R.color.black)
        val unselectedBg = ContextCompat.getColor(this, android.R.color.transparent)

        buttons.forEach { button ->
            if (button.isSelected) {
                button.setBackgroundColor(selectedColor)
                button.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            } else {
                button.setBackgroundColor(unselectedBg)
                button.setTextColor(unselectedText)
            }
        }
    }

    private fun updateStarUI() {
        starViews.forEachIndexed { i, imageView ->
            val drawableRes = if (i < selectedStars) R.drawable.ic_star_filled else R.drawable.ic_star_unfilled
            imageView.setImageResource(drawableRes)
        }
    }
}
