package com.queatz.permanentmemory.screens

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.ContextManager
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.logic.NavigationManager
import com.queatz.permanentmemory.logic.ProgressManager
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
    private var isAlreadyLearned = false

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

        isAlreadyLearned = app.on(ProgressManager::class).getProgress(set) >= 100

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

        if (!isAlreadyLearned && app.on(ProgressManager::class).getProgress(set) >= 100) {
            AlertDialog.Builder(app.on(ContextManager::class).context)
                    .setTitle(R.string.learning_complete)
                    .setMessage(R.string.learning_complete_message)
                    .setPositiveButton(R.string.keep_playing) { _: DialogInterface, _: Int -> }
                    .setNegativeButton(R.string.return_to_home) { _: DialogInterface, _: Int ->
                        app.on(NavigationManager::class).fallback()
                    }
                    .show()
        }

        if (brainSample.correct) {
            correctIndicator.visibility = View.VISIBLE
            correctIndicator.postDelayed({ correctIndicator.visibility = View.GONE }, 500)
        } else {
            incorrectIndicator.visibility = View.VISIBLE
            incorrectIndicator.postDelayed({ incorrectIndicator.visibility = View.GONE }, 500)
        }

        next()
    }


    private fun next() {
        if (items.isEmpty()) return

        item = items[Random().nextInt(items.size)]

        answerText.setText("")
        answerText.requestFocus()

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