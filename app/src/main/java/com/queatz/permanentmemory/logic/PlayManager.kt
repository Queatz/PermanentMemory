package com.queatz.permanentmemory.logic

import android.app.AlertDialog
import android.content.DialogInterface
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.models.*
import com.queatz.permanentmemory.pool.PoolMember
import com.queatz.permanentmemory.pool.on
import java.util.*

class PlayManager : PoolMember() {
    private lateinit var set: SetModel
    private lateinit var subject: SubjectModel
    private lateinit var item: ItemModel
    private var isInverse = false
    private var isAlreadyLearned = false

    var onAnswer: ((AnswerResult) -> Unit)? = null
    var onNext: ((OnNextItem) -> Unit)? = null

    fun start(setId: Long) {
        set = app.on(DataManager::class).box(SetModel::class).get(setId) ?: return
        subject = app.on(DataManager::class).box(SubjectModel::class).get(set.subject) ?: return
        isAlreadyLearned = app.on(ProgressManager::class).getProgress(set) >= 100
    }

    fun submitAnswer(answer: String) {
        if (answer.isEmpty()) return
        submitAnswer(when (isInverse) { true -> item.question false -> item.answer }.toLowerCase() == answer.trim().toLowerCase())
    }

    fun submitAnswer(correct: Boolean) {
        val brainSample = BrainSampleModel()
        brainSample.set = item.set
        brainSample.item = item.objectBoxId
        brainSample.correct = correct
        brainSample.inverse = isInverse
        app.on(DataManager::class).box(BrainSampleModel::class).put(brainSample)

        item.streak = when (brainSample.correct) { true -> item.streak + 1 false -> item.streak / 2 }
        app.on(DataManager::class).box(ItemModel::class).put(item)

        set.updated = Date()
        subject.updated = Date()
        app.on(DataManager::class).box(SetModel::class).put(set)
        app.on(DataManager::class).box(SubjectModel::class).put(subject)

        if (!isAlreadyLearned && app.on(ProgressManager::class).getProgress(set) >= 100) {
            isAlreadyLearned = true
            AlertDialog.Builder(app.on(ContextManager::class).context)
                    .setTitle(R.string.learning_complete)
                    .setMessage(R.string.learning_complete_message)
                    .setPositiveButton(R.string.keep_playing) { _: DialogInterface, _: Int -> }
                    .setNegativeButton(R.string.return_to_home) { _: DialogInterface, _: Int ->
                        app.on(NavigationManager::class).fallback()
                    }
                    .show()
        }

        onAnswer?.invoke(AnswerResult(brainSample, isInverse, item))
    }

    fun next() {
        val itemsQuery = app.on(DataManager::class).box(ItemModel::class).query().equal(ItemModel_.set, set.objectBoxId)
        if (!isAlreadyLearned) {
            itemsQuery.less(ItemModel_.streak, 10)
            itemsQuery.orderDesc(ItemModel_.streak)
        }
        val items = itemsQuery.build().find()

        if (items.isEmpty()) return

        val rnd = Random().nextInt(items.size)
        val nextItem = items[if (isAlreadyLearned) rnd else rnd % 10]

        isInverse = when (app.on(SettingsManager::class).get().playMode) {
            PlayMode.RANDOM -> if (::item.isInitialized && nextItem.objectBoxId == item.objectBoxId) { !isInverse } else { Random().nextInt(2) == 0 }
            PlayMode.NORMAL -> false
            PlayMode.INVERSE -> true
        }

        item = nextItem

        set.progress = app.on(ProgressManager::class).getProgress(set)
        app.on(DataManager::class).box(SetModel::class).put(set)

        onNext?.invoke(OnNextItem(set.progress, isInverse, item, subject))
    }

}

data class AnswerResult constructor(
        val brainSample: BrainSampleModel,
        val isInverse: Boolean,
        val item: ItemModel
)

data class OnNextItem constructor(
        val setProgress: Int,
        val isInverse: Boolean,
        val item: ItemModel,
        val subject: SubjectModel
)