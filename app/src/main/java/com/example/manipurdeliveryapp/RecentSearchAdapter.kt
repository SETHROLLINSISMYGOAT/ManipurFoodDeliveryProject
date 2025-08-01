package com.example.manipurdeliveryapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.manipurdeliveryapp.databinding.ItemRecentSearchBinding

class RecentSearchAdapter(
    val recentSearches: MutableList<String>,
    private val onItemAction: (String, String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {

    inner class RecentSearchViewHolder(private val binding: ItemRecentSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(query: String) {
            binding.tvSearchQuery.text = query
            binding.root.setOnClickListener { onItemAction(query, "click") }
            binding.btnDeleteSearch.setOnClickListener { onItemAction(query, "delete") }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val binding = ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        holder.bind(recentSearches[position])
    }

    override fun getItemCount(): Int = recentSearches.size
}