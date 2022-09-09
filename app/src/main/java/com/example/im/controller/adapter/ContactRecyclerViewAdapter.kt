package com.example.im.controller.adapter

import android.app.ActionBar
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.im.IMApplication.Companion.mContext
import com.example.im.R
import com.example.im.controller.activity.ChatActivity
import com.example.im.controller.activity.ContactInvitedActivity
import com.example.im.databinding.HeaderFragmentContactBinding
import com.example.im.databinding.ItemChatRecyclerBinding
import com.example.im.model.Model
import com.example.im.model.bean.UserInfo
import com.example.im.model.db.ContactManager
import com.example.im.utils.Constant
import com.example.im.utils.SpUtils
import com.example.im.utils.Utils
import com.google.gson.Gson


class ContactRecyclerViewAdapter(private var list: List<UserInfo>, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mLBM: LocalBroadcastManager = LocalBroadcastManager.getInstance(mContext)
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var refreshContactBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Model.getGlobalThreadPool().execute {
                val list = ContactManager.getAllContacts()
                (context as Activity).runOnUiThread {
                    setData(list)
                }
            }
        }
    }

    private companion object {
        private const val TAG = "ContactRecyclerViewAdapter"
        private const val TYPE_HEADER = 101
        private const val TYPE_BODY = 102
    }

    init {
        mLBM.registerReceiver(
            refreshContactBroadcastReceiver,
            IntentFilter(Constant.CONTACT_CHANGED)
        )
    }

    fun setData(list: List<UserInfo>) {
        this.list = list
        //todo
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = when (viewType) {
        TYPE_HEADER -> HeaderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.header_fragment_contact, parent, false)
        )
        else -> BodyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_recycler, parent, false)
        ).apply {
            binding.run {
                llContent.layoutParams =
                    LinearLayout.LayoutParams(
                        Utils.getScreenWidth(),
                        ActionBar.LayoutParams.WRAP_CONTENT
                    )
                tvToTop.visibility = View.GONE
                tvDelete.visibility = View.GONE
                tvSetReadFlag.visibility = View.GONE
            }

        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                val binding = holder.binding
                //红点控制
                val isNewInvite = SpUtils.getBoolean(SpUtils.IS_NEW_INVITE, false)
                binding.imgRedNewContact.visibility = if (isNewInvite) View.VISIBLE else View.GONE
                //监听红点广播
                broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(p0: Context?, p1: Intent?) {
                        binding.imgRedNewContact.visibility = View.VISIBLE
                        SpUtils.save(SpUtils.IS_NEW_INVITE, true)
                    }
                }
                mLBM.registerReceiver(
                    broadcastReceiver,
                    IntentFilter(Constant.CONTACT_INVITE_CHANGED)
                )

                //新朋友条目点击事件
                binding.llContactInvite.setOnClickListener {
                    binding.imgRedNewContact.visibility = View.GONE
                    SpUtils.save(SpUtils.IS_NEW_INVITE, false)
                    ContactInvitedActivity.start(context)

                }
            }
            is BodyViewHolder -> holder.binding.run {
                val userInfo = list[position - 1]

                Glide.with(root)
                    .load(userInfo.avatarUrl)
                    .into(imgAvatar)
                tvNickname.text = userInfo.nickname
                tvSign.text = userInfo.signature

                llContent.run {
                    setOnClickListener {
                        ChatActivity.start(mContext, userInfo.userId, userInfo.nickname)
                    }
                    setOnLongClickListener {
                        PopupMenu(context, it).run {
                            inflate(R.menu.contact_recycler_item_menu)
                            setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.contact_long_delete -> {
                                        ContactManager.deleteContactFromClient(userInfo.userId)
                                    }
                                }
                                false
                            }
                            gravity = Gravity.CENTER
                            show()
                        }
                        false
                    }
                }
            }


        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        //0为头布局，1为常规布局
        return when (position) {
            0 -> TYPE_HEADER
            else -> TYPE_BODY
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        mLBM.unregisterReceiver(broadcastReceiver)
        mLBM.unregisterReceiver(refreshContactBroadcastReceiver)
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = HeaderFragmentContactBinding.bind(itemView)
    }

    class BodyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemChatRecyclerBinding.bind(itemView)
    }
}
