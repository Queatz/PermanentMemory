package com.queatz.permanentmemory.screens

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.adapters.ItemAdapter
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.*
import com.queatz.permanentmemory.models.*
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import io.objectbox.android.AndroidScheduler
import io.objectbox.reactive.DataSubscription
import kotlinx.android.synthetic.main.screen_set.*
import java.util.*

class SetScreen : Fragment() {

    private lateinit var set: SetModel
    private lateinit var itemAdapter: ItemAdapter
    private var itemsSubscription: DataSubscription? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.screen_set, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(Extras.ID) ?: return

        app.on(DataManager::class).box(SetModel::class).query().equal(SetModel_.objectBoxId, id).build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .observer {
                    it[0]?.let {
                        set = it
                        update()
                    }
                }
                .addToScope(on(ScopeManager::class))

        setRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        itemAdapter = ItemAdapter({
            app.on(DataManager::class).box(ItemModel::class).remove(it.objectBoxId)
        }, {
            app.on(DataManager::class).box(ItemModel::class).put(ItemModel(set = set.objectBoxId, streak = 0))

            val subject = app.on(DataManager::class).box(SubjectModel::class).get(set.subject)

            subject?.let {
                it.updated = Date()
                app.on(DataManager::class).box(SubjectModel::class).put(it)
            }

            set.updated = Date()
            app.on(DataManager::class).box(SetModel::class).put(set)
        })

        setRecyclerView.adapter = itemAdapter

        moreButton.setOnClickListener {
            app.on(EditorManager::class).renameSet(set)
        }

        playButton.setOnClickListener { app.on(NavigationManager::class).playSet(set.objectBoxId) }
    }

    private fun update() {
        view?: return

        itemAdapter.subject = app.on(DataManager::class).box(SubjectModel::class).get(set.subject)
        setName.text = set.name
        on(ScopeManager::class).clear(itemsSubscription)
        itemsSubscription = app.on(DataManager::class).box(ItemModel::class).query()
                .equal(ItemModel_.set, set.objectBoxId)
                .build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .observer {
                    itemAdapter.items = it
                    setCardCount?.text = resources.getQuantityString(R.plurals.num_cards, it.size, it.size)
                }
                .addToScope(on(ScopeManager::class))
    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}
