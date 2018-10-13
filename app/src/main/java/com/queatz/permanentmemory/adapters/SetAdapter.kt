package com.queatz.permanentmemory.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.queatz.permanentmemory.R

class SetAdapter : RecyclerView.Adapter<SetViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_set_item, parent, false))

    override fun getItemCount() = 14

    override fun onBindViewHolder(viewHolder: SetViewHolder, position: Int) {

    }
}

class SetViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
