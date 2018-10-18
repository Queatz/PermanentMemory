package com.queatz.permanentmemory.logic

import com.queatz.permanentmemory.pool.PoolMember
import io.objectbox.reactive.DataSubscription

class ScopeManager : PoolMember() {
    private val dataSubscriptions: MutableSet<DataSubscription> = HashSet()

    fun add(dataSubscription: DataSubscription) {
        dataSubscriptions.add(dataSubscription)
    }

    fun clear() {
        dataSubscriptions.forEach { it.cancel() }
        dataSubscriptions.clear()
    }

    fun clear(dataSubscription: DataSubscription?) {
        dataSubscriptions.remove(dataSubscription)
        dataSubscription?.cancel()
    }

    override fun onPoolEnd() {
        clear()
    }
}

fun DataSubscription.addToScope(scope: ScopeManager): DataSubscription {
    scope.add(this)
    return this
}