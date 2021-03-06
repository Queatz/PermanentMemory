package com.queatz.permanentmemory.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.adapters.FlashCardAdapter
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.ContextManager
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.logic.SettingsManager
import com.queatz.permanentmemory.logic.ThemeManager
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.ItemModel_
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import kotlinx.android.synthetic.main.screen_practice.*

class PracticeScreen : Fragment() {

    private lateinit var set: SetModel
    private lateinit var subject: SubjectModel

    override fun onResume() {
        super.onResume()
        app.on(ContextManager::class).context.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onPause() {
        super.onPause()
        app.on(ContextManager::class).context.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            app.on(ThemeManager::class)
                    .inflatorWithThemeFromSettings(this, inflater)
                    .inflate(R.layout.screen_practice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(Extras.ID) ?: return

        set = app.on(DataManager::class).box(SetModel::class).get(id) ?: return
        subject = app.on(DataManager::class).box(SubjectModel::class).get(set.subject) ?: return

        practiceRecyclerView.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        val adapter = FlashCardAdapter(subject, app.on(SettingsManager::class).get().playMode)
        practiceRecyclerView.adapter = adapter

        adapter.items = app.on(DataManager::class).box(ItemModel::class).query()
                .equal(ItemModel_.set, set.objectBoxId)
                .build()
                .find()
    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}