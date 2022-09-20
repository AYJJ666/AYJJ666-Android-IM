package com.example.im.controller.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.im.IMApplication
import com.example.im.controller.adapter.SearchResultAdapter
import com.example.im.databinding.ActivityAddContactBinding
import com.example.im.model.Model
import com.example.im.model.bean.UserAccount
import com.example.im.model.bean.UserInfo
import com.example.im.model.db.ContactManager
import com.example.im.model.db.UserAccountManager
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMUserInfo
import kotlinx.coroutines.*

class AddContactActivity : BaseActivity() {
    private lateinit var binding: ActivityAddContactBinding

    companion object {
        const val TAG = "AddContactActivity"

        //应通过此方法启动activity
        fun start(context: Context) {
            val intent = Intent(context, AddContactActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.btnAddSearch.setOnClickListener {
            val userId = binding.etAddKey.text.toString()
            if (TextUtils.isEmpty(userId)) {
                Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (userId == EMClient.getInstance().currentUser) {
                Log.d(TAG, "自己")
                //todo
                Model.getGlobalThreadPool().execute {
                    val list = ArrayList<UserInfo>()
                    list.add(UserAccountManager.currentUserAccount)
                    runOnUiThread {
                        binding.rvAddSearchResult.run {
                            if (adapter == null) {
                                adapter = SearchResultAdapter(context, list)
                                layoutManager = LinearLayoutManager(context)
                            } else {
                                (adapter as SearchResultAdapter).setData(list)
                            }
                        }
                    }
                }
            } else {
                Model.getGlobalThreadPool().execute {
                    if (ContactManager.hasContact(userId)) {
                        //查找本地好友列表
                        val userInfo = ContactManager.getContactByUserId(userId)
                        //                userInfo.isContact = false
                        val list = ArrayList<UserInfo>().apply { add(userInfo) }
                        runOnUiThread {
                            binding.rvAddSearchResult.run {
                                if (adapter == null) {
                                    adapter = SearchResultAdapter(context, list)
                                    layoutManager = LinearLayoutManager(context)
                                } else {
                                    (adapter as SearchResultAdapter).setData(list)
                                }
                            }
                        }
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            val userInfo =
                                ContactManager.getContactFromClient(userId)
                            val list = ArrayList<UserInfo>().apply {
                                if (userInfo != null) {
                                    add(userInfo)
                                }
                            }
                            binding.rvAddSearchResult.run {
                                if (adapter == null) {
                                    adapter = SearchResultAdapter(context, list)
                                    layoutManager = LinearLayoutManager(context)
                                } else {
                                    (adapter as SearchResultAdapter).setData(list)
                                }
                            }
                        }
                    }

                }
            }


        }


    }
}