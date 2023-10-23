package com.example.earlybirdy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ItemRangkingBinding
import com.example.earlybirdy.dto.UserDto

class RankingAdapter(private val userList: List<UserDto>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRangkingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDto) {
            binding.iv4th.setImageResource(user.profile ?: R.drawable.img_profile_add)
            binding.tv4thid.text = user.nickname
            binding.tvContent1.text = user.exp.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRangkingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
