package com.queatz.permanentmemory.screens

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.adapters.SetAdapter
import kotlinx.android.synthetic.main.screen_set.*

class SetScreen : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.screen_set, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        setRecyclerView.adapter = SetAdapter()

        moreButton.setOnClickListener {
            AlertDialog.Builder(activity)
                    .setPositiveButton(R.string.rename_set) { _: DialogInterface, _: Int -> }
                    .setView(layoutInflater.inflate(R.layout.modal_set_settings, null, false))
                    .show()
        }
    }
}
