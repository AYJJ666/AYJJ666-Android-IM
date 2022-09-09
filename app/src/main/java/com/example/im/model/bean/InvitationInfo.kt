package com.example.im.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hyphenate.chat.EMUserInfo

@Entity
data class InvitationInfo(
    @PrimaryKey()
    override var userId: String = "",
    var reason: String = "",
    var status: InvitationStatus = InvitationStatus.NEW_INVITE
) : UserInfo() {

    constructor(emUserInfo: EMUserInfo, reason: String, status: InvitationStatus) : this(
        emUserInfo.userId,
        reason,
        status
    ) {
        nickname = emUserInfo.nickname
        avatarUrl = emUserInfo.avatarUrl
        signature = emUserInfo.signature

    }

    enum class InvitationStatus {
        //联系人邀请信息状态
        NEW_INVITE, //新邀请
        INVITE_ACCEPT,  //接受邀请
        INVITE_REFUSE,  //拒绝邀请
//        INVITE_ACCEPT_BY_PEER,  //邀请被接受

    }


}

//@Entity
//data class InvitationStatus(@PrimaryKey val id: Int, val status: InvitationInfo.InvitationStatus)