package com.queatz.permanentmemory.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.models.SetModel
import kotlinx.android.synthetic.main.item_set.view.*

class SubjectAdapter (
        private val onClickListener: (SetModel) -> Unit,
        private val onMoreClickListener: (SetModel) -> Unit,
        private val onActionClickListener: () -> Unit,
        var isActionVisible: Boolean = true) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isInitialSetLoaded = false

    var items: MutableList<SetModel> = ArrayList()
        set(value) {
            if (!isInitialSetLoaded) {
                items.clear()
                items.addAll(value)
                notifyDataSetChanged()
                isInitialSetLoaded = true
                return
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

    override fun getItemCount() = items.size + (if (!isActionVisible) 0 else 1)

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

    override fun getItemViewType(position: Int) = if (!isActionVisible) 0 else when (position) { itemCount - 1 -> 1 else -> 0 }
}

class SubjectViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val cardView = itemView.cardView
    val moreButton = itemView.moreButton
    val setName = itemView.setName
}
