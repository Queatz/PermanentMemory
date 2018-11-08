package com.queatz.permanentmemory.logic

import android.os.Bundle
import android.support.v4.app.Fragment
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.ItemModel_
import com.queatz.permanentmemory.pool.PoolMember
import com.queatz.permanentmemory.screens.*

class NavigationManager : PoolMember() {
    lateinit var view: (Fragment, Boolean) -> Unit

    fun showSubject(id: Long) {
        val screen = SubjectScreen()
        screen.arguments = Bundle()
        screen.arguments!!.putLong(Extras.ID, id)
        view.invoke(screen, false)
    }

    fun editSet(id: Long) {
        val screen = SetScreen()
        screen.arguments = Bundle()
        screen.arguments!!.putLong(Extras.ID, id)
        view.invoke(screen, true)
    }

    fun playSet(id: Long, review: Boolean = false) {
        val itemCount = on(DataManager::class).box(ItemModel::class).query()
                .equal(ItemModel_.set, id)
                .build()
                .count()

        if (itemCount < 1) {
            editSet(id)
            return
        }

        val gameMode = on(SettingsManager::class).get().gameMode
        val screen = when(gameMode) { GameMode.FLASH_CARD -> FlashCardScreen() else -> PlayScreen() }
        screen.arguments = Bundle()
        screen.arguments!!.putLong(Extras.ID, id)
        screen.arguments!!.putBoolean(Extras.ACTIVITY, review)
        view.invoke(screen, true)
    }

    fun practiceSet(id: Long) {
        val screen = PracticeScreen()
        screen.arguments = Bundle()
        screen.arguments!!.putLong(Extras.ID, id)
        view.invoke(screen, true)
    }

    fun fallback() {
        view.invoke(HomeScreen(), false)
    }
}
