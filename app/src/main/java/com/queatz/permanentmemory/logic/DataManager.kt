package com.queatz.permanentmemory.logic

import com.queatz.permanentmemory.models.BaseModel
import com.queatz.permanentmemory.models.MyObjectBox
import com.queatz.permanentmemory.pool.PoolMember
import io.objectbox.BoxStore
import kotlin.reflect.KClass

class DataManager : PoolMember() {

    private lateinit var database: BoxStore

    override fun onPoolInit() {
        database = MyObjectBox.builder().androidContext(on(ContextManager::class).context).build()
    }

    fun <T : BaseModel> box(clazz: KClass<T>) = database.boxFor(clazz.java)

}