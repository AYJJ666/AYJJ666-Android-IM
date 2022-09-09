package com.example.im.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.im.model.bean.Conversation
import com.example.im.model.bean.Message

@Dao
interface MessageDao {
    @Insert
    fun insertMessage(message: Message): Long

    @Query("delete from Message where userId = :userId")
    fun deleteByUserId(userId: String)

    @Update
    fun updateMessage(message: Message): Int

    @Query("select * from Message")
    fun allMessages(): List<Message>

    @Query("select * from Message where userId = :userId order by time asc limit :page,20")
    fun getMessagesByUserId(userId: String, page: Int = 0): List<Message>

}