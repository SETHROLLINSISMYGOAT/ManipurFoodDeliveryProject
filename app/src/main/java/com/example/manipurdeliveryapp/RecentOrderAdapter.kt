package com.example.manipurdeliveryapp



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.manipurdeliveryapp.databinding.ItemRecentOrderBinding

class RecentOrderAdapter(private val recentOrders: List<Restaurant>) :
    RecyclerView.Adapter<RecentOrderAdapter.RecentOrderViewHolder>() {

    private var onItemClickListener: ((Restaurant) -> Unit)? = null

    fun setOnItemClickListener(listener: (Restaurant) -> Unit) {
        onItemClickListener = listener
    }

    inner class RecentOrderViewHolder(private val binding: ItemRecentOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(restaurant: Restaurant) {
            binding.tvRestaurantName.text = restaurant.name
            binding.tvRating.text = restaurant.rating.toString()
            binding.tvDistance.text = "${restaurant.distance}m"
            binding.ivRestaurantImage.setImageResource(restaurant.imageResId)

            binding.root.setOnClickListener {
                onItemClickListener?.invoke(restaurant)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentOrderViewHolder {
        val binding = ItemRecentOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentOrderViewHolder, position: Int) {
        holder.bind(recentOrders[position])
    }

    override fun getItemCount(): Int = recentOrders.size
}