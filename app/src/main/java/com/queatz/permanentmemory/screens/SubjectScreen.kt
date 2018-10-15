package com.queatz.permanentmemory.screens

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.adapters.SubjectAdapter
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
    private lateinit var subjectAdapter: SubjectAdapter
    private var setsSubscription: DataSubscription? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.screen_subject, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(Extras.ID) ?: return

        app.on(DataManager::class).box(SubjectModel::class).query().equal(SubjectModel_.objectBoxId, id).build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .observer {
                    it[0]?.let {
                        subject = it
                        update()
                    }
                }
                .addToScope(on(ScopeManager::class))

        setRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        subjectAdapter = SubjectAdapter(
                { app.on(NavigationManager::class).playSet(it.objectBoxId) },
                { app.on(NavigationManager::class).editSet(it.objectBoxId) },
                {
                    app.on(DataManager::class).box(SetModel::class).put(SetModel(subject = subject.objectBoxId, name = "New set"))
                }
        )

        setRecyclerView.adapter = subjectAdapter

        moreButton.setOnClickListener {
            app.on(EditorManager::class).renameSubject(subject)
        }
    }

    private fun update() {
        subjectName.text = subject.name
        on(ScopeManager::class).clear(setsSubscription)
        setsSubscription = app.on(DataManager::class).box(SetModel::class).query()
                .equal(SetModel_.subject, subject.objectBoxId)
                .build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .observer { subjectAdapter.items = it }
                .addToScope(on(ScopeManager::class))
    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}
