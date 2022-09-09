package com.example.im.model.bean

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.TEXT
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.im.model.db.ContactManager
import java.text.SimpleDateFormat

/**
 * 此类重写了equals方法，userId相同即视为相同
 */
@Entity
data class Conversation(
    @PrimaryKey
    @ColumnInfo(typeAffinity = TEXT)
    val userId: String,
    var nickname: String = "昵0称",
    var avatarUrl: String = "",
    var message: String = "最近的消息",
    ) {
    //未读消息数量
    var count: Int = 0
    var time: Long = 0

    //视图渲染使用标记
    @Ignore
    var showMenu = false

    constructor(message: Message) : this(message.userId) {
        val userInfo = ContactManager.getContactByUserId(userId)
        userInfo.let {
            nickname = it.nickname
            avatarUrl = it.avatarUrl
        }
        this.message = message.content
        time = message.time
    }

    fun getLastTime(): String {
        return SimpleDateFormat("hh:mm").format(time)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Conversation) this.userId == other.userId else false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    fun setValue(message: Message) {
        this.message = message.content
        time = message.time
    }
}