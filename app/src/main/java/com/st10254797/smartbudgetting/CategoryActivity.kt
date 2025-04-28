package com.st10254797.smartbudgetting

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.st10254797.smartbudgetting.databinding.ActivityCategoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase
    private lateinit var categoryDao: CategoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = AppDatabase.getDatabase(applicationContext)
        categoryDao = appDatabase.categoryDao()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateCategoryList(binding)

        binding.buttonAddCategory.setOnClickListener {
            val categoryName = binding.editTextCategoryName.text.toString()

            if (categoryName.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                    // Check if the category already exists for this user
                    val existingCategory = categoryDao.getCategoryByNameAndUser(categoryName, userId)

                    if (existingCategory == null) {
                        val newCategory = Category(name = categoryName, userId = userId)
                        categoryDao.insert(newCategory)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "$categoryName added", Toast.LENGTH_SHORT).show()
                            binding.editTextCategoryName.text.clear()

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

        binding.buttonDeleteCategory.setOnClickListener {
            val categoryNameToDelete = binding.editTextCategoryName.text.toString()

            if (categoryNameToDelete.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                    val existingCategory = categoryDao.getCategoryByNameAndUser(categoryNameToDelete, userId)

                    if (existingCategory != null) {
                        categoryDao.deleteCategoryByNameAndUser(categoryNameToDelete, userId)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "$categoryNameToDelete deleted", Toast.LENGTH_SHORT).show()
                            binding.editTextCategoryName.text.clear()

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

        binding.buttonBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateCategoryList(binding: ActivityCategoryBinding) {
        lifecycleScope.launch(Dispatchers.IO) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val categories = categoryDao.getCategoriesByUser(userId)

            withContext(Dispatchers.Main) {
                binding.textViewCategories.text = categories.joinToString(", ") { it.name }
            }
        }
    }
}
