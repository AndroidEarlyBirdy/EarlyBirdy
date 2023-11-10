package com.nbcproject.earlybirdy.board.board_main

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nbcproject.earlybirdy.databinding.ItemBoardBinding
import com.nbcproject.earlybirdy.dto.BoardDto
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.nbcproject.earlybirdy.R

class BoardAdapter(context: Context) : RecyclerView.Adapter<BoardAdapter.Holder>() {

    private val list = ArrayList<BoardDto>()
    private val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference

    var bContext = context

    interface ItemClick {

        fun onClick(view: View, boardData: BoardDto)
        fun deleteItem(view: View, boardData: BoardDto)
    }

    var itemClick: ItemClick? = null

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: List<BoardDto>) {
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
        val binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var item = list[position]
        list.sortWith(
            compareBy { item.uid } // 오름차순
        )
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(val binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BoardDto) = with(binding) { //클릭이벤트추가부분
            itemView.setOnClickListener {
                itemClick?.onClick(it, item)
            }
            tvWriter.text = item.writer
            etContentsTitle.text = item.contentsTitle

            Glide.with(bContext).load(item.contentsPhoto).fallback(R.drawable.bg_calendar_date1).error(R.drawable.bg_calendar_date3).into(ivContentsPoto)

//            val imageRef = storageRef.child(item.bid).child(item.bid)
//
//            imageRef.downloadUrl.addOnSuccessListener {
//                Glide.with(bContext)
//                    .load(it)
//                    .into(binding.ivContentsPoto)
//            }.addOnFailureListener {
//                if (it is StorageException){
//                    ivContentsPoto.setImageURI(Uri.parse(R.drawable.bg_calendar_date1.toString()))
//                }
//            }

            Firebase.firestore.collection("BoardDto").document(item.bid).collection("CommentDto")
                .count().get(
                    AggregateSource.SERVER
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        tvLike.text = it.result.count.toString()
                    }
                }

        }
    }

}