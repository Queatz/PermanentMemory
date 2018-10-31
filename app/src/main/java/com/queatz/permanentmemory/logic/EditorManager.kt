package com.queatz.permanentmemory.logic

import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.EditText
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.PoolMember
import com.queatz.permanentmemory.pool.on

class EditorManager : PoolMember() {
    fun renameSet(set: SetModel) {
        val view = on(ContextManager::class).context.layoutInflater.inflate(R.layout.modal_set_settings, null, false)
        val setNameEditText = view.findViewById<EditText>(R.id.setName)

        setNameEditText.setText(set.name)
        setNameEditText.requestFocus()
        setNameEditText.selectAll()

        AlertDialog.Builder(on(ContextManager::class).context)
                .setNeutralButton(R.string.delete) { _: DialogInterface, _: Int ->
                    on(ConfirmManager::class).confirm {
                        on(DataManager::class).box(SetModel::class).remove(set.objectBoxId)
                        on(NavigationManager::class).showSubject(set.subject)
                    }
                }
                .setPositiveButton(R.string.rename_set) { _: DialogInterface, _: Int ->
                    set.name = setNameEditText.text.toString()
                    on(DataManager::class).box(SetModel::class).put(set)
                }
                .setView(view)
                .show()
    }

    fun renameSubject(subject: SubjectModel) {
        val view = on(ContextManager::class).context.layoutInflater.inflate(R.layout.modal_subject_settings, null, false)
        val subjectNameEditText = view.findViewById<EditText>(R.id.subjectName)
        val inverseSubjectName = view.findViewById<EditText>(R.id.inverseSubjectName)

        subjectNameEditText.setText(subject.name)
        inverseSubjectName.setText(subject.inverse)
        subjectNameEditText.requestFocus()
        subjectNameEditText.selectAll()

        AlertDialog.Builder(on(ContextManager::class).context)
                .setNeutralButton(R.string.delete) { _: DialogInterface, _: Int ->
                    app.on(ConfirmManager::class).confirm {
                        on(DataManager::class).box(SubjectModel::class).remove(subject.objectBoxId)
                        on(NavigationManager::class).fallback()
                    }
                }
                .setPositiveButton(R.string.update_subject) { _: DialogInterface, _: Int ->
                    subject.name = subjectNameEditText.text.toString()
                    subject.inverse = inverseSubjectName.text.toString()
                    on(DataManager::class).box(SubjectModel::class).put(subject)
                }
                .setView(view)
                .show()
    }

}