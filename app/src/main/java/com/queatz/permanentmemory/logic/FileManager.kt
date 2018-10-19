package com.queatz.permanentmemory.logic

import android.app.Activity
import android.content.Intent
import com.queatz.permanentmemory.pool.PoolMember
import java.io.InputStreamReader

class FileManager : PoolMember() {

    private var callback: ((String) -> Unit)? = null

    fun getText(callback: (String) -> Unit) {
        this.callback = callback
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/plain"
        on(ContextManager::class).context.startActivityForResult(intent, 1)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != 1) return
        if (resultCode != Activity.RESULT_OK) return
        data?.data?.let { InputStreamReader(on(ContextManager::class).context.contentResolver.openInputStream(it)) }
                ?.apply { callback?.invoke(this.readText()) }
    }
}
