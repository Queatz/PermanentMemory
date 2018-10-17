package com.queatz.permanentmemory.screens

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.models.*
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import kotlinx.android.synthetic.main.screen_play.*
import java.util.*

class PlayScreen : Fragment() {

    private lateinit var set: SetModel
    private lateinit var subject: SubjectModel
    private lateinit var items: List<ItemModel>
    private lateinit var item: ItemModel
    private var isInverse = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(Extras.ID) ?: return
        set = app.on(DataManager::class).box(SetModel::class).get(id) ?: return
        subject = app.on(DataManager::class).box(SubjectModel::class).get(set.subject) ?: return
        items = app.on(DataManager::class).box(ItemModel::class).find(ItemModel_.set, id)

        submitButton.setOnClickListener {
            submitAnswer(item, answerText.text.toString())
        }

        next()
    }

    private fun submitAnswer(item: ItemModel, answer: String) {
        if (answerText.text.isEmpty()) return

        val brainSample = BrainSampleModel()
        brainSample.set = item.set
        brainSample.item = item.objectBoxId
        brainSample.correct = when (isInverse) { true -> item.question false -> item.answer } == answer
        app.on(DataManager::class).box(BrainSampleModel::class).put(brainSample)

        item.streak = when (brainSample.correct) { true -> item.streak + 1 false -> 0 }
        app.on(DataManager::class).box(ItemModel::class).put(item)

        set.updated = Date()
        subject.updated = Date()
        app.on(DataManager::class).box(SetModel::class).put(set)
        app.on(DataManager::class).box(SubjectModel::class).put(subject)

        next()
    }


    private fun next() {
        if (items.isEmpty()) return

        item = items[Random().nextInt(items.size)]

        answerText.setText("")

        isInverse = Random().nextInt(2) == 0
        if (isInverse) {
            questionText.text = item.answer
            answerText.hint = subject.name
        } else {
            questionText.text = item.question
            answerText.hint = subject.inverse
        }

    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}