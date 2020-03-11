package com.queatz.permanentmemory.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.ThemeManager
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd

class ProgressScreen : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            app.on(ThemeManager::class)
                    .inflatorWithThemeFromSettings(this, inflater)
                    .inflate(R.layout.screen_progress, container, false)

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}