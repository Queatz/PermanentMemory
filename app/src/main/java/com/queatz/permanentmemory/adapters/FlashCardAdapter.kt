package com.queatz.permanentmemory.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.PlayMode
import com.queatz.permanentmemory.logic.ProgressManager
import com.queatz.permanentmemory.logic.applyColorFromProgress
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.on
import kotlinx.android.synthetic.main.item_flash_card.view.*
import java.util.*

class FlashCardAdapter constructor(
        private val subject: SubjectModel,
        private val playMode: PlayMode) : RecyclerView.Adapter<FlashCardViewHolder>() {

    var items = mutableListOf<ItemModel>()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            FlashCardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_flash_card, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(viewHolder: FlashCardViewHolder, position: Int) {
        var isInverse = when (playMode) {
            PlayMode.RANDOM -> Random().nextBoolean()
            PlayMode.NORMAL -> false
            PlayMode.INVERSE -> true
        }
        val item = items[position]

        if (viewHolder.flashCardFlipView.isBackSide) {
            isInverse = !isInverse
        }

        if (isInverse) {
            viewHolder.questionText.text = item.question
            viewHolder.answerText.text = item.answer
            viewHolder.questionTextHint.text = subject.name
            viewHolder.answerTextHint.text = subject.inverse
        } else {
            viewHolder.questionText.text = item.answer
            viewHolder.answerText.text = item.question
            viewHolder.questionTextHint.text = subject.inverse
            viewHolder.answerTextHint.text = subject.name
        }

        viewHolder.cardBack.setOnClickListener { viewHolder.flashCardFlipView.flipTheView() }
        viewHolder.cardFront.setOnClickListener { viewHolder.flashCardFlipView.flipTheView() }

        val itemProgress = app.on(ProgressManager::class).getProgress(item)
        viewHolder.itemProgressBack.progress = itemProgress
        viewHolder.itemProgressBack.applyColorFromProgress()
        viewHolder.itemProgressFront.progress = itemProgress
        viewHolder.itemProgressFront.applyColorFromProgress()
    }
}

class FlashCardViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val questionTextHint = itemView.questionTextHint!!
    val questionText = itemView.questionText!!
    val answerTextHint = itemView.answerTextHint!!
    val answerText = itemView.answerText!!
    val cardBack = itemView.cardBack!!
    val cardFront = itemView.cardFront!!
    val flashCardFlipView = itemView.flashCardFlipView!!
    val itemProgressBack = itemView.itemProgressBack!!
    val itemProgressFront = itemView.itemProgressFront!!
}