package com.queatz.permanentmemory.adapters

import android.graphics.PorterDuff
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.logic.ProgressManager
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.on
import kotlinx.android.synthetic.main.item_set.view.*

class SetAdapter(
        private val onClickListener: (SetModel) -> Unit,
        private val onMoreClickListener: (SetModel) -> Unit,
        private val onActionClickListener: () -> Unit,
        var isActionVisible: Boolean = true,
        var showSubjectName: Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                val set = items[position]
                val progress = app.on(ProgressManager::class).getProgress(set)
                viewHolder.cardView.setOnClickListener { onClickListener.invoke(set) }
                viewHolder.moreButton.setOnClickListener { onMoreClickListener.invoke(set) }
                viewHolder.setName.text = items[position].name
                viewHolder.setProgress.progress = progress

                if (showSubjectName) {
                    val subject = app.on(DataManager::class).box(SubjectModel::class).get(set.subject)
                    viewHolder.subjectName.text = subject?.name
                    viewHolder.subjectName.visibility = View.VISIBLE
                }

                when {
                    progress == 0 -> {
                        viewHolder.setStatus.setText(R.string.not_started)
                        viewHolder.setStatus.setTextColor(viewHolder.setStatus.resources.getColor(R.color.colorPrimary))
                        viewHolder.setProgress.progressDrawable.colorFilter = null
                    }
                    progress < 100 -> {
                        viewHolder.setStatus.setText(R.string.in_progress)
                        viewHolder.setStatus.setTextColor(viewHolder.setStatus.resources.getColor(R.color.colorAccent))
                        viewHolder.setProgress.progressDrawable.colorFilter = null
                    }
                    else -> {
                        viewHolder.setStatus.setText(R.string.completed)
                        viewHolder.setStatus.setTextColor(viewHolder.setStatus.resources.getColor(R.color.green))
                        viewHolder.setProgress.progressDrawable.setColorFilter(viewHolder.setStatus.resources.getColor(R.color.green), PorterDuff.Mode.MULTIPLY)
                    }
                }
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
    val setProgress = itemView.setProgress
    val setStatus = itemView.setStatus
    val subjectName = itemView.subjectName
}
