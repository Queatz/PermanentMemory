package com.queatz.permanentmemory.adapters

import androidx.recyclerview.widget.DiffUtil
import com.queatz.permanentmemory.models.BaseModel

open class ModelDiffCallback(private val oldItems: List<BaseModel>, private val newItems: List<BaseModel>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldItems[oldPosition].objectBoxId == newItems[newPosition].objectBoxId
    }

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int) = false
}
