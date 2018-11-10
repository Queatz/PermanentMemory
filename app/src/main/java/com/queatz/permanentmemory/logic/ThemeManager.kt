package com.queatz.permanentmemory.logic

import android.support.v4.app.Fragment
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.pool.PoolMember


class ThemeManager : PoolMember() {
    fun inflatorWithThemeFromSettings(fragment: Fragment, inflater: LayoutInflater): LayoutInflater {
        val contextThemeWrapper = ContextThemeWrapper(fragment.activity, getThemeStyleRes())
        return inflater.cloneInContext(contextThemeWrapper)
    }

    fun getThemeStyleRes(): Int {
        return when (on(SettingsManager::class).get().darkMode) {
            true -> R.style.AppTheme_NoActionBar_Dark
            false -> R.style.AppTheme_NoActionBar
        }
    }

}
