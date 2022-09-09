package com.example.im.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.im.model.bean.*
import com.example.im.model.dao.*

@Database(
    version = 1,
    entities = [UserAccount::class, InvitationInfo::class, Contact::class, Message::class, Conversation::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun UserAccountDao(): UserAccountDao
    abstract fun inviteDao(): InviteDao
    abstract fun ContactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao

    companion object {
        private var instance: AppDatabase? = null

        //返回一个数据库实例，单利模式，如果没创建过，就新创建一个名字为app_database的数据库
        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build().apply {
                instance = this
            }
        }
    }
}