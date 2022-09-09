package com.example.im.controller.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.im.R
import com.example.im.databinding.ItemMsgLeftBinding
import com.example.im.databinding.ItemMsgRightBinding
import com.example.im.model.bean.Message
import com.example.im.model.db.ContactManager

class ChatMessageAdapter(
    private val msgList: List<Message>,
    private val myAvatarUrl: String,
    private val contactAvatarUrl: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == Message.TYPE_RECEIVE) {
            ReceiveViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_msg_left, parent, false)
            )
        } else {
            SentViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_msg_right, parent, false)
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = msgList[position]

        when (holder) {
            is ReceiveViewHolder -> holder.binding.run {
                tvChatItemLeftMsg.text = message.content

                Glide.with(this.root)
                    .load(contactAvatarUrl)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(imgAvatar)
            }
            is SentViewHolder -> holder.binding.run {
                tvChatItemRightMsg.text = message.content
                Glide.with(this.root)
                    .load(myAvatarUrl)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(imgAvatar)
            }
        }
    }

    override fun getItemCount(): Int = msgList.size

    override fun getItemViewType(position: Int): Int {
        return msgList[position].type
    }

    inner class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = ItemMsgLeftBinding.bind(itemView)
    }

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = ItemMsgRightBinding.bind(itemView)
    }
}