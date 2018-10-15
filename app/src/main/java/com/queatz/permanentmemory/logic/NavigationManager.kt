package com.queatz.permanentmemory.logic

import android.os.Bundle
import android.support.v4.app.Fragment
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.pool.PoolMember
import com.queatz.permanentmemory.screens.PlayScreen
import com.queatz.permanentmemory.screens.SetScreen
import com.queatz.permanentmemory.screens.SubjectScreen

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

    fun playSet(id: Long) {
        val screen = PlayScreen()
        screen.arguments = Bundle()
        screen.arguments!!.putLong(Extras.ID, id)
        view.invoke(screen, true)
    }

    fun fallback() {
        view.invoke(SubjectScreen(), false)
    }
}