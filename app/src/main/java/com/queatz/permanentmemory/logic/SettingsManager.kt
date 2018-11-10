package com.queatz.permanentmemory.logic

import com.queatz.permanentmemory.models.SettingsModel
import com.queatz.permanentmemory.pool.PoolMember
import io.objectbox.android.AndroidScheduler

class SettingsManager : PoolMember() {
    fun get(): SettingsModel = on(DataManager::class).box(SettingsModel::class).query().build().findFirst() ?: save(SettingsModel())
    fun observe() = on(DataManager::class).box(SettingsModel::class).query().build().subscribe().on(AndroidScheduler.mainThread()).transform {
        if (it.isEmpty()) null else it[0]
    }
    fun save(settingsModel: SettingsModel): SettingsModel {
        on(DataManager::class).box(SettingsModel::class).removeAll()
        on(DataManager::class).box(SettingsModel::class).put(settingsModel)
        return settingsModel
    }
}