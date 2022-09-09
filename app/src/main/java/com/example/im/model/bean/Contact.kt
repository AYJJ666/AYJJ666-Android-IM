package com.example.im.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hyphenate.chat.EMUserInfo

@Entity
class Contact() : UserInfo() {
    @PrimaryKey
    override var userId = ""
    var isFriend: Boolean = false

    constructor(obj: EMUserInfo) : this() {
        userId = obj.userId
        nickname = obj.nickname
        avatarUrl = obj.avatarUrl
        birth = obj.birth
        signature = obj.signature
        phoneNumber = obj.phoneNumber
        email = obj.email
        gender = obj.gender
    }
}