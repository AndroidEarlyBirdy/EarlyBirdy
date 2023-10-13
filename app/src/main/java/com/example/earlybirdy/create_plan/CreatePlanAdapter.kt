package com.example.earlybirdy.create_plan

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.earlybirdy.data.Todo
import com.example.earlybirdy.databinding.ItemTodoBinding

class CreatePlanAdapter : RecyclerView.Adapter<CreatePlanAdapter.ViewHolder>() {

    private val list = ArrayList<Todo>()

    fun addItems(items : List<Todo>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CreatePlanAdapter.ViewHolder {
        return ViewHolder(
            ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CreatePlanAdapter.ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(private val binding : ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Todo) = with(binding) {
            checkBoxIschecked.text = item.title
            checkBoxIschecked.isChecked= item.isChecked
        }
    }

}