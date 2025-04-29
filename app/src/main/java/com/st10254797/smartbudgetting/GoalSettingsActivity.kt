package com.st10254797.smartbudgetting

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GoalSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_settings)

        // Enable back button in ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Set Spending Goals"

        val minGoalInput = findViewById<EditText>(R.id.editTextMinGoal)
        val maxGoalInput = findViewById<EditText>(R.id.editTextMaxGoal)
        val saveButton = findViewById<Button>(R.id.buttonSaveGoals)

        saveButton.setOnClickListener {
            val minGoal = minGoalInput.text.toString().toFloatOrNull()
            val maxGoal = maxGoalInput.text.toString().toFloatOrNull()

            if (minGoal == null || maxGoal == null || minGoal < 0 || maxGoal < 0 || minGoal > maxGoal) {
                Toast.makeText(this, "Please enter valid goal values", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("BudgetGoals", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putFloat("minGoal", minGoal)
                putFloat("maxGoal", maxGoal)
                apply()
            }

            Toast.makeText(this, "Goals saved successfully!", Toast.LENGTH_SHORT).show()
            finish() // Go back to previous screen
        }
    }

    // Handle ActionBar back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
