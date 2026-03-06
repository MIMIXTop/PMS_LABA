package com.example.laba

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: HumanMood)

    @Query("SELECT * FROM human_mood ORDER BY date DESC")
    fun getAllMoods() : Flow<List<HumanMood>>

    @Delete
    suspend fun deleteMood(mood: HumanMood)

    @Update
    suspend fun updateMood(mood: HumanMood)

}