package com.queatz.permanentmemory.logic

import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.PoolMember
import com.queatz.permanentmemory.pool.on

class InitializationManager : PoolMember() {
    fun initialize() {
        val settings = app.on(SettingsManager::class).get()
        if (!settings.isInitialSubjectLoaded) {
            val subject = SubjectModel("Tiếng Việt", "English")
            subject.objectBoxId = app.on(DataManager::class).box(SubjectModel::class).put(subject)

            val set = SetModel("Numbers", subject.objectBoxId)
            set.objectBoxId = app.on(DataManager::class).box(SetModel::class).put(set)

            val items = arrayListOf(
                    ItemModel(set = set.objectBoxId, question = "Một ", answer = "1"),
                    ItemModel(set = set.objectBoxId, question = "Hai", answer = "2"),
                    ItemModel(set = set.objectBoxId, question = "Ba", answer = "3"),
                    ItemModel(set = set.objectBoxId, question = "Bốn", answer = "4"),
                    ItemModel(set = set.objectBoxId, question = "Năm", answer = "5"),
                    ItemModel(set = set.objectBoxId, question = "Sáu", answer = "6"),
                    ItemModel(set = set.objectBoxId, question = "Bảy", answer = "7"),
                    ItemModel(set = set.objectBoxId, question = "Tám", answer = "8"),
                    ItemModel(set = set.objectBoxId, question = "Chín", answer = "9"),
                    ItemModel(set = set.objectBoxId, question = "Mười", answer = "10"),
                    ItemModel(set = set.objectBoxId, question = "Mười một", answer = "11"),
                    ItemModel(set = set.objectBoxId, question = "Mười hai", answer = "12"),
                    ItemModel(set = set.objectBoxId, question = "Mười ba", answer = "13"),
                    ItemModel(set = set.objectBoxId, question = "Mười bốn", answer = "14"),
                    ItemModel(set = set.objectBoxId, question = "Mười lăm", answer = "15"),
                    ItemModel(set = set.objectBoxId, question = "Mười sáu", answer = "16"),
                    ItemModel(set = set.objectBoxId, question = "Mười bảy", answer = "17"),
                    ItemModel(set = set.objectBoxId, question = "Mười tám", answer = "18"),
                    ItemModel(set = set.objectBoxId, question = "Mười chín", answer = "19"),
                    ItemModel(set = set.objectBoxId, question = "Hai Mười", answer = "20"),
                    ItemModel(set = set.objectBoxId, question = "Hai mươi mốt", answer = "21"),
                    ItemModel(set = set.objectBoxId, question = "Một trăm", answer = "100"),
                    ItemModel(set = set.objectBoxId, question = "Một nghìn", answer = "1000"),
                    ItemModel(set = set.objectBoxId, question = "Một triệu", answer = "1000000")
            )

            app.on(DataManager::class).box(ItemModel::class).put(items)

            settings.isInitialSubjectLoaded = true
            app.on(SettingsManager::class).save(settings)
        }
    }
}
