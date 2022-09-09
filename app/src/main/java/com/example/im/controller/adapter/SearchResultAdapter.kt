package com.example.im.controller.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.im.R
import com.example.im.databinding.DialogAddContactBinding
import com.example.im.databinding.ItemAddContactBinding
import com.example.im.model.bean.Contact
import com.example.im.model.bean.UserAccount
import com.example.im.model.bean.UserInfo
import com.example.im.model.db.ContactManager
import com.hyphenate.chat.EMClient

class SearchResultAdapter(val context: Context, private var list: ArrayList<UserInfo>) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_contact, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        binding.tvAddContactNickname.text = list[position].userId
        when (val userInfo = list[position]) {
            is UserAccount -> {
                binding.run {
                    btnAddSend.run {
                        text = "自己"
                        isEnabled = false
                    }
                }
            }
            is Contact -> {
                if (userInfo.isFriend) {
                    binding.btnAddSend.run {
                        text = "好友"
                        isEnabled = false
                    }
                } else {
                    binding.btnAddSend.run {
                        text = "加好友"
                        isEnabled = true
                        setOnClickListener {
                            // 添加好友。
                            sendInvitation(position, it as Button)
                        }
                    }
                }
            }
            else -> {
                binding.btnAddSend.run {
                    text = "加好友"
                    isEnabled = true
                    setOnClickListener {
                        // 添加好友。
                        sendInvitation(position, it as Button)
                    }
                }
            }
        }
    }

    private fun sendInvitation(position: Int, btnSend: Button) {
        //创建一个对话框
        val builder = AlertDialog.Builder(context)
        builder.setTitle("请输入申请理由：")
        //设置内容
        val dialogBinding =
            DialogAddContactBinding.inflate(LayoutInflater.from(context))
        builder.setView(dialogBinding.root)
        val alertDialog: AlertDialog = builder.create() //这个方法可以返回一个alertDialog对象
        //事件
        dialogBinding.run {
            btnCancel.setOnClickListener {
                alertDialog.cancel()
            }
            btnSent.setOnClickListener {
                ContactManager.addContact(
                    list[position].userId,
                    dialogBinding.etReason.text.toString()
                )
                btnSend.run {
                    text = "已发送"
                    isEnabled = false
                }
                alertDialog.cancel()
            }
        }
        alertDialog.show()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(list: ArrayList<UserInfo>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemAddContactBinding.bind(itemView)
    }
}