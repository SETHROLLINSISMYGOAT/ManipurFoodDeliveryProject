package com.example.manipurdeliveryapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
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

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val myCartActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Initiating location update...", Toast.LENGTH_SHORT).show()
            requestLocationAndUpdateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnCart.setOnClickListener {
            myCartActivityLauncher.launch(Intent(this, OngoingActivity::class.java))
        }

        binding.iconLocation.setOnClickListener {
            requestLocationAndUpdateUI()
        }
        binding.tvLocationLabel.setOnClickListener {
            requestLocationAndUpdateUI()
        }
        binding.tvLocationAddress.setOnClickListener {
            requestLocationAndUpdateUI()
        }
        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)

            startActivity(intent)

        }


        binding.etSearch.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val query = textView.text.toString()


                if (query.isNotBlank()) {
                    val intent = Intent(this, RestaurantListActivity::class.java).apply {
                        putExtra("SEARCH_QUERY", query)
                    }
                    startActivity(intent)
                    Toast.makeText(this, "Searching for: $query", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a search query.", Toast.LENGTH_SHORT).show()
                }

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.iconSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()

            if (query.isNotBlank()) {
                val intent = Intent(this, RestaurantListActivity::class.java).apply {
                    putExtra("SEARCH_QUERY", query)
                }
                startActivity(intent)
                Toast.makeText(this, "Searching for: $query", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a search query.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnFilter.setOnClickListener {
            Toast.makeText(this, "Filter options would open here!", Toast.LENGTH_SHORT).show()
        }

        binding.tvSeeAllBestOffers.setOnClickListener {
            Toast.makeText(this, "Navigating to all Best Offers!", Toast.LENGTH_SHORT).show()
        }

        binding.tvSeeAllRecommended.setOnClickListener {
            Toast.makeText(this, "Navigating to all Recommended foods!", Toast.LENGTH_SHORT).show()
        }

        binding.btnOrderNow.setOnClickListener {
            myCartActivityLauncher.launch(Intent(this, MyCartActivity::class.java))
            Toast.makeText(this, "Navigating to My Cart!", Toast.LENGTH_SHORT).show()
        }


    }


    private fun requestLocationAndUpdateUI() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Location permission denied. Cannot get current location.", Toast.LENGTH_SHORT).show()
                binding.tvLocationAddress.text = "Permission Denied"
            }
        }
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted. Please enable it in app settings.", Toast.LENGTH_LONG).show()
            binding.tvLocationAddress.text = "Permission Missing"
            return
        }

        binding.tvLocationAddress.text = "Fetching current location..."

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
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

                address.thoroughfare?.let { fullAddress.append(it).append(", ") }
                address.locality?.let { fullAddress.append(it).append(", ") }
                address.adminArea?.let { fullAddress.append(it).append(", ") }
                address.countryName?.let { fullAddress.append(it) }

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