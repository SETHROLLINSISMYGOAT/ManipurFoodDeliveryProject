package com.example.manipurdeliveryapp



import android.animation.ObjectAnimator
import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manipurdeliveryapp.ReviewAdapter
import com.example.manipurdeliveryapp.databinding.ActivityRestaurantDashboardBinding
import com.example.manipurdeliveryapp.Review
import java.text.DecimalFormat
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.random.Random

class RestaurantDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantDashboardBinding
    private lateinit var reviewAdapter: ReviewAdapter


    private var currentRunningOrders = 20
    private var isRestaurantOpen = true
    private var currentRevenue = 2241.0
    private var currentRating = 4.9
    private var totalReviews = 20


    private val revenueDataPoints = listOf(
        Pair(150.0, 0.1f),
        Pair(200.0, 0.25f),
        Pair(500.0, 0.5f),
        Pair(300.0, 0.7f),
        Pair(400.0, 0.85f),
        Pair(550.0, 1.0f)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
        setupReviewsRecyclerView()


        startSimulatedRealtimeUpdates()
    }

    private fun setupUI() {

        updateRunningOrdersUI()
        updateRestaurantStatusUI()
        updateRevenueUI()
        updateReviewsSummaryUI()


        ArrayAdapter.createFromResource(
            this,
            R.array.revenue_periods,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerRevenuePeriod.adapter = adapter
        }
        binding.spinnerRevenuePeriod.setSelection(0)


        binding.tvGraphTooltip.visibility = View.GONE
    }

    private fun setupListeners() {

        binding.llLocation.setOnClickListener {
            Toast.makeText(this, "Location dropdown clicked", Toast.LENGTH_SHORT).show()

        }

        binding.tvSeeDetails.setOnClickListener {
            Toast.makeText(this, "See Revenue Details clicked", Toast.LENGTH_SHORT).show()

        }


        binding.spinnerRevenuePeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                Toast.makeText(this@RestaurantDashboardActivity, "Viewing data for: $selectedItem", Toast.LENGTH_SHORT).show()

            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        binding.ivRevenueGraph.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val x = event.x
                    val y = event.y


                    val graphWidth = v.width.toFloat()
                    val graphHeight = v.height.toFloat()

                    // Find the data point closest to the touch X
                    var closestPoint: Pair<Double, Float>? = null
                    var minDistance = Float.MAX_VALUE

                    revenueDataPoints.forEach { point ->
                        val pointXAbs = point.second * graphWidth
                        val distance = Math.abs(x - pointXAbs)
                        if (distance < minDistance) {
                            minDistance = distance.toFloat()
                            closestPoint = point
                        }
                    }

                    closestPoint?.let { point ->
                        binding.tvGraphTooltip.text = "$${DecimalFormat("#,##0").format(point.first)}"
                        binding.tvGraphTooltip.x = (point.second * graphWidth) - (binding.tvGraphTooltip.width / 2)
                        binding.tvGraphTooltip.y = (graphHeight * 0.4f) // Adjust Y position for tooltip
                        binding.tvGraphTooltip.visibility = View.VISIBLE
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    binding.tvGraphTooltip.visibility = View.GONE
                }
            }
            true
        }



        binding.tvSeeAllReviews.setOnClickListener {
            Toast.makeText(this, "See All Reviews clicked", Toast.LENGTH_SHORT).show()

        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    Toast.makeText(this, "Dashboard clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_orders -> {
                    Toast.makeText(this, "Orders clicked", Toast.LENGTH_SHORT).show()

                    true
                }
                R.id.nav_add_item -> {

                    Toast.makeText(this, "Add New Item clicked", Toast.LENGTH_SHORT).show()

                    true
                }
                R.id.nav_notifications -> {
                    Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()

                    true
                }
                else -> false
            }
        }
    }

    private fun setupReviewsRecyclerView() {
        val reviewsList = mutableListOf(
            Review("John Doe", "20/12/2020", "Great Food and Service", 5, R.drawable.ic_person), // Use ic_person for placeholder
            Review("Jane Smith", "18/12/2020", "Fast delivery and delicious!", 4, R.drawable.ic_person),
            Review("Alex Johnson", "15/12/2020", "Portions could be bigger.", 3, R.drawable.ic_person)

        )
        reviewAdapter = ReviewAdapter(reviewsList)
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(this@RestaurantDashboardActivity)
            adapter = reviewAdapter
        }
    }


    private fun updateRunningOrdersUI() {
        binding.tvRunningOrdersCount.text = currentRunningOrders.toString()

    }

    private fun updateRestaurantStatusUI() {
        binding.tvRestaurantStatus.text = if (isRestaurantOpen) "Open" else "Closed"
        val statusColor = if (isRestaurantOpen) R.color.green_status else android.R.color.holo_red_dark
        binding.tvRestaurantStatus.setTextColor(resources.getColor(statusColor, null))
    }

    private fun updateRevenueUI() {
        val formatter = DecimalFormat("#,##0")
        binding.tvTotalRevenueAmount.text = "$${formatter.format(currentRevenue)}"
    }

    private fun updateReviewsSummaryUI() {
        val formatter = DecimalFormat("0.0")
        binding.tvOverallRating.text = formatter.format(currentRating)
        binding.tvTotalReviews.text = "Total $totalReviews Reviews"
    }

    private fun simulateNewOrder() {
        currentRunningOrders++
        currentRevenue += Random.nextDouble(50.0, 300.0)

        if (Random.nextBoolean()) {
            currentRating = (currentRating * totalReviews + Random.nextInt(3, 5)) / (totalReviews + 1)
        }
        totalReviews++
        val newReview = Review(
            "New Customer",
            "Today",
            "Just ordered! Great!",
            Random.nextInt(4, 5),
            R.drawable.ic_person
        )
        (reviewAdapter.reviews as MutableList<Review>).add(0, newReview)
        reviewAdapter.notifyItemInserted(0)
        binding.rvReviews.scrollToPosition(0)

        updateRunningOrdersUI()
        updateRevenueUI()
        updateReviewsSummaryUI()

        Toast.makeText(this, "New order received! (Simulated)", Toast.LENGTH_SHORT).show()
    }

    private fun startSimulatedRealtimeUpdates() {

        Timer().schedule(5000, 5000) {
            runOnUiThread {
                simulateNewOrder()

            }
        }
    }
}