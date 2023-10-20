package com.example.earlybirdy.create_plan

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.earlybirdy.data.Todo
import com.example.earlybirdy.databinding.ItemTodoBinding
import com.prolificinteractive.materialcalendarview.CalendarDay

class CreatePlanAdapter(
    private val onClickItem: (Int, Todo) -> Unit,
    private val todoDeleteListener : TodoDeleteListener
) : RecyclerView.Adapter<CreatePlanAdapter.ViewHolder>() {

    private var list = ArrayList<Todo>()

    fun addItems(items : List<Todo>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        list.clear()
        notifyDataSetChanged()
    }


    fun removeItemInList(position : Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }


    fun editItemInList(item:Todo, position: Int) {
        list[position] = item
        notifyDataSetChanged()
    }
    

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CreatePlanAdapter.ViewHolder {
        return ViewHolder(
            ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClickItem
        )
    }

    override fun onBindViewHolder(holder: CreatePlanAdapter.ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)


    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding : ItemTodoBinding,
    private val onClickItem: (Int, Todo) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Todo) = with(binding) {
            tvTitle.text = item.title

            //삭제 버튼 클릭 시
            ivDelete.setOnClickListener {
                removeItemInList(adapterPosition)
                todoDeleteListener.onDeleteButtonClicked(item)
            }

            //아이템 클릭 시
            itemTodo.setOnClickListener {
                onClickItem(
                    adapterPosition,
                    item
                )
            }

        }
    }

    interface TodoDeleteListener {
        fun onDeleteButtonClicked(todo : Todo)
    }
}