package com.queatz.permanentmemory.logic

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.PoolMember

class ImportManager : PoolMember() {

    private val gson = Gson()

    fun export() {
        val db = on(DataManager::class)

        val json = gson.toJson(ExportData(
            db.box(SubjectModel::class).all,
            db.box(SetModel::class).all,
            db.box(ItemModel::class).all
        ))

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_SUBJECT, "permanent_memory.json")
        intent.putExtra(Intent.EXTRA_TEXT, json)
        intent.type = "text/plain"
        on(ContextManager::class).context.startActivity(Intent.createChooser(intent, on(ContextManager::class).context.getString(R.string.save_to)))

    }

    fun import() {
        on(FileManager::class).getText {
            try {
                gson.fromJson(it, ExportData::class.java)?.apply {
                    val db = on(DataManager::class)

                    try {
                        db.box(SubjectModel::class).put(subjects)
                        db.box(SetModel::class).put(sets)
                        db.box(ItemModel::class).put(items)
                    } catch (exception: IllegalArgumentException) {
                        showError(exception)
                        return@getText
                    }

                    AlertDialog.Builder(on(ContextManager::class).context)
                            .setTitle(R.string.import_successful)
                            .setPositiveButton(R.string.ok) { _: DialogInterface, _: Int -> }
                            .show()
                }
            } catch (exception: JsonSyntaxException) {
                showError(exception)
            }
        }
    }

    private fun showError(exception: Exception) {
        AlertDialog.Builder(on(ContextManager::class).context)
                .setTitle(R.string.import_error)
                .setMessage(exception.message)
                .setPositiveButton(R.string.ok) { _: DialogInterface, _: Int -> }
                .show()
    }
}

data class ExportData (
        val subjects: List<SubjectModel>,
        val sets: List<SetModel>,
        val items: List<ItemModel>
)