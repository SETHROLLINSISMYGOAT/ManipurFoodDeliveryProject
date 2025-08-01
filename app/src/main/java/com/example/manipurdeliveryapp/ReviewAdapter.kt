package com.example.manipurdeliveryapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.manipurdeliveryapp.R
import com.example.manipurdeliveryapp.Review
import de.hdodenhof.circleimageview.CircleImageView

class ReviewAdapter(val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivReviewerProfile: CircleImageView = itemView.findViewById(R.id.ivReviewerProfile)
        val tvReviewerName: TextView = itemView.findViewById(R.id.tvReviewerName)
        val tvReviewDate: TextView = itemView.findViewById(R.id.tvReviewDate)
        val tvReviewComment: TextView = itemView.findViewById(R.id.tvReviewComment)
        val llStars: LinearLayout = itemView.findViewById(R.id.llStars)

        fun bind(review: Review) {
            ivReviewerProfile.setImageResource(review.reviewerImageResId)
            tvReviewerName.text = review.reviewerName
            tvReviewDate.text = review.date
            tvReviewComment.text = review.comment


            llStars.removeAllViews()
            for (i in 1..5) {
                val star = ImageView(itemView.context)
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.width = itemView.context.resources.getDimensionPixelSize(R.dimen.star_size)
                params.height = itemView.context.resources.getDimensionPixelSize(R.dimen.star_size)
                if (i > 1) {
                    params.marginStart = itemView.context.resources.getDimensionPixelSize(R.dimen.star_spacing)
                }
                star.layoutParams = params
                star.setImageResource(R.drawable.ic_star)
                star.setColorFilter(itemView.context.resources.getColor(R.color.yellow_star, null))
                if (i > review.rating) {

                    star.alpha = 0.3f
                }
                llStars.addView(star)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size
}