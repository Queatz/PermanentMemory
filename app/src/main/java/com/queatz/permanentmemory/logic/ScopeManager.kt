package com.queatz.permanentmemory.logic

import com.queatz.permanentmemory.pool.PoolMember
import io.objectbox.reactive.DataSubscription

class ScopeManager : PoolMember() {
    private val dataSubscriptions: MutableSet<DataSubscription> = HashSet()

    fun add(dataSubscription: DataSubscription) {
        dataSubscriptions.add(dataSubscription)
    }

    override fun onPoolEnd() {
        dataSubscriptions.forEach { it.cancel() }
    }
}

fun DataSubscription.addToScope(scope: ScopeManager) {
    scope.add(this)
}