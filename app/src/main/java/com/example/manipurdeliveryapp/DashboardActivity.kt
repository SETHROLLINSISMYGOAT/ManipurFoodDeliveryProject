package com.example.manipurdeliveryapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.manipurdeliveryapp.databinding.ActivityDashboardBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Constants for location permissions
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    // Launcher to start MyCartActivity and get a result back
    private val myCartActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // This means "Change Location" was clicked in MyCartActivity
            Toast.makeText(this, "Initiating location update...", Toast.LENGTH_SHORT).show()
            // Directly trigger location update when "Change Location" is clicked in MyCart
            requestLocationAndUpdateUI()
        }
        // If result.resultCode is RESULT_CANCELED, it means MyCartActivity was just closed normally.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set up click listener for the cart button to navigate to MyCartActivity
        binding.btnCart.setOnClickListener {
            myCartActivityLauncher.launch(Intent(this, MyCartActivity::class.java))
        }

        // Make the location icon and text clickable to trigger real-time location update
        binding.iconLocation.setOnClickListener {
            requestLocationAndUpdateUI()
        }
        binding.tvLocationLabel.setOnClickListener {
            requestLocationAndUpdateUI()
        }
        binding.tvLocationAddress.setOnClickListener {
            requestLocationAndUpdateUI()
        }

        // Basic functionality for search and filter (as placeholders)
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString()
                Toast.makeText(this, "Searching for: $query", Toast.LENGTH_SHORT).show()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.btnFilter.setOnClickListener {
            Toast.makeText(this, "Filter options would open here!", Toast.LENGTH_SHORT).show()
        }

        // See all links (as placeholders)
        binding.tvSeeAllBestOffers.setOnClickListener {
            Toast.makeText(this, "Navigating to all Best Offers!", Toast.LENGTH_SHORT).show()
        }

        binding.tvSeeAllRecommended.setOnClickListener {
            Toast.makeText(this, "Navigating to all Recommended foods!", Toast.LENGTH_SHORT).show()
        }

        // "Order now" button on the offer card navigates to MyCartActivity
        binding.btnOrderNow.setOnClickListener {
            myCartActivityLauncher.launch(Intent(this, MyCartActivity::class.java))
            Toast.makeText(this, "Navigating to My Cart!", Toast.LENGTH_SHORT).show()
        }

        // --- NEW: Click listener for the "Cheese burger" recommended item ---
        // Access the included layout's root view using its ID
        binding.cheeseBurgerItemInclude.root.setOnClickListener {
            val intent = Intent(this, FoodDetailActivity::class.java)
            // Optionally, pass data to the FoodDetailActivity if needed
            intent.putExtra("FOOD_NAME", "Cheese burger")
            intent.putExtra("FOOD_DESCRIPTION", "A cheeseburger is a hamburger with one or more slices of cheese placed on the meat patty.")
            startActivity(intent)
        }
        // --- END NEW ---

    }

    // --- Location Permission and Fetching Logic (from previous request - unchanged) ---

    // This function checks permission and requests location
    private fun requestLocationAndUpdateUI() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now get location
                getLastLocation()
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied. Cannot get current location.", Toast.LENGTH_SHORT).show()
                binding.tvLocationAddress.text = "Permission Denied" // Update UI to reflect status
            }
        }
    }

    private fun getLastLocation() {
        // Double-check permissions right before making the request
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // This block should ideally not be reached if permission flow is correct,
            // but acts as a safeguard.
            Toast.makeText(this, "Location permission not granted. Please enable it in app settings.", Toast.LENGTH_LONG).show()
            binding.tvLocationAddress.text = "Permission Missing"
            return
        }

        binding.tvLocationAddress.text = "Fetching current location..." // Give immediate feedback to user

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Got last known location. Now get address from latitude and longitude.
                    getAddressFromLocation(location)
                } else {
                    Toast.makeText(this, "Unable to get current location. Please ensure GPS is enabled.", Toast.LENGTH_LONG).show()
                    binding.tvLocationAddress.text = "Location not found"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting location: ${e.message}", Toast.LENGTH_LONG).show()
                binding.tvLocationAddress.text = "Location error"
            }
    }

    private fun getAddressFromLocation(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val fullAddress = StringBuilder()

                // Prioritize specific address lines for a cleaner display
                address.thoroughfare?.let { fullAddress.append(it).append(", ") } // Street name
                address.locality?.let { fullAddress.append(it).append(", ") }       // City/Town
                address.adminArea?.let { fullAddress.append(it).append(", ") }      // State/Province
                address.countryName?.let { fullAddress.append(it) }                 // Country

                // Fallback to maxAddressLineIndex if specific components are not found
                if (fullAddress.isEmpty() && address.maxAddressLineIndex >= 0) {
                    for (i in 0..address.maxAddressLineIndex) {
                        fullAddress.append(address.getAddressLine(i)).append(", ")
                    }
                }

                binding.tvLocationAddress.text = fullAddress.toString().trim().removeSuffix(",").trim()
                Toast.makeText(this, "Location updated!", Toast.LENGTH_SHORT).show()
            } else {
                binding.tvLocationAddress.text = "Address details not found"
                Toast.makeText(this, "Address details not found for this location.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            binding.tvLocationAddress.text = "Network error for address"
            Toast.makeText(this, "Network error or no geocoder service available: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            binding.tvLocationAddress.text = "Invalid location data"
            Toast.makeText(this, "Invalid location data provided.", Toast.LENGTH_LONG).show()
        }
    }
}