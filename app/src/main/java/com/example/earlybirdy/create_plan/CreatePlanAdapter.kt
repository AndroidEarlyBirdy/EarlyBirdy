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
) : RecyclerView.Adapter<CreatePlanAdapter.ViewHolder>() {
    private val wholeList = ArrayList<Todo>()

    private var list = ArrayList<Todo>()

    private lateinit var getDate : CalendarDay

    fun addItems(items : List<Todo>) {
        wholeList.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        list.clear()
        notifyDataSetChanged()
    }

    fun addItem(todo : Todo) {
        wholeList.add(todo)
        notifyDataSetChanged()
    }

    fun addItemInList(todo : Todo) {
        list.add(todo)
        notifyDataSetChanged()
    }
    fun removeItem(position: Int) {
        wholeList.removeAt(position)
        notifyDataSetChanged()
    }

    fun removeItemInList(position : Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(item:Todo, position: Int) {
        val pos = wholeList.indexOfFirst { it == list[position] }
        wholeList[pos] = item
    }

    fun editItemInList(item:Todo, position: Int) {
        list[position] = item
        notifyDataSetChanged()
    }

    fun filterByDate(date: CalendarDay) {
        getDate = date
        for(i in wholeList) {
            if (i.date == date) list.add(i)
        }
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
            checkBoxIschecked.text = item.title
            checkBoxIschecked.isChecked = item.isChecked

            //삭제 버튼 클릭 시
            ivDelete.setOnClickListener {
                val position = wholeList.indexOfFirst { it == item }
                if (position != RecyclerView.NO_POSITION) {
                    removeItemInList(adapterPosition)
                    removeItem(position)
                }
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
}