package com.example.manipurdeliveryapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.manipurdeliveryapp.databinding.ItemCartFoodBinding

data class FoodItem(
    val name: String,
    val price: Double,
    var quantity: Int,
    val imageResId: Int
)

class CartAdapter(
    private val cartItems: MutableList<FoodItem>,
    private val onItemAction: (FoodItem, String) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: ItemCartFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FoodItem) {
            binding.tvFoodName.text = item.name
            binding.tvFoodPrice.text = "$${"%.0f".format(item.price)}"
            binding.tvQuantity.text = item.quantity.toString()
            binding.ivFoodImage.setImageResource(item.imageResId)

            binding.btnMinus.setOnClickListener { onItemAction(item, "minus") }
            binding.btnPlus.setOnClickListener { onItemAction(item, "plus") }
            binding.btnDelete.setOnClickListener { onItemAction(item, "delete") }
            binding.checkboxSelected.setOnCheckedChangeListener { _, isChecked ->
                onItemAction(item, "checkbox")
                if (isChecked) {
                    binding.cartItemContainer.setBackgroundResource(R.drawable.cart_item_selected_bg)
                } else {
                    binding.cartItemContainer.setBackgroundResource(R.drawable.rounded_corners_light_gray)
                }
            }

            binding.checkboxSelected.isChecked = true
            binding.cartItemContainer.setBackgroundResource(R.drawable.cart_item_selected_bg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size
}