package com.queatz.permanentmemory.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.queatz.permanentmemory.R

class SubjectAdapter constructor(
        private val onClickListener: (Int) -> Unit,
        private val onMoreClickListener: (Int) -> Unit) : RecyclerView.Adapter<SubjectViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SubjectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false))

    override fun getItemCount() = 4

    override fun onBindViewHolder(viewHolder: SubjectViewHolder, position: Int) {
        viewHolder.cardView.setOnClickListener { onClickListener.invoke(position) }
        viewHolder.moreButton.setOnClickListener { onMoreClickListener.invoke(position) }
    }
}

class SubjectViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val cardView = itemView.findViewById<View>(R.id.cardView)!!
    val moreButton = itemView.findViewById<View>(R.id.moreButton)!!
}
