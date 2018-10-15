package com.queatz.permanentmemory.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.models.ItemModel

class ItemAdapter constructor(
        private val onDeleteClickListener: (item: ItemModel) -> Unit,
        private val onActionClickListener: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isInitialSetLoaded = false

    var items: MutableList<ItemModel> = ArrayList()
        set(value) {
            if (!isInitialSetLoaded) {
                items.clear()
                items.addAll(value)
                notifyDataSetChanged()
                isInitialSetLoaded = true
            }

            val diffResult = DiffUtil.calculateDiff(ModelDiffCallback(items, value))
            items.clear()
            items.addAll(value)
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                0 -> SetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_set_item, parent, false))
                else -> ActionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_action, parent, false))
            }

    override fun getItemCount() = items.size + 1

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is SetViewHolder -> {
                viewHolder.deleteButton.setOnClickListener { onDeleteClickListener.invoke(items[position]) }
            }
            is ActionViewHolder -> {
                viewHolder.actionButton.text = viewHolder.actionButton.resources.getText(R.string.add_an_item)
                viewHolder.actionButton.setOnClickListener { onActionClickListener.invoke() }
            }
        }
    }

    override fun getItemViewType(position: Int) = when (position) { itemCount - 1 -> 1 else -> 0 }
}

class SetViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val percentLearnedButton = itemView.findViewById<Button>(R.id.percentLearnedButton)
    val deleteButton = itemView.findViewById<Button>(R.id.deleteButton)
}
