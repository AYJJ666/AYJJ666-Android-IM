package com.example.im.model.bean

import androidx.room.*
import androidx.room.ColumnInfo.INTEGER
import org.w3c.dom.Text
import java.sql.Time
import java.sql.Timestamp
import java.util.*

@Entity
data class Message(
    val from: String,
    val to: String,
    val content: String,
    //发送/接收类型
    val type: Int,
    //消息内容类型
    val charType: Int,
    @ColumnInfo(typeAffinity = INTEGER)
//    var time: Long = Date().time,
    var time: Long = 1662293000000,
    //是否已读
    var isRead: Boolean = false,

    ) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0


    //    val time: Long = Date().time
    var userId: String = if (type == TYPE_SENT) to else from

    companion object {
        //发送/接收类型
        const val TYPE_RECEIVE = 0
        const val TYPE_SENT = 1

        //消息内容类型
        const val TEXT = 0
    }

}