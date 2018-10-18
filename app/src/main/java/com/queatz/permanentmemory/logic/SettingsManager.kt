package com.queatz.permanentmemory.logic

import com.queatz.permanentmemory.models.SettingsModel
import com.queatz.permanentmemory.pool.PoolMember

class SettingsManager : PoolMember() {
    fun get(): SettingsModel = on(DataManager::class).box(SettingsModel::class).query().build().findFirst() ?: save(SettingsModel())
    fun save(settingsModel: SettingsModel): SettingsModel {
        on(DataManager::class).box(SettingsModel::class).removeAll()
        on(DataManager::class).box(SettingsModel::class).put(settingsModel)
        return settingsModel
    }
}