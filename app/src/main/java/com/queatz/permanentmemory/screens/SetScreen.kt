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
import com.queatz.permanentmemory.adapters.ItemAdapter
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.logic.NavigationManager
import com.queatz.permanentmemory.logic.ScopeManager
import com.queatz.permanentmemory.logic.addToScope
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.ItemModel_
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SetModel_
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import io.objectbox.android.AndroidScheduler
import kotlinx.android.synthetic.main.screen_set.*

class SetScreen : Fragment() {

    private lateinit var set: SetModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.screen_set, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getLong(Extras.ID)?.let {
            app.on(DataManager::class).box(SetModel::class).query().equal(SetModel_.objectBoxId, it).build().findFirst()?.let {
                set = it
                setName.text = it.name
            } ?: return
        } ?: return


        setRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val itemAdapter = ItemAdapter({
            app.on(DataManager::class).box(ItemModel::class).remove(it.objectBoxId)
        }, {
            app.on(DataManager::class).box(ItemModel::class).put(ItemModel(set = set.objectBoxId))
        })

        setRecyclerView.adapter = itemAdapter

        app.on(DataManager::class).box(ItemModel::class).query()
                .equal(ItemModel_.set, set.objectBoxId)
                .build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .observer { itemAdapter.items = it }
                .addToScope(on(ScopeManager::class))

        moreButton.setOnClickListener {
            AlertDialog.Builder(activity)
                    .setNeutralButton(R.string.delete) { _: DialogInterface, _: Int ->
                        app.on(DataManager::class).box(SetModel::class).remove(set.objectBoxId)
                        app.on(NavigationManager::class).showSubject(set.subject)

                    }
                    .setPositiveButton(R.string.rename_set) { _: DialogInterface, _: Int -> }
                    .setView(layoutInflater.inflate(R.layout.modal_set_settings, null, false))
                    .show()
        }
    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}
