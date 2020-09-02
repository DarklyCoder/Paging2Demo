package com.darklycoder.paging2demo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.darklycoder.paging2demo.data.User

class UserListAdapter : PagedListAdapter<User, UserListAdapter.UserViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.uid == newItem.uid
            override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserViewHolder(parent)

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) =
        holder.bindData(getItem(position))

    class UserViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
    ) {

        private val tvName = itemView.findViewById<TextView>(R.id.tv_name)

        fun bindData(item: User?) {
            tvName.text = item?.name
        }
    }

}