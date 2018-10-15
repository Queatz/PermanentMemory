package com.queatz.permanentmemory.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.models.SetModel

class SubjectAdapter constructor(
        private val onClickListener: (SetModel) -> Unit,
        private val onMoreClickListener: (SetModel) -> Unit,
        private val onActionClickListener: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isInitialSetLoaded = false

    var items: MutableList<SetModel> = ArrayList()
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
                0 -> SubjectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false))
                else -> ActionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_action, parent, false))
            }

    override fun getItemCount() = items.size + 1

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is SubjectViewHolder -> {
                viewHolder.cardView.setOnClickListener { onClickListener.invoke(items[position]) }
                viewHolder.moreButton.setOnClickListener { onMoreClickListener.invoke(items[position]) }
                viewHolder.setName.text = items[position].name
            }
            is ActionViewHolder -> {
                viewHolder.actionButton.text = viewHolder.actionButton.resources.getText(R.string.add_a_set)
                viewHolder.actionButton.setOnClickListener { onActionClickListener.invoke() }
            }
        }
    }

    override fun getItemViewType(position: Int) = when (position) { itemCount - 1 -> 1 else -> 0 }
}

class SubjectViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val cardView = itemView.findViewById<View>(R.id.cardView)!!
    val moreButton = itemView.findViewById<View>(R.id.moreButton)!!
    val setName = itemView.findViewById<TextView>(R.id.setName)!!
}
