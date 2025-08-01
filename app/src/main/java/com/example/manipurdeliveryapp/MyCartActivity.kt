package com.example.manipurdeliveryapp

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.manipurdeliveryapp.databinding.ActivityMyCartBinding
import com.example.manipurdeliveryapp.databinding.ItemCartFoodBinding
import kotlin.math.max


data class CartItem(
    val id: Int,
    val name: String,
    val pricePerItem: Double,
    val imageUrl: Int,
    var quantity: Int,
    var isSelected: Boolean = true
)

class MyCartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyCartBinding
    private lateinit var cartAdapter: CartItemsAdapter
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {

            setResult(Activity.RESULT_CANCELED)
            onBackPressedDispatcher.onBackPressed()
        }


        binding.tvClearCart.setOnClickListener {
            clearCart()
        }


        binding.btnChangeLocation.setOnClickListener {

            setResult(Activity.RESULT_OK)
            finish()
            Toast.makeText(this, "Returning to Dashboard to change location", Toast.LENGTH_SHORT).show()
        }


        binding.btnOrderNowGlobal.setOnClickListener {
            Toast.makeText(this, "Proceeding to order!", Toast.LENGTH_SHORT).show()

        }

        cartItems.add(CartItem(1, "Burger With Meat", 122.30, R.drawable.img_burger_placeholder, 1))
        cartItems.add(CartItem(2, "Ordinary Burgers", 122.30, R.drawable.img_burger_placeholder, 1))

        // Setup RecyclerView
        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartItemsAdapter(cartItems,
            onQuantityChanged = { item, newQuantity ->
                item.quantity = newQuantity
                updatePaymentSummary()
            },
            onDeleteItem = { item ->
                cartItems.remove(item)
                cartAdapter.notifyDataSetChanged()
                updatePaymentSummary()
                updateClearCartText()
                if (cartItems.isEmpty()) {
                    Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show()
                }
            },
            onCheckboxChanged = { item, isChecked ->
                item.isSelected = isChecked
                updatePaymentSummary()
            }
        )
        binding.rvCartItems.adapter = cartAdapter


        updatePaymentSummary()
        updateClearCartText()
    }

    private fun updatePaymentSummary() {
        var totalItemsPrice = 0.0
        var totalItemsCount = 0

        for (item in cartItems) {
            if (item.isSelected) {
                totalItemsPrice += item.pricePerItem * item.quantity
                totalItemsCount += item.quantity
            }
        }

        val deliveryFee = 0.0
        val discount = 109.00

        val finalTotal = totalItemsPrice - discount + deliveryFee

        binding.tvTotalItemsPrice.text = String.format("$%.2f", totalItemsPrice)
        binding.tvDeliveryFee.text = if (deliveryFee == 0.0) "Free" else String.format("$%.2f", deliveryFee)
        binding.tvDiscount.text = String.format("-$%.2f", discount)
        binding.tvTotalAmount.text = String.format("$%.2f", finalTotal)

        binding.paymentSummaryCard.findViewById<TextView>(R.id.tvPaymentSummaryItemCount)?.text = "Total Items ($totalItemsCount)"
    }

    private fun clearCart() {
        if (cartItems.isNotEmpty()) {
            cartItems.clear()
            cartAdapter.notifyDataSetChanged()
            updatePaymentSummary()
            updateClearCartText()
            Toast.makeText(this, "Cart cleared!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Cart is already empty.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateClearCartText() {
        val selectedItemsCount = cartItems.count { it.isSelected }
    }
}

class CartItemsAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onDeleteItem: (CartItem) -> Unit,
    private val onCheckboxChanged: (CartItem, Boolean) -> Unit
) : RecyclerView.Adapter<CartItemsAdapter.CartItemViewHolder>() {

    inner class CartItemViewHolder(private val binding: ItemCartFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.ivFoodImage.setImageResource(item.imageUrl)
            binding.tvFoodName.text = item.name
            binding.tvFoodPrice.text = String.format("$%.2f", item.pricePerItem)
            binding.tvQuantity.text = item.quantity.toString()

            binding.checkboxSelected.isChecked = item.isSelected

            binding.btnPlus.setOnClickListener {
                val newQuantity = item.quantity + 1
                item.quantity = newQuantity
                binding.tvQuantity.text = newQuantity.toString()
                onQuantityChanged(item, newQuantity)
            }

            binding.btnMinus.setOnClickListener {
                val newQuantity = max(1, item.quantity - 1)
                if (newQuantity != item.quantity) {
                    item.quantity = newQuantity
                    binding.tvQuantity.text = newQuantity.toString()
                    onQuantityChanged(item, newQuantity)
                }
            }

            binding.btnDelete.setOnClickListener {
                onDeleteItem(item)
            }


            binding.checkboxSelected.setOnCheckedChangeListener { _, isChecked ->
                onCheckboxChanged(item, isChecked)

                if (isChecked) {
                    binding.cartItemContainer.setBackgroundResource(R.drawable.cart_item_selected_bg)
                } else {
                    binding.cartItemContainer.setBackgroundResource(R.drawable.rounded_corners_light_gray)
                }
            }

            if (item.isSelected) {
                binding.cartItemContainer.setBackgroundResource(R.drawable.cart_item_selected_bg)
            } else {
                binding.cartItemContainer.setBackgroundResource(R.drawable.rounded_corners_light_gray)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = ItemCartFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size
}