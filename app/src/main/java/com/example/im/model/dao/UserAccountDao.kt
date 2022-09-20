package com.example.im.model.dao

import androidx.room.*
import com.example.im.model.bean.UserAccount

@Dao
interface UserAccountDao {
//    private var mHelper: DBHelper? = DBHelper.getInstance()

    //添加用户到数据库
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserAccount(userAccount: UserAccount): Long

    @Update
    fun updateUserAccount(newUserAccount: UserAccount)

    @Query("select count(userId) from UserAccount where userId = :userId")
    fun getAccountNumber(userId: String): Int

    @Query("select * from UserAccount where userId = :userId")
    fun getAccountByUserId(userId: String): UserAccount?


//    fun addAccount(user: UserInfo) {
//        println("UserAccountDao:添加:$user")
//        //获取数据库对象
//        val db = mHelper?.readableDatabase
//        //执行添加操作（替换）
//        if (db != null && db.isOpen){
//            val values = ContentValues()
//            values.run {
//                put(UserAccountTable.COL_HXID, user.userId)
//                put(UserAccountTable.COL_NAME, user.userId)
//                put(UserAccountTable.COL_NICK, user.userId)
//                put(UserAccountTable.COL_PHOTO, user.avatarUrl)
//            }
//            db.replace(UserAccountTable.TAB_NAME, null, values)
//            Log.v(javaClass.simpleName, "保存登录信息成功！")
//        }else{
//            Log.d("UserAccountDao", "数据库为空")
//        }
//
//    }

    //根据环信id获取用户信息
//    @SuppressLint("Range")
//    @SuppressLint("Range")

//    fun getAccountByHxId(hxId: String): UserInfo? {
//        var userInfo: UserInfo? = null
//        //获取数据库对象
//        val db = mHelper?.readableDatabase
//        //执行查询语句
//        if (db != null && db.isOpen){
//            val sql =
//                "select * from ${UserAccountTable.TAB_NAME} where ${UserAccountTable.COL_HXID} = ?"
//            val cursor = db.rawQuery(sql, arrayOf(hxId))
//
//            if (cursor.moveToNext()) {
//                userInfo = UserInfo(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)))
//                userInfo.userId = cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID))
//                userInfo.nickname = cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK))
//                userInfo.avatarUrl = cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO))
//            }
//            Log.d(javaClass.simpleName, ":查询:$userInfo")
//            //关闭资源
//            cursor.close()
//        }
//
//        //返回数据
//        return userInfo
//    }
}