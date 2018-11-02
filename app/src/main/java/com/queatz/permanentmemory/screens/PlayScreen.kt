package com.queatz.permanentmemory.screens

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.ContextManager
import com.queatz.permanentmemory.logic.NavigationManager
import com.queatz.permanentmemory.logic.PlayManager
import com.queatz.permanentmemory.logic.applyColorFromProgress
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import kotlinx.android.synthetic.main.screen_play.*

class PlayScreen : Fragment() {

    private var isShowingIncorrectAnswer = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(Extras.ID) ?: return

        on(PlayManager::class).onNext = {
            if (isShowingIncorrectAnswer) {
                isShowingIncorrectAnswer = false
                incorrectIndicator.visibility = View.GONE
                submitButton.setText(R.string.submit)
            }

            answerText.setText("")
            answerText.requestFocus()

            if (it.isInverse) {
                questionText.text = it.item.answer
                answerText.hint = it.subject.name
            } else {
                questionText.text = it.item.question
                answerText.hint = it.subject.inverse
            }

            setProgress.progress = it.setProgress
            setProgress.applyColorFromProgress()

            it.reviewProgress?.let {
                reviewProgress.visibility = View.VISIBLE
                reviewProgress.progress = it
            }
        }

        on(PlayManager::class).onAnswer = {
            if (it.brainSample.correct) {
                correctIndicator.visibility = View.VISIBLE
                correctIndicator.postDelayed({ correctIndicator.visibility = View.GONE }, 500)
                on(PlayManager::class).next()
            } else {
                answerText.setText(when (it.isInverse) { true -> it.item.question false -> it.item.answer })
                answerText.selectAll()
                submitButton.setText(R.string.continue_text)
                incorrectIndicator.visibility = View.VISIBLE
                isShowingIncorrectAnswer = true
            }
        }

        if (!on(PlayManager::class).start(id)) {
            AlertDialog.Builder(app.on(ContextManager::class).context)
                    .setMessage(R.string.subject_missing)
                    .setPositiveButton(R.string.bummer) { _: DialogInterface, _: Int -> }
                    .show()
            app.on(NavigationManager::class).fallback()
            return
        }

        submitButton.setOnClickListener { submitAnswer() }

        answerText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    submitAnswer()
                    true
                }
                else -> false
            }
        }

        answerText.setOnKeyListener { _, keyCode, event ->
            when (event.action) {
                KeyEvent.ACTION_UP -> {
                    when (keyCode) {
                        KeyEvent.KEYCODE_ENTER -> {
                            submitAnswer()
                            return@setOnKeyListener true
                        }
                    }
                }
            }

            false
        }

        on(PlayManager::class).next()
    }

    private fun submitAnswer() {
        if (isShowingIncorrectAnswer) {
            on(PlayManager::class).next()
        } else {
            on(PlayManager::class).submitAnswer(answerText.text.toString())
        }
    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}