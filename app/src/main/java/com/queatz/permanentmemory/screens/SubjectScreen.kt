package com.queatz.permanentmemory.screens

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.MainActivity
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.adapters.SubjectAdapter
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.models.SubjectModel_
import com.queatz.permanentmemory.pool.on
import kotlinx.android.synthetic.main.screen_subject.*

class SubjectScreen : Fragment() {

    companion object {
        const val ID = "id"
    }

    private var subject: SubjectModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.screen_subject, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getLong(ID)?.let {
            app.on(DataManager::class).box(SubjectModel::class).query().equal(SubjectModel_.objectBoxId, it).build().findFirst()?.let {
                subject = it
                subjectName.text = it.name
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        setRecyclerView.adapter = SubjectAdapter(
                { _ -> (activity as MainActivity).show(PlayScreen(), true)},
                { _ -> (activity as MainActivity).show(SetScreen(), true)},
                {}
        )

        moreButton.setOnClickListener {
            AlertDialog.Builder(activity)
                    .setNeutralButton(R.string.delete) { _: DialogInterface, _: Int ->
                        subject?.let {
                            app.on(DataManager::class).box(SubjectModel::class).remove(it.objectBoxId)
                        }
                    }
                    .setPositiveButton(R.string.update_subject) { _: DialogInterface, _: Int -> }
                    .setView(layoutInflater.inflate(R.layout.modal_subject_settings, null, false))
                    .show()
        }
    }
}
