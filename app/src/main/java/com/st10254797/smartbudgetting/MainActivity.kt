package com.st10254797.smartbudgetting

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.st10254797.smartbudgetting.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize Room database and DAO
        appDatabase = AppDatabase.getDatabase(applicationContext)
        var categoryDao = appDatabase.categoryDao()


        // Redirect to SignInActivity if not signed in
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        } else {
            // Show welcome message
            val email = firebaseAuth.currentUser?.email
            binding.textViewWelcome.text = "Welcome, $email"
        }

        // Logout button functionality
        binding.buttonLogout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Insert a new category (Example)
        val newCategory = Category(name = "Groceries")
        lifecycleScope.launch(Dispatchers.IO) {
            categoryDao.insert(newCategory)
            // Optionally show a message
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Category added", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch and display all categories
        lifecycleScope.launch(Dispatchers.IO) {
            val categories = categoryDao.getAllCategories()
            // Update the UI with the categories
            withContext(Dispatchers.Main) {
                // Assuming you have a TextView to show the categories or use a RecyclerView
                binding.textViewCategories.text = categories.joinToString(", ") { it.name }
            }
        }
    }
}
