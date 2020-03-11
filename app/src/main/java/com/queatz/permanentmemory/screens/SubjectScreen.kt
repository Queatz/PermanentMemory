package com.queatz.permanentmemory.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.adapters.SetAdapter
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.*
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SetModel_
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.models.SubjectModel_
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import io.objectbox.android.AndroidScheduler
import io.objectbox.reactive.DataSubscription
import kotlinx.android.synthetic.main.screen_subject.*

class SubjectScreen : Fragment() {

    private lateinit var subject: SubjectModel
    private lateinit var setAdapter: SetAdapter
    private var setsSubscription: DataSubscription? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            app.on(ThemeManager::class)
                    .inflatorWithThemeFromSettings(this, inflater)
                    .inflate(R.layout.screen_subject, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(Extras.ID) ?: return

        app.on(DataManager::class).box(SubjectModel::class).query().equal(SubjectModel_.objectBoxId, id).build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .observer {
                    if (it.isNotEmpty()) {
                        subject = it[0]
                        update()
                    }
                }
                .addToScope(on(ScopeManager::class))

        setRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        setAdapter = SetAdapter(
                { app.on(NavigationManager::class).playSet(it.objectBoxId) },
                { app.on(NavigationManager::class).playSet(it.objectBoxId, review = true) },
                { app.on(NavigationManager::class).playSet(it.objectBoxId, review = true) },
                { app.on(NavigationManager::class).practiceSet(it.objectBoxId) },
                { app.on(NavigationManager::class).editSet(it.objectBoxId) },
                {
                    val setId = app.on(DataManager::class).box(SetModel::class).put(SetModel(subject = subject.objectBoxId, name = "New set"))
                    app.on(NavigationManager::class).editSet(setId)
                }
        )

        setRecyclerView.adapter = setAdapter

        moreButton.setOnClickListener {
            app.on(EditorManager::class).renameSubject(subject)
        }
    }

    private fun update() {
        view ?: return

        val progress = app.on(ProgressManager::class).getProgress(subject)
        subjectProgress.progress = progress
        subjectProgress.applyColorFromProgress()

        subjectName.text = subject.name
        on(ScopeManager::class).clear(setsSubscription)
        setsSubscription = app.on(DataManager::class).box(SetModel::class).query()
                .equal(SetModel_.subject, subject.objectBoxId)
                .order(SetModel_.progress)
                .build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .observer { setAdapter.items = it }
                .addToScope(on(ScopeManager::class))
    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}
