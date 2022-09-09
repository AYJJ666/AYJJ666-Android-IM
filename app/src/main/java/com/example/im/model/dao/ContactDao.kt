package com.example.im.model.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.im.model.bean.Contact
import com.hyphenate.chat.EMUserInfo

@Dao
interface ContactDao {
    @Insert(onConflict = REPLACE)
    fun insertContact(contact: Contact)

    @Insert(onConflict = REPLACE)
    fun insertContacts(list: List<Contact>)

    @Query("delete from Contact")
    fun deleteAll()

    @Query("delete from Contact where userId = :userId")
    fun deleteByUserId(userId: String)

    @Query("select * from Contact")
    fun allContacts(): List<Contact>

    @Query("select * from Contact where userId = :userId")
    fun getContactByUserId(userId: String): Contact

    @Query("select count(userId) from Contact where userId = :userId")
    fun hasContact(userId: String): Int
}