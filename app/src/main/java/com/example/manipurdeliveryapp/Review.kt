package com.example.manipurdeliveryapp



import androidx.annotation.DrawableRes

data class Review(
    val reviewerName: String,
    val date: String,
    val comment: String,
    val rating: Int,
    @DrawableRes val reviewerImageResId: Int
)