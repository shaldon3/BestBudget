package com.st10254797.smartbudgetting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(goal: Goal)

    @Query("SELECT * FROM Goal WHERE id = 1 LIMIT 1")
    suspend fun getGoal(): Goal?
}
