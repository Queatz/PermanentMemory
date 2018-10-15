package com.queatz.permanentmemory.logic

import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.EditText
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.pool.PoolMember

class EditorManager : PoolMember() {
    fun renameSet(set: SetModel) {
        val view = on(ContextManager::class).context.layoutInflater.inflate(R.layout.modal_set_settings, null, false)
        val setNameEditText = view.findViewById<EditText>(R.id.setName)

        setNameEditText.setText(set.name)
        setNameEditText.requestFocus()

        AlertDialog.Builder(on(ContextManager::class).context)
                .setNeutralButton(R.string.delete) { _: DialogInterface, _: Int ->
                    on(DataManager::class).box(SetModel::class).remove(set.objectBoxId)
                    on(NavigationManager::class).showSubject(set.subject)

                }
                .setPositiveButton(R.string.rename_set) { _: DialogInterface, _: Int ->
                    set.name = setNameEditText.text.toString()
                    on(DataManager::class).box(SetModel::class).put(set)
                }
                .setView(view)
                .show()
    }

}