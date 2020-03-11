package com.queatz.permanentmemory.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.queatz.permanentmemory.R

class ActionViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val actionButton = itemView.findViewById<TextView>(R.id.actionButton)!!
}