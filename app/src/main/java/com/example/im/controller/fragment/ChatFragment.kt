package com.example.im.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.im.controller.adapter.ChatRecyclerViewAdapter
import com.example.im.databinding.FragmentChatBinding
import com.example.im.model.Model
import com.example.im.model.db.ConversationManager
import com.example.im.utils.SetList


/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var list: SetList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        Model.getGlobalThreadPool().execute {
            list = ConversationManager.getConversations()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (_binding == null)
            _binding = FragmentChatBinding.inflate(inflater, container, false)


        initToolbar()
        initRecyclerView()

        binding.srlRefresh.setOnRefreshListener {
            binding.rvChatMessageList.adapter?.notifyDataSetChanged()
            binding.srlRefresh.isRefreshing = false
        }
        return binding.root
    }

    private fun initToolbar(){
        binding.toolbar.run {
            title = "消息"
        }
    }

    private fun initRecyclerView() {
        binding.rvChatMessageList.run {
            adapter = ChatRecyclerViewAdapter(list)
            layoutManager = LinearLayoutManager(context)
        }
        binding.rvChatMessageList.itemAnimator
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "ChatFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ChatFragment.
         */
        @JvmStatic
        fun newInstance() =
            ChatFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}