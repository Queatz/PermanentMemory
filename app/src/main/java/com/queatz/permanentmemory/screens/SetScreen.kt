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
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.logic.EditorManager
import com.queatz.permanentmemory.logic.ScopeManager
import com.queatz.permanentmemory.logic.addToScope
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.ItemModel_
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SetModel_
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import io.objectbox.android.AndroidScheduler
import io.objectbox.reactive.DataSubscription
import kotlinx.android.synthetic.main.screen_set.*

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
            app.on(DataManager::class).box(ItemModel::class).put(ItemModel(set = set.objectBoxId))
        })

        setRecyclerView.adapter = itemAdapter

        moreButton.setOnClickListener {
            app.on(EditorManager::class).renameSet(set)
        }
    }

    private fun update() {
        setName.text = set.name
        on(ScopeManager::class).clear(itemsSubscription)
        itemsSubscription = app.on(DataManager::class).box(ItemModel::class).query()
                .equal(ItemModel_.set, set.objectBoxId)
                .build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .observer { itemAdapter.items = it }
                .addToScope(on(ScopeManager::class))
    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}
