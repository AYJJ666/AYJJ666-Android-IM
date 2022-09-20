package com.example.im.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hyphenate.chat.EMUserInfo

//用户信息bean类
//@Entity
open class UserInfo(
    open var userId: String = "",
    open var nickname: String = "昵称",
    open var avatarUrl: String = "https://download-sdk.oss-cn-beijing.aliyuncs.com/downloads/IMDemo/avatar/Image1.png",
    open var birth: String = "未知",
    open var signature: String = "这个人很懒",
    open var phoneNumber: String = "未知",
    open var email: String = "暂未设置",
    open var gender: Int = 1,
) {

    constructor(user: EMUserInfo) : this(
        user.userId,
        user.nickname,
        user.avatarUrl,
        user.birth,
        user.signature,
        user.phoneNumber,
        user.email,
        user.gender
    )

    override fun toString(): String {
        return "UserInfo(userId='$userId', nickname='$nickname', avatarUrl='$avatarUrl', birth='$birth', signature='$signature', phoneNumber='$phoneNumber', email='$email', gender=$gender)"
    }
}