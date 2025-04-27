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

        // Fetch and display all categories
        lifecycleScope.launch(Dispatchers.IO) {
            val categories = categoryDao.getAllCategories()
            // Update the UI with the categories
            withContext(Dispatchers.Main) {
                binding.textViewCategories.text = categories.joinToString(", ") { it.name }
            }
        }

        fun updateCategoryList() {
            lifecycleScope.launch(Dispatchers.IO) {
                val categories = categoryDao.getAllCategories()
                withContext(Dispatchers.Main) {
                    // Update the UI with the categories
                    binding.textViewCategories.text = categories.joinToString(", ") { it.name }
                }
            }
        }

        // Handle Add Category button click
        binding.buttonAddCategory.setOnClickListener {
            val categoryName = binding.editTextCategoryName.text.toString()

            if (categoryName.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    // Check if category already exists
                    val existingCategory = categoryDao.getCategoryByName(categoryName)

                    if (existingCategory == null) {
                        val newCategory = Category(name = categoryName)
                        categoryDao.insert(newCategory)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "$categoryName added", Toast.LENGTH_SHORT).show()
                            binding.editTextCategoryName.text.clear()

                            // Refresh category list
                            updateCategoryList()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "$categoryName already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonDeleteCategory.setOnClickListener {
            val categoryNameToDelete = binding.editTextCategoryName.text.toString()

            if (categoryNameToDelete.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    // Check if the category exists
                    val existingCategory = categoryDao.getCategoryByName(categoryNameToDelete)

                    if (existingCategory != null) {
                        // Delete the category from the database
                        categoryDao.deleteCategory(categoryNameToDelete)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "$categoryNameToDelete deleted", Toast.LENGTH_SHORT).show()
                            binding.editTextCategoryName.text.clear()

                            // Refresh the category list after deletion
                            updateCategoryList()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "$categoryNameToDelete does not exist", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Please enter a category name to delete", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
