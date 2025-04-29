package com.st10254797.smartbudgetting

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoalSettingsActivity : AppCompatActivity() {

    private lateinit var minGoalInput: EditText
    private lateinit var maxGoalInput: EditText
    private lateinit var goalTextView: TextView
    private lateinit var goalDao: GoalDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Set Spending Goals"

        minGoalInput = findViewById(R.id.editTextMinGoal)
        maxGoalInput = findViewById(R.id.editTextMaxGoal)
        goalTextView = findViewById(R.id.textViewGoalValues)
        val saveButton = findViewById<Button>(R.id.buttonSaveGoals)

        // Initialize Room DAO
        val db = AppDatabase.getDatabase(this)
        goalDao = db.goalDao()

        // Load existing goal and display in both the input fields and the goal bar
        lifecycleScope.launch(Dispatchers.IO) {
            val existingGoal = goalDao.getGoal()
            runOnUiThread {
                if (existingGoal != null) {
                    minGoalInput.setText(existingGoal.minGoal.toString())
                    maxGoalInput.setText(existingGoal.maxGoal.toString())
                    goalTextView.text = "Min: R${existingGoal.minGoal} | Max: R${existingGoal.maxGoal}"
                } else {
                    goalTextView.text = "No goals set yet"
                }
            }
        }

        saveButton.setOnClickListener {
            val minGoal = minGoalInput.text.toString().toFloatOrNull()
            val maxGoal = maxGoalInput.text.toString().toFloatOrNull()

            if (minGoal == null || maxGoal == null || minGoal < 0 || maxGoal < 0 || minGoal > maxGoal) {
                Toast.makeText(this, "Please enter valid goal values", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val goal = Goal(id = 1, minGoal = minGoal, maxGoal = maxGoal) // Fixed ID = 1 for single-row

            // Save to Room and update goal bar text
            lifecycleScope.launch(Dispatchers.IO) {
                goalDao.insertOrUpdate(goal)
                runOnUiThread {
                    Toast.makeText(this@GoalSettingsActivity, "Goals saved successfully!", Toast.LENGTH_SHORT).show()
                    goalTextView.text = "Min: R${minGoal} | Max: R${maxGoal}"
                    finish() // Optional: remove this line if you want to stay on page
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
