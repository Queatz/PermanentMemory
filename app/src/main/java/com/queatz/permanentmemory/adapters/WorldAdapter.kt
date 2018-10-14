package com.queatz.permanentmemory.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.queatz.permanentmemory.R

class WorldAdapter constructor(private val onClickListener: (Int) -> Unit) : RecyclerView.Adapter<WorldViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            WorldViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false))

    override fun getItemCount() = 6

    override fun onBindViewHolder(viewHolder: WorldViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { onClickListener.invoke(position) }
    }
}

class WorldViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
