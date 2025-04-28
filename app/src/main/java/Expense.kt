package com.st10254797.smartbudgetting

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val description: String,
    val date: String,
    val category: Long, // Reference to the category in Room
    val imageUrl: String?, // URL of the uploaded image
    val userId: String // this will store the Firebase UID
)
