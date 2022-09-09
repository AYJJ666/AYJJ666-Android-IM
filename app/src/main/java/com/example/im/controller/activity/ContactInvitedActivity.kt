package com.example.im.controller.activity

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.im.R
import com.example.im.databinding.ActivityContactInvitedBinding
import com.example.im.databinding.ItemInviteManagerBinding
import com.example.im.model.Model
import com.example.im.model.bean.InvitationInfo
import com.example.im.model.db.InviteManager

class ContactInvitedActivity : BaseActivity() {
    private lateinit var binding: ActivityContactInvitedBinding

    companion object {
        const val TAG = "ContactInvitedActivity"

        //应通过此方法启动activity
        fun start(context: Context) {
            val intent = Intent(context, ContactInvitedActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactInvitedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Model.getGlobalThreadPool().execute {
            val list = InviteManager.getAllInvitation()
            binding.rvInvite.run {
                adapter = Adapter(list)
                layoutManager = LinearLayoutManager(context)
            }
        }

        binding.srlRefresh.setOnRefreshListener {
            //todo

            binding.srlRefresh.isRefreshing = false
        }

    }


    private class Adapter(val list: List<InvitationInfo>) :
        RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_invite_manager, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val binding = holder.binding
            val info = list[position]

            Glide.with(binding.root)
                .load(info.avatarUrl)
                .into(binding.imgAvatar)
            binding.tvNickname.text = info.nickname
            binding.tvReason.text = info.reason

            setButton(binding, info)
        }

        private fun setButton(binding: ItemInviteManagerBinding, info: InvitationInfo) {
            //todo
            //同意或拒绝后刷新item
            when (info.status) {
                InvitationInfo.InvitationStatus.NEW_INVITE -> {
                    binding.btnRefuse.run {
                        text = "拒绝"
                        isEnabled = true
                        setOnClickListener {
                            InviteManager.refuseInvitation(info.userId)
                            binding.btnRefuse.visibility = View.GONE
                            binding.btnAccept.run {
                                text = "已拒绝"
                                isEnabled = false
                            }
                        }
                    }
                    binding.btnAccept.run {
                        isEnabled = true
                        text = "同意"
                        setOnClickListener {
                            InviteManager.acceptInvitation(info.userId)
                            binding.btnRefuse.visibility = View.GONE
                            binding.btnAccept.run {
                                text = "已同意"
                                isEnabled = false
                            }
                        }
                    }
                }
                InvitationInfo.InvitationStatus.INVITE_ACCEPT -> {
                    binding.btnRefuse.visibility = View.GONE
                    binding.btnAccept.run {
                        isEnabled = false
                        text = "已同意"
                    }
                }
                InvitationInfo.InvitationStatus.INVITE_REFUSE -> {
                    binding.btnRefuse.visibility = View.GONE
                    binding.btnAccept.run {
                        isEnabled = false
                        text = "已拒绝"
                    }
                }
            }
        }

        override fun getItemCount(): Int = list.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val binding = ItemInviteManagerBinding.bind(itemView)
        }

    }
}