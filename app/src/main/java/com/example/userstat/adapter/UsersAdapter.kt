package com.example.userstat.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.userstat.R
import com.example.userstat.model.User
import com.squareup.picasso.Picasso

class UsersAdapter(private val callback: (User) -> Unit) :
    PagedListAdapter<User, UsersAdapter.UserViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindTo(getItem(position), callback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(parent)

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem
        }
    }


    class UserViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)) {

        private val nameView = itemView.findViewById<TextView>(R.id.name)
        private val image = itemView.findViewById<ImageView>(R.id.image)
        private val context = parent.context;
        private var user: User? = null

        fun bindTo(user: User?, callback: (User) -> Unit) {
            this.user = user
            nameView.text = user?.login

            Picasso.with(context)
                .load(user?.avatar_url)
                .placeholder(R.drawable.circle_shape)
                .error(R.drawable.notloaded)
                .into(image)
            if (user != null)
                itemView.setOnClickListener { callback(user) }
        }
    }
}