package com.queatz.permanentmemory.logic

import android.app.AlertDialog
import android.content.DialogInterface
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.pool.PoolMember

class ConfirmManager : PoolMember() {
    fun confirm(action: () -> Unit) {
        AlertDialog.Builder(on(ContextManager::class).context)
                .setTitle(R.string.confirm_pls)
                .setMessage(R.string.you_cannot_undo)
                .setNegativeButton(R.string.cancel) { _: DialogInterface, _: Int -> }
                .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                    action.invoke()
                }
                .show()
    }
}