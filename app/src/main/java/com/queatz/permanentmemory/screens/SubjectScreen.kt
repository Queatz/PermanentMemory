package com.queatz.permanentmemory.screens

import android.app.AlertDialog
import android.content.DialogInterface
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
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.logic.NavigationManager
import com.queatz.permanentmemory.logic.ScopeManager
import com.queatz.permanentmemory.logic.addToScope
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SetModel_
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.models.SubjectModel_
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import io.objectbox.android.AndroidScheduler
import kotlinx.android.synthetic.main.screen_subject.*

class SubjectScreen : Fragment() {

    private lateinit var subject: SubjectModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.screen_subject, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getLong(Extras.ID)?.let {
            app.on(DataManager::class).box(SubjectModel::class).query().equal(SubjectModel_.objectBoxId, it).build().findFirst()?.let {
                subject = it
                subjectName.text = it.name
            } ?: return
        } ?: return

        setRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val subjectAdapter = SubjectAdapter(
                { app.on(NavigationManager::class).playSet(it.objectBoxId) },
                { app.on(NavigationManager::class).editSet(it.objectBoxId) },
                {
                    app.on(DataManager::class).box(SetModel::class).put(SetModel(subject = subject.objectBoxId, name = "New set"))
                }
        )

        setRecyclerView.adapter = subjectAdapter

        app.on(DataManager::class).box(SetModel::class).query()
                .equal(SetModel_.subject, subject.objectBoxId)
                .build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .observer { subjectAdapter.items = it }
                .addToScope(on(ScopeManager::class))

        moreButton.setOnClickListener {
            AlertDialog.Builder(activity)
                    .setNeutralButton(R.string.delete) { _: DialogInterface, _: Int ->
                        app.on(DataManager::class).box(SubjectModel::class).remove(subject.objectBoxId)
                        app.on(NavigationManager::class).fallback()
                    }
                    .setPositiveButton(R.string.update_subject) { _: DialogInterface, _: Int -> }
                    .setView(layoutInflater.inflate(R.layout.modal_subject_settings, null, false))
                    .show()
        }
    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}
