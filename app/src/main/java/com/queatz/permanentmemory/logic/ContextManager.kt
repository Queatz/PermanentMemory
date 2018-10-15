package com.queatz.permanentmemory.logic

import android.app.Activity
import com.queatz.permanentmemory.pool.PoolMember

class ContextManager : PoolMember() {
    lateinit var context: Activity
}