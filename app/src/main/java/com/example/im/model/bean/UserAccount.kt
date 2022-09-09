package com.example.im.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hyphenate.chat.EMUserInfo

@Entity
data class UserAccount(
//    var nickname: String,
    var password: String,
    var rememberPwd: Boolean,
//    override var userId: String,
//    var avatarUrl: String
) : UserInfo() {
    @PrimaryKey
    override var userId: String = ""


//    constructor(
//        userId: String, password: String,
//        rememberPwd: Boolean
//    ) : this(password, rememberPwd)

    constructor(user: EMUserInfo, password: String = "", rememberPwd: Boolean = false) : this(
        password,
        rememberPwd
    ) {
        this.userId = user.userId
        this.nickname = user.nickname
        this.avatarUrl = user.avatarUrl
        this.birth = user.birth
        this.signature = user.signature
        this.phoneNumber = user.phoneNumber
        this.email = user.email
        this.gender = user.gender
    }

    override fun toString(): String {
        return "UserAccount(userId='$userId', nickname='$nickname', avatarUrl='$avatarUrl', birth='$birth', signature='$signature', phoneNumber='$phoneNumber', email='$email', gender=$gender,password='$password', rememberPwd=$rememberPwd)"
    }

}
