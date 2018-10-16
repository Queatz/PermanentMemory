package com.queatz.permanentmemory.screens

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.adapters.SubjectAdapter
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.logic.NavigationManager
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.ItemModel_
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SetModel_
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import io.objectbox.query.OrderFlags.DESCENDING
import kotlinx.android.synthetic.main.screen_home.*

class HomeScreen : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        keepPlayingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        keepPlayingRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val subjectAdapter = SubjectAdapter(
                { app.on(NavigationManager::class).playSet(it.objectBoxId) },
                { app.on(NavigationManager::class).editSet(it.objectBoxId) },
                {}
        )
        subjectAdapter.isActionVisible = false

        app.on(DataManager::class).box(SetModel::class).query()
                .order(SetModel_.updated, DESCENDING)
                .build()
                .findFirst()
                ?.let {
                    subjectAdapter.items = mutableListOf(it)
                }

        keepPlayingRecyclerView.adapter = subjectAdapter

        app.on(DataManager::class).box(ItemModel::class).query()
                .order(ItemModel_.updated, DESCENDING)
                .build()
                .findFirst()
                ?.let {
                    worldOfTheDay.text = it.question
                }
    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}