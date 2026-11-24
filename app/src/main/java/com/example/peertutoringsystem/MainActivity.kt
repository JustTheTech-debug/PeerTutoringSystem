package com.example.peertutoringsystem

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    // Result of the notification permission prompt (the app keeps working if denied)
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Set up your new Toolbar (Member 6 design)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // 2. Link the Bottom Menu to the Navigation Graph
        bottomNav.setupWithNavController(navController)

        // This hides the bottom menu AND top bar on the login/register screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.roleSelectFragment ||
                destination.id == R.id.loginFragment ||
                destination.id == R.id.registerFragment) {
                bottomNav.visibility = android.view.View.GONE
                supportActionBar?.hide() // Hide top bar on login
            } else {
                bottomNav.visibility = android.view.View.VISIBLE
                supportActionBar?.show() // Show top bar on main screens
            }
        }

        askNotificationPermission()
    }

    // Android 13 (API 33) and above need the user to allow notifications
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}