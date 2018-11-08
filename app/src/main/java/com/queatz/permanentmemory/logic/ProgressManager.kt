package com.queatz.permanentmemory.logic

import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.models.*
import com.queatz.permanentmemory.pool.PoolMember
import com.queatz.permanentmemory.pool.on
import java.util.*
import kotlin.math.min

class ProgressManager : PoolMember() {
    fun getProgress(subject: SubjectModel): Int {
        var progress = 0
        var sets = app.on(DataManager::class).box(SetModel::class).query()
                .equal(SetModel_.subject, subject.objectBoxId)
                .build()
                .find()

        if (sets.isEmpty()) return 0

        sets.forEach { progress += getProgress(it) }
        progress /= sets.size
        return progress
    }

    fun getProgress(set: SetModel): Int {
        var progress = 0
        var items = app.on(DataManager::class).box(ItemModel::class).query()
                .equal(ItemModel_.set, set.objectBoxId)
                .build()
                .find()

        if (items.isEmpty()) return 0

        items.forEach { progress += getProgress(it) }
        progress /= items.size
        return progress
    }

    fun getProgress(itemModel: ItemModel): Int {
        return min(100, itemModel.streak.toInt() * 10)
    }

    fun update(set: SetModel) {
        set.updated = Date()
        set.progress = app.on(ProgressManager::class).getProgress(set)
        app.on(DataManager::class).box(SetModel::class).put(set)

        val subject = on(DataManager::class).box(SubjectModel::class).get(set.subject)
        subject.updated = Date()
        app.on(DataManager::class).box(SubjectModel::class).put(subject)
    }
}
