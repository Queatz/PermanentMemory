package com.queatz.permanentmemory.logic

import android.content.Context
import com.queatz.permanentmemory.pool.PoolMember

class ContextManager : PoolMember() {
    lateinit var context: Context
}