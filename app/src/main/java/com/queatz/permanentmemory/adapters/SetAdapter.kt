package com.queatz.permanentmemory.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.logic.ProgressManager
import com.queatz.permanentmemory.logic.applyColorFromProgress
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.ItemModel_
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.on
import kotlinx.android.synthetic.main.item_set.view.*

class SetAdapter(
        private val onClickListener: (SetModel) -> Unit,
        private val onLongClickListener: (SetModel) -> Unit,
        private val onReviewClickListener: (SetModel) -> Unit,
        private val onPracticeClickListener: (SetModel) -> Unit,
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
        val resources = viewHolder.itemView.resources

        when (viewHolder) {
            is SubjectViewHolder -> {
                val set = items[position]
                val progress = app.on(ProgressManager::class).getProgress(set)
                viewHolder.cardView.setOnClickListener { onClickListener.invoke(set) }
                viewHolder.cardView.setOnLongClickListener { onLongClickListener.invoke(set); true }
                viewHolder.moreButton.setOnClickListener { onMoreClickListener.invoke(set) }
                viewHolder.setName.text = items[position].name
                viewHolder.setProgress.progress = progress
                viewHolder.setProgress.applyColorFromProgress()

                val setCardCount = app.on(DataManager::class).box(ItemModel::class).query()
                        .equal(ItemModel_.set, set.objectBoxId)
                        .build()
                        .count()
                        .toInt()

                if (showSubjectName) {
                    val subject = app.on(DataManager::class).box(SubjectModel::class).get(set.subject)
                    viewHolder.subjectName.text = resources.getString(R.string.data_line,
                            subject?.name,
                            resources.getQuantityString(R.plurals.num_cards, setCardCount, setCardCount))
                } else {
                    viewHolder.subjectName.text = resources.getQuantityString(R.plurals.num_cards, setCardCount, setCardCount)
                }

                viewHolder.reviewButton.setOnClickListener { onReviewClickListener.invoke(set) }
                viewHolder.practiceButton.setOnClickListener { onPracticeClickListener.invoke(set) }

                when {
                    progress == 0 -> {
                        viewHolder.setStatus.setText(R.string.not_started)
                        viewHolder.setStatus.setTextColor(resources.getColor(R.color.colorPrimary))
                    }
                    progress < 100 -> {
                        viewHolder.setStatus.setText(R.string.in_progress)
                        viewHolder.setStatus.setTextColor(resources.getColor(R.color.colorAccent))
                    }
                    else -> {
                        viewHolder.setStatus.setText(R.string.completed)
                        viewHolder.setStatus.setTextColor(resources.getColor(R.color.green))
                    }
                }
            }
            is ActionViewHolder -> {
                viewHolder.actionButton.text = resources.getText(R.string.add_a_set)
                viewHolder.actionButton.setOnClickListener { onActionClickListener.invoke() }
            }
        }
    }

    override fun getItemViewType(position: Int) = if (!isActionVisible) 0 else when (position) { itemCount - 1 -> 1 else -> 0 }
}

class SubjectViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val cardView = itemView.cardView!!
    val moreButton = itemView.moreButton!!
    val setName = itemView.setName!!
    val setProgress = itemView.setProgress!!
    val setStatus = itemView.setStatus!!
    val subjectName = itemView.subjectName!!
    val reviewButton = itemView.reviewButton!!
    val practiceButton = itemView.practiceButton!!
}
