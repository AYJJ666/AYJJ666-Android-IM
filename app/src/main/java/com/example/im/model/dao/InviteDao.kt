package com.example.im.model.dao

import androidx.room.*
import com.example.im.model.bean.InvitationInfo

@Dao
interface InviteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInvitation(invitationInfo: InvitationInfo): Long

    @Update
    fun updateInvitation(invitationInfo: InvitationInfo)

    @Query("update InvitationInfo set status = :status where userId = :userId")
    fun updateStatus(userId: String, status: InvitationInfo.InvitationStatus)

    @Query("select * from InvitationInfo")
    fun allInvitation(): List<InvitationInfo>


//    fun saveInvitation(
//        username: String?,
//        reason: String?,
//        status: InvitationInfo.InvitationStatus
//    ) {
//        val db = DBHelper.getInstance().writableDatabase
//        if (db.isOpen) {
//            val contentValues = ContentValues().apply {
//                put(InviteTable.COL_USERID, username)
//                put(InviteTable.COL_NICKNAME, username)
//                put(InviteTable.COL_REASON, reason)
//                put(InviteTable.COL_STATUS, status.ordinal)
//            }
//            db.replace(InviteTable.TABLE_NAME, null, contentValues)
//        }
//    }
//
//    fun getInvitations(): List<InvitationInfo> {
//        val list = ArrayList<InvitationInfo>()
//        val db = DBHelper.getInstance().readableDatabase
//
//        if (db.isOpen) {
//            val sql =
//                "select * from ${InviteTable.TABLE_NAME}"
//
//            val cursor = db.rawQuery(sql, null)
//            while (cursor.moveToNext()) {
//                val userIdIndex = cursor.getColumnIndex(ContactTable.COL_USERID)
//
//                val userInfo = UserInfo(cursor.getString(userIdIndex))
////                val groupInfo = GroupInfo()
//                val reasonIndex = cursor.getColumnIndex(InviteTable.COL_REASON)
//                if (reasonIndex == -1) {
//                    Log.d("", "222222222222222222222222222")
//                }
//                var reason = cursor.getString(reasonIndex)
//                if (reason == null) {
//                    reason = "555"
//                }
//                list.add(
//                    InvitationInfo(
//                        userInfo,
////                        groupInfo,
//                        reason,
//                        InvitationInfo.InvitationStatus.INVITE_ACCEPT
//                    )
//                )
//            }
//
//            cursor.close()
//        }
//        return list
//    }
//
//    fun setStatus(userId: String, status: InvitationInfo.InvitationStatus) {
//        val db = DBHelper.getInstance().writableDatabase
//        if (db.isOpen) {
//            val contentValues = ContentValues().apply {
//                put(InviteTable.COL_STATUS, status.ordinal)
//            }
//            db.update(
//                InviteTable.TABLE_NAME,
//                contentValues,
//                "${InviteTable.COL_USERID} = ?",
//                arrayOf(userId)
//            )
//        }
//    }
//
//    fun clean() {
//        val db = DBHelper.getInstance().writableDatabase
//        if (db.isOpen) {
//            db.execSQL("delete from ${InviteTable.TABLE_NAME}")
//        }
//    }
}