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
                    ItemModel(set = set.objectBoxId, question = "Một ", answer = "One"),
                    ItemModel(set = set.objectBoxId, question = "Hai", answer = "Two"),
                    ItemModel(set = set.objectBoxId, question = "Ba", answer = "Three"),
                    ItemModel(set = set.objectBoxId, question = "Bốn", answer = "Four"),
                    ItemModel(set = set.objectBoxId, question = "Năm", answer = "Five"),
                    ItemModel(set = set.objectBoxId, question = "Sáu", answer = "Six"),
                    ItemModel(set = set.objectBoxId, question = "Bảy", answer = "Seven"),
                    ItemModel(set = set.objectBoxId, question = "Tám", answer = "Eight"),
                    ItemModel(set = set.objectBoxId, question = "Chín", answer = "Nine"),
                    ItemModel(set = set.objectBoxId, question = "Mười", answer = "Ten"),
                    ItemModel(set = set.objectBoxId, question = "Mười một", answer = "Eleven"),
                    ItemModel(set = set.objectBoxId, question = "Mười hai", answer = "Twelve"),
                    ItemModel(set = set.objectBoxId, question = "Mười ba", answer = "Thirteen"),
                    ItemModel(set = set.objectBoxId, question = "Mười bốn", answer = "Fourteen"),
                    ItemModel(set = set.objectBoxId, question = "Mười lăm", answer = "Fifteen"),
                    ItemModel(set = set.objectBoxId, question = "Mười sáu", answer = "Sixteen"),
                    ItemModel(set = set.objectBoxId, question = "Mười bảy", answer = "Seventeen"),
                    ItemModel(set = set.objectBoxId, question = "Mười tám", answer = "Eighteen"),
                    ItemModel(set = set.objectBoxId, question = "Mười chín", answer = "Nineteen"),
                    ItemModel(set = set.objectBoxId, question = "Hai Mười", answer = "Twenty"),
                    ItemModel(set = set.objectBoxId, question = "Hai mươi mốt", answer = "Twenty one"),
                    ItemModel(set = set.objectBoxId, question = "Một trăm", answer = "One hundred"),
                    ItemModel(set = set.objectBoxId, question = "Một nghìn", answer = "One thousand"),
                    ItemModel(set = set.objectBoxId, question = "Một triệu", answer = "One million")
            )

            app.on(DataManager::class).box(ItemModel::class).put(items)

            settings.isInitialSubjectLoaded = true
            app.on(SettingsManager::class).save(settings)
        }
    }
}
