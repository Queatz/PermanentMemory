package com.queatz.permanentmemory.screens

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.ItemModel_
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import kotlinx.android.synthetic.main.screen_play.*
import java.util.*

class PlayScreen : Fragment() {

    private lateinit var set: SetModel
    private lateinit var subject: SubjectModel
    private lateinit var items: List<ItemModel>
    private lateinit var item: ItemModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(Extras.ID) ?: return
        set = app.on(DataManager::class).box(SetModel::class).get(id) ?: return
        subject = app.on(DataManager::class).box(SubjectModel::class).get(set.subject) ?: return
        items = app.on(DataManager::class).box(ItemModel::class).find(ItemModel_.set, id)

        submitButton.setOnClickListener { next() }

        next()
    }

    private fun next() {
        item = items[Random().nextInt(items.size)]

        answerText.setText("")

        if (Random().nextInt(2) == 0) {
            questionText.text = item.question
            answerText.hint = subject.inverse
        } else {
            questionText.text = item.answer
            answerText.hint = subject.name
        }

    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}