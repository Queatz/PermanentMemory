package com.queatz.permanentmemory.logic

import android.os.Bundle
import android.support.v4.app.Fragment
import com.queatz.permanentmemory.pool.PoolMember
import com.queatz.permanentmemory.screens.SubjectScreen

class NavigationManager : PoolMember() {
    lateinit var view: (Fragment, Boolean) -> Unit

    fun showSubject(id: Long) {
        val screen = SubjectScreen()
        screen.arguments = Bundle()
        screen.arguments!!.putLong(SubjectScreen.ID, id)
        view.invoke(screen, false)
    }
}
