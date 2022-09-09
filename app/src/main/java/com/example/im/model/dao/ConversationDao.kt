package com.example.im.model.dao

import androidx.room.*
import com.example.im.model.bean.Conversation

@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConversation(conversation: Conversation): Long

    @Query("delete from Conversation where userId = :userId")
    fun deleteById(userId: String)

    @Update
    fun updateConversation(conversation: Conversation): Int

    @Query("update Conversation set count = :count where userId = :userId")
    fun updateCount(userId: String, count:Int)

    @Query("select * from Conversation")
    fun allConversation(): List<Conversation>

}