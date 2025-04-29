package com.st10254797.smartbudgetting

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val minGoal: Float,
    val maxGoal: Float
)