package com.nbcproject.earlybirdy.board.board_read

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nbcproject.earlybirdy.databinding.ItemBoardCommentBinding
import com.nbcproject.earlybirdy.dto.CommentDto
import java.text.SimpleDateFormat
import java.util.Locale

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.Holder>() {

    private val list = ArrayList<CommentDto>()

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

        // 타임스템프 변환
        private val dateFormat = SimpleDateFormat("yy.MM.dd", Locale.getDefault())

        fun bind(item: CommentDto) = with(binding) { //클릭이벤트추가부분
            ivDeleteComment.setOnClickListener {
                itemClick?.deleteItem(it, item)
            }
            tvWriter.text = item.writer
            tvCommentTime.text = item.commentTime?.toDate()?.let { dateFormat.format(it) }
            tvComment.text = item.comments
        }
    }
}