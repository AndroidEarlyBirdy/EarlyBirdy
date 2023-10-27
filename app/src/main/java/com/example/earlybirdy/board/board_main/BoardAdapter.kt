package com.example.earlybirdy.board.board_main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.earlybirdy.databinding.ItemCommunityBinding
import com.example.earlybirdy.dto.BoardDto

class BoardAdapter() : RecyclerView.Adapter<BoardAdapter.Holder>() {

    private val list = ArrayList<BoardDto>()

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: List<BoardDto>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    interface ItemClick {
        fun onClick(view : View, position : Int)
    }

    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemCommunityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.setOnClickListener {  //클릭이벤트추가부분
            itemClick?.onClick(it, position)
        }
        holder.writer.text = list[position].writer
        holder.contentsTitle.text = list[position].contentsTitle
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(val binding: ItemCommunityBinding) : RecyclerView.ViewHolder(binding.root) {
        val writer = binding.tvWriter
        val contentsTitle = binding.tvContentsTitle
    }


}