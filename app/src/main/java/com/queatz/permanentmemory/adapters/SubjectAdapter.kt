package com.queatz.permanentmemory.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.ProgressManager
import com.queatz.permanentmemory.logic.applyColorFromProgress
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.on
import kotlinx.android.synthetic.main.item_subject.view.*

class SubjectAdapter constructor(
        private val onClickListener: (SubjectModel) -> Unit,
        private val onActionClickListener: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isInitialSetLoaded = false

    var items: MutableList<SubjectModel> = ArrayList()
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
                0 -> WorldViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false))
                else -> ActionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_action, parent, false))
            }


    override fun getItemCount() = items.size + 1

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is WorldViewHolder -> {
                val subject = items[position]
                viewHolder.itemView.setOnClickListener { onClickListener.invoke(subject) }
                viewHolder.subjectName.text = items[position].name
                val progress = app.on(ProgressManager::class).getProgress(subject)
                viewHolder.subjectProgress.progress = progress
                viewHolder.subjectProgress.applyColorFromProgress()
            }
            is ActionViewHolder -> {
                viewHolder.actionButton.text = viewHolder.actionButton.resources.getText(R.string.add_a_subject)
                viewHolder.actionButton.setOnClickListener { onActionClickListener.invoke() }
            }
        }
    }

    override fun getItemViewType(position: Int) = when (position) { itemCount - 1 -> 1 else -> 0 }
}

class WorldViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val subjectName = itemView.subjectName!!
    val subjectProgress = itemView.subjectProgress!!
}