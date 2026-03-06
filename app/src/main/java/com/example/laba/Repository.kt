package com.example.laba

import kotlinx.coroutines.flow.Flow

interface MoodRepository {
    fun getAllItemsStream(): Flow<List<HumanMood>>

    suspend fun insertItem(item: HumanMood)

    suspend fun deleteItem(item: HumanMood)

    suspend fun editeItem(item: HumanMood)
}

class Repository(private val moodDao: MoodDAO) : MoodRepository {
    override suspend fun insertItem(item: HumanMood) {
        moodDao.insertMood(item)
    }

    override fun getAllItemsStream(): Flow<List<HumanMood>> {
        return moodDao.getAllMoods()
    }

    override suspend fun deleteItem(item: HumanMood) {
        moodDao.deleteMood(item)
    }

    override suspend fun editeItem(item: HumanMood) {
        moodDao.updateMood(item)
    }
}