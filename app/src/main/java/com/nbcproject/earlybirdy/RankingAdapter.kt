package com.nbcproject.earlybirdy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nbcproject.earlybirdy.databinding.ItemRangkingBinding
import com.nbcproject.earlybirdy.dto.UserDto

class RankingAdapter(private val userList: List<UserDto>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRangkingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDto, position: Int) {
            val rank = calculateRank(position)
            binding.tvBoard1.text = "$rank 등"
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
        holder.bind(user, position)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    // 모든 사용자의 exp를 비교하여 같은 경우 동일한 rank를 반환
    private fun calculateRank(position: Int): Int {
        if (position == 0) return 4
        if (userList[position].exp == userList[position - 1].exp) {
            return calculateRank(position - 1)
        }
        return calculateRank(position - 1) + 1
    }
}
