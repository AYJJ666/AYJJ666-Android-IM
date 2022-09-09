package com.example.im.controller.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.im.R
import com.example.im.controller.adapter.ChatMessageAdapter
import com.example.im.databinding.ActivityChatBinding
import com.example.im.model.Model
import com.example.im.model.bean.Message
import com.example.im.model.db.ContactManager
import com.example.im.model.db.MessageManager
import com.example.im.model.db.UserAccountManager
import com.example.im.utils.ActivityManager
import com.hyphenate.chat.EMClient

class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding

    private val mLBM = LocalBroadcastManager.getInstance(this)
    private lateinit var mBR: BroadcastReceiver
    private lateinit var list: List<Message>

    companion object {
        private const val TAG = "ChatActivity"
        lateinit var userId:String

        //应通过此方法启动activity
        fun start(context: Context, userId: String, nickname: String) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("nickname", nickname)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
        }

        //获取传入参数
        userId = intent.getStringExtra("userId").toString()
        val nickname = intent.getStringExtra("nickname")


        binding.toolbar.run {
            title = nickname
        }

        initRecyclerView(userId)
        //开启消息监听
        mBR = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                Log.d(TAG, "有消息变化")
                binding.rvChatMessage.run {
                    adapter?.let {
                        if (list.isNotEmpty()){
                            it.notifyItemChanged(list.size - 1)
                            scrollToPosition(it.itemCount - 1)
                        }else{
                            it.notifyDataSetChanged()
                        }
                    }

                }
            }
        }
        mLBM.registerReceiver(mBR, IntentFilter(userId))

        //发送消息
        binding.btnChatSend.setOnClickListener {
            val content = binding.etChatInput.text.toString()
            if (content != "") {
                val message = Message(

                    EMClient.getInstance().currentUser,
                    userId,
                    content,
                    Message.TYPE_SENT,
                    Message.TEXT
                )
                Model.getGlobalThreadPool().execute {
                    MessageManager.sendMessage(message)
                }

            }
            binding.etChatInput.text = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_activity_toolbar_menu, menu).apply {
            title = intent.getStringExtra("nickname")
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_menu_delete_message_history -> {
                intent.getStringExtra("userId")?.let { MessageManager.deleteHistory(it) }
            }
            android.R.id.home ->{
                finish()
            }
        }
        return true
    }

    private fun initRecyclerView(userId: String) {
        Model.getGlobalThreadPool().execute {
            //todo 消息顺序不对（时间）
            list = MessageManager.getMessagesByUserId(userId)
            val myAvatarUrl = UserAccountManager.currentUserAccount.avatarUrl
            val contactAvatarUrl = ContactManager.getContactByUserId(userId).avatarUrl
            binding.rvChatMessage.run {
                adapter = ChatMessageAdapter(list, myAvatarUrl, contactAvatarUrl)
                layoutManager = LinearLayoutManager(context)
                this.adapter?.let { scrollToPosition(it.itemCount - 1) }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mLBM.unregisterReceiver(mBR)
    }
}