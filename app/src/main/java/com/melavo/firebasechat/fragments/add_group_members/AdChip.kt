package com.melavo.firebasechat.fragments.add_group_members

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.melavo.firebasechat.databinding.RowChipBinding
import com.melavo.firebasechat.db.data.ChatUser
import com.melavo.firebasechat.utils.DiffCallbackChatUser
import com.melavo.firebasechat.utils.ItemClickListener
import kotlin.collections.ArrayList

class AdChip (private val context: Context) :
            ListAdapter<ChatUser, RecyclerView.ViewHolder>(DiffCallbackChatUser()) {

   companion object{
         var allAddedContacts=ArrayList<ChatUser>()
         lateinit var listener: ItemClickListener
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
         val binding= RowChipBinding.inflate(layoutInflater, parent, false)
          return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MyViewHolder
        holder.bind(getItem(position))
    }


    class MyViewHolder(val binding: RowChipBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatUser) {
            binding.chatUser = item
            binding.chip.setOnCloseIconClickListener {
                listener.onItemClicked(it,bindingAdapterPosition)
            }
            binding.executePendingBindings()
        }
    }
}
