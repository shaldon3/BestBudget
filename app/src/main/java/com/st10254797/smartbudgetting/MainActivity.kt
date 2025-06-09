package com.st10254797.smartbudgetting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.st10254797.smartbudgetting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Redirect to SignInActivity if user is not signed in
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        } else {
            // Show welcome message with user's email
            val email = firebaseAuth.currentUser?.email
            binding.textViewWelcome.text = "Welcome, $email"
        }

        // Logout button functionality
        binding.buttonLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        // Navigate to CategoryActivity
        binding.buttonManageCategories.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        // Navigate to GoalSettingsActivity
        binding.buttonSetGoals.setOnClickListener {
            startActivity(Intent(this, GoalSettingsActivity::class.java))
        }
    }
}
