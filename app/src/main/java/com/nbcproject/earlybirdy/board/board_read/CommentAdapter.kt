package com.nbcproject.earlybirdy.board.board_read

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nbcproject.earlybirdy.databinding.ItemBoardCommentBinding
import com.nbcproject.earlybirdy.dto.CommentDto

class CommentAdapter(context: Context) : RecyclerView.Adapter<CommentAdapter.Holder>() {

    private val list = ArrayList<CommentDto>()

    //var cContext = context

    interface ItemClick {
        fun deleteItem(view: View, commentData: CommentDto)

    }

    var itemClick: ItemClick? = null

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: List<CommentDto>) {
        list.addAll(items)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun removeList(position: Int) {
        list.removeAt(position)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemBoardCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var item = list[position]
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(val binding: ItemBoardCommentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommentDto) = with(binding) { //클릭이벤트추가부분
            ivDeleteComment.setOnClickListener {
                itemClick?.deleteItem(it, item)
            }
            tvWriter.text = item.writer
            tvCommentTime.text = item.commentTime.toString()
            tvComment.text = item.comments
        }
    }


}