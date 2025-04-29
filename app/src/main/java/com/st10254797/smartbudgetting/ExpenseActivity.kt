package com.st10254797.smartbudgetting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ExpenseActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var categoryDao: CategoryDao

    private lateinit var categorySpinner: Spinner
    private lateinit var amountEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var saveExpenseBtn: Button
    private lateinit var uploadImageBtn: Button
    private lateinit var returnBtn: Button
    private lateinit var expensesListView: ListView // ListView to display expenses

    private var imageUrl: String? = null
    private var selectedCategoryId: Long = -1

    // ✅ Modern image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Persist permission so the app can read it later
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            imageUrl = it.toString()
            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        appDatabase = AppDatabase.getDatabase(this)
        expenseDao = appDatabase.expenseDao()
        categoryDao = appDatabase.categoryDao()

        amountEditText = findViewById(R.id.amountEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        dateEditText = findViewById(R.id.dateEditText)
        saveExpenseBtn = findViewById(R.id.saveExpenseBtn)
        uploadImageBtn = findViewById(R.id.uploadImageBtn)
        categorySpinner = findViewById(R.id.categorySpinner)
        returnBtn = findViewById(R.id.returnBtn)
        expensesListView = findViewById(R.id.expensesListView)

        setupCategorySpinner()

        uploadImageBtn.setOnClickListener {
            pickImage()
        }

        saveExpenseBtn.setOnClickListener {
            saveExpense()
        }

        returnBtn.setOnClickListener {
            finish()
        }
    }

    private fun setupCategorySpinner() {
        lifecycleScope.launch(Dispatchers.IO) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val categories = categoryDao.getCategoriesByUser(userId)
                val categoryNames = categories.map { it.name }

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@ExpenseActivity,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categorySpinner.adapter = adapter

                    categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>, view: View?, position: Int, id: Long
                        ) {
                            selectedCategoryId = categories[position].id.toLong()
                            loadExpensesForCategory(selectedCategoryId)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            selectedCategoryId = -1
                        }
                    }
                }
            }
        }
    }

    private fun loadExpensesForCategory(categoryId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val expenses = expenseDao.getExpensesByCategory(categoryId)

            withContext(Dispatchers.Main) {
                val adapter = ExpenseAdapter(this@ExpenseActivity, expenses)
                expensesListView.adapter = adapter
            }
        }
    }

    // ✅ Updated to use modern Activity Result API
    private fun pickImage() {
        imagePickerLauncher.launch(arrayOf("image/*"))
    }

    private fun saveExpense() {
        val amount = amountEditText.text.toString().toDoubleOrNull()
        val description = descriptionEditText.text.toString()
        val dateString = dateEditText.text.toString()

        if (amount == null || description.isEmpty() || dateString.isEmpty() || selectedCategoryId.toInt() == -1) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val newExpense = Expense(
                id = 0,
                amount = amount,
                description = description,
                date = dateString,
                category = selectedCategoryId,
                imageUrl = imageUrl,
                userId = userId
            )

            lifecycleScope.launch(Dispatchers.IO) {
                expenseDao.insert(newExpense)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ExpenseActivity, "Expense added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show()
        }
    }
}
