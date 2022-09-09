package com.example.im.controller.adapter

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.ColorSpace
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.im.IMApplication.Companion.mContext
import com.example.im.R
import com.example.im.controller.activity.ChatActivity
import com.example.im.databinding.ItemChatRecyclerBinding
import com.example.im.model.db.ConversationManager
import com.example.im.utils.Constant
import com.example.im.utils.SetList
import com.example.im.utils.Utils
import java.time.LocalTime
import java.util.*


class ChatRecyclerViewAdapter(private var list: SetList) :
    RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>() {

    private val mBLM = LocalBroadcastManager.getInstance(mContext)
    private lateinit var mBR: BroadcastReceiver

    //上一个获取焦点的item
    private lateinit var lastView: HorizontalScrollView
    private var lastPosition = -1

    companion object {
        const val TAG = "ChatRecyclerViewAdapter"
    }

    init {
        setConversationListener()
    }

    private fun setConversationListener() {
        mBR = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                val index = intent?.getIntExtra("index", 0)
                if (index != null) {
                    notifyItemChanged(index, 0)
                }
            }
        }
        mBLM.registerReceiver(mBR, IntentFilter(Constant.CONVERSATION_CHANGED))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_recycler, parent, false)
        ).apply {
            binding.llContent.layoutParams =
                LinearLayout.LayoutParams(
                    Utils.getScreenWidth(),
                    ActionBar.LayoutParams.WRAP_CONTENT
                )
            binding.tvCount.visibility = View.VISIBLE
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding

        val info = list[position]

        binding.llContent.run {
            setOnClickListener {
                if (info.showMenu) {
                    binding.hsvItem.post {
                        binding.hsvItem.fullScroll(HorizontalScrollView.FOCUS_LEFT)
                        info.showMenu = false
                    }
                } else {
                    //关闭上个焦点item的菜单
                    closeLastItemMenu()
                    ChatActivity.start(context, info.userId, info.nickname)
                    ConversationManager.setMessageCount(position, 0)
                    binding.tvCount.visibility = View.INVISIBLE
                }

            }
        }

        //滑动菜单效果
        //初始位置
        var x1: Float = -1f
        binding.hsvItem.run {
            setOnTouchListener { view, motionEvent ->
                view as HorizontalScrollView

                //关闭上个焦点item的菜单
                closeLastItemMenu()

                when (motionEvent.action) {
                    MotionEvent.ACTION_MOVE -> {
                        if (x1 < 0) {
                            x1 = motionEvent.x
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        val rowDis = motionEvent.x - x1
                        if (rowDis > 30 && info.showMenu) {
                            //向右滑动，隐藏菜单
                            info.showMenu = false
                        } else if (rowDis < -30 && !info.showMenu) {
                            //向左滑动，显示菜单
                            info.showMenu = true
                        }
                        x1 = -1f
                        view.post {
                            if (info.showMenu) {
                                view.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                                lastView = view
                                lastPosition = position
                            } else {
                                view.fullScroll(HorizontalScrollView.FOCUS_LEFT)
                            }
                        }
                        performClick()
                    }
                }
                false
            }
        }

        //置顶
        binding.tvToTop.setOnClickListener {
            Log.d(TAG, "置顶会话")
            Log.d(TAG, binding.tvCount.height.toString())
        }

        //标为未读/已读
        binding.tvSetReadFlag.run {
            text = if (info.count > 0) "标为未读" else "标为已读"

            setOnClickListener {
                text = if (info.count > 0) {
                    ConversationManager.setMessageCount(position, 0)
                    binding.tvCount.visibility = View.INVISIBLE
                    "标为未读"
                } else {
                    ConversationManager.setMessageCount(position, 1)
                    binding.tvCount.run {
                        text = "1"
                        visibility = View.VISIBLE
                    }
                    "标为已读"
                }
                binding.tvSetReadFlag.post {
                    binding.hsvItem.fullScroll(HorizontalScrollView.FOCUS_LEFT)
                }
            }
        }

        //从列表删除
        binding.tvDelete.setOnClickListener {
            binding.hsvItem.post {
                binding.hsvItem.fullScroll(HorizontalScrollView.FOCUS_LEFT)
            }
            list[position].showMenu = false
            ConversationManager.deleteConversation(position)
        }


        //设置页面内容
        if (info.avatarUrl == "") {
            info.avatarUrl =
                "https://img2.baidu.com/it/u=2708726027,898681604&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500"
        }

        Glide.with(binding.root)
            .load(info.avatarUrl)
            .into(binding.imgAvatar)

        binding.run {
            tvNickname.text = info.nickname
            tvSign.text = info.message
            tvTime.text = info.getLastTime()
            tvCount.run {
                if (info.count > 0) {
                    text = info.count.toString()
                    visibility = View.VISIBLE
                } else {
                    visibility = View.INVISIBLE
                }


            }


        }
    }


    private fun closeLastItemMenu() {
        if (lastPosition != -1) {
            val last = list[lastPosition]
            if (last.showMenu) {
                lastView.post {
                    lastView.fullScroll(HorizontalScrollView.FOCUS_LEFT)
                    list[lastPosition].showMenu = false
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemChatRecyclerBinding.bind(itemView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mBLM.unregisterReceiver(mBR)
    }

}
