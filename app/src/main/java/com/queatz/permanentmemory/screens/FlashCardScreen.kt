package com.queatz.permanentmemory.screens


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.logic.PlayManager
import com.queatz.permanentmemory.logic.applyColorFromProgress
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import kotlinx.android.synthetic.main.screen_flash_card.*

class FlashCardScreen : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_flash_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(Extras.ID) ?: return
        on(PlayManager::class).start(id)

        submitButton.setOnClickListener { showAnswer(true) }
        submitCorrectButton.setOnClickListener { on(PlayManager::class).submitAnswer(true) }
        submitIncorrectButton.setOnClickListener { on(PlayManager::class).submitAnswer(false) }

        on(PlayManager::class).onAnswer = {
            on(PlayManager::class).next()
        }

        on(PlayManager::class).onNext = {
            showAnswer(false)

            if (it.isInverse) {
                questionText.text = it.item.answer
                answerText.text = it.item.question
                questionTextHint.text = it.subject.inverse
                answerTextHint.text = it.subject.name
            } else {
                questionText.text = it.item.question
                answerText.text = it.item.answer
                questionTextHint.text = it.subject.name
                answerTextHint.text = it.subject.inverse
            }

            setProgress.progress = it.setProgress
            setProgress.applyColorFromProgress()
        }

        on(PlayManager::class).next()
    }

    private fun showAnswer(show: Boolean) {
        if (show) {
            flashCardFlipView.flipDuration = 400
            flashCardFlipView.flipTheView()
            submitButton.visibility = View.GONE
            submitCorrectButton.visibility = View.VISIBLE
            submitIncorrectButton.visibility = View.VISIBLE
        } else {
            flashCardFlipView.flipDuration = 0
            flashCardFlipView.flipTheView()
            submitButton.visibility = View.VISIBLE
            submitCorrectButton.visibility = View.GONE
            submitIncorrectButton.visibility = View.GONE
        }
    }


    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}