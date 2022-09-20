package com.example.im.model

import android.app.Application
import android.content.Context
import com.example.im.model.db.ContactManager
import com.example.im.model.listener.EventListener
import com.example.im.utils.NameUtil
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//数据模型层全局类
object Model : Application() {
    //线程池
    private val executors = Executors.newCachedThreadPool()
    //用户账号操作类对象
//    private lateinit var userAccountDao:UserAccountDao

    fun init(context: Context) {
        //开启全局监听
        EventListener(context)
    }

    //获取全局线程池
    fun getGlobalThreadPool(): ExecutorService {
        return executors
    }

    fun loginSuccess(userId: String) {
        //刷新联系人
        ContactManager.refreshContacts()
    }

//    fun getUserAccountDao():UserAccountDao{
//        return userAccountDao
//    }
}