package com.queatz.permanentmemory.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.logic.ProgressManager
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.on
import kotlinx.android.synthetic.main.item_set_item.view.*

class ItemAdapter constructor(
        private val onDeleteClickListener: (item: ItemModel) -> Unit,
        private val onActionClickListener: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isInitialSetLoaded = false
    var subject: SubjectModel? = null

    var items: MutableList<ItemModel> = ArrayList()
        set(value) {
            if (!isInitialSetLoaded) {
                items.clear()
                items.addAll(value)
                notifyDataSetChanged()
                isInitialSetLoaded = true
                return
            }

            val diffResult = DiffUtil.calculateDiff(object : ModelDiffCallback(items, value) {
                override fun areContentsTheSame(oldPosition: Int, newPosition: Int) = true
            })
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
                val item = items[position]
                viewHolder.percentLearnedButton.text = viewHolder.itemView.resources.getString(
                        R.string.percent_learned, app.on(ProgressManager::class).getProgress(item).toString())
                viewHolder.deleteButton.setOnClickListener { onDeleteClickListener.invoke(item) }
                viewHolder.questionText.setText(item.question)
                viewHolder.answerText.setText(item.answer)
                viewHolder.questionText.hint = subject?.name
                viewHolder.answerText.hint = subject?.inverse

                viewHolder.answerTextWatcher = object : TextWatcher {
                    override fun afterTextChanged(text: Editable?) {
                        if (item.answer == text.toString()) return
                        item.answer = text.toString()
                        app.on(DataManager::class).box(ItemModel::class).put(item)
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                }

                viewHolder.questionTextWatcher = object : TextWatcher {
                    override fun afterTextChanged(text: Editable?) {
                        if (item.question == text.toString()) return
                        item.question = text.toString()
                        app.on(DataManager::class).box(ItemModel::class).put(item)
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                }

                viewHolder.questionText.addTextChangedListener(viewHolder.questionTextWatcher)
                viewHolder.answerText.addTextChangedListener(viewHolder.answerTextWatcher)
            }
            is ActionViewHolder -> {
                viewHolder.actionButton.text = viewHolder.actionButton.resources.getText(R.string.add_a_card)
                viewHolder.actionButton.setOnClickListener { onActionClickListener.invoke() }
            }
        }
    }

    override fun onViewRecycled(viewHolder: RecyclerView.ViewHolder) {
        when (viewHolder) {
            is SetViewHolder -> {
                viewHolder.answerText.removeTextChangedListener(viewHolder.answerTextWatcher)
                viewHolder.questionText.removeTextChangedListener(viewHolder.questionTextWatcher)
            }
        }
    }

    override fun getItemViewType(position: Int) = when (position) { itemCount - 1 -> 1 else -> 0 }
}

class SetViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val percentLearnedButton = itemView.percentLearnedButton
    val deleteButton = itemView.deleteButton
    val questionText = itemView.questionText
    val answerText = itemView.answerText
    lateinit var answerTextWatcher: TextWatcher
    lateinit var questionTextWatcher: TextWatcher
}
