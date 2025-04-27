package com.st10254797.smartbudgetting

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.st10254797.smartbudgetting.databinding.ActivityCategoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase
    private lateinit var categoryDao: CategoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Bind the view with ActivityCategoryBinding (or your layout)
        val binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Room database and DAO
        appDatabase = AppDatabase.getDatabase(applicationContext)
        categoryDao = appDatabase.categoryDao()

        // Set window insets listener to handle system bars like status bar and navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fetch and display all categories
        updateCategoryList(binding)

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
                            updateCategoryList(binding)
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

        // Handle Delete Category button click
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
                            updateCategoryList(binding)
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

    // Function to update the category list
    private fun updateCategoryList(binding: ActivityCategoryBinding) {
        lifecycleScope.launch(Dispatchers.IO) {
            val categories = categoryDao.getAllCategories()
            // Update the UI with the categories
            withContext(Dispatchers.Main) {
                binding.textViewCategories.text = categories.joinToString(", ") { it.name }
            }
        }
    }
}
