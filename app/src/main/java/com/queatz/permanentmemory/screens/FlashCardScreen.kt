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

    private var pendingInk: Runnable? = null
    private var blockButtons = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(Extras.ID) ?: return

        on(PlayManager::class).onAnswer = {
            on(PlayManager::class).next()
        }

        on(PlayManager::class).onNext = {
            showAnswer(false)

            flashCardFlipView.removeCallbacks(pendingInk)
            pendingInk = Runnable {
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
            }
            flashCardFlipView.postDelayed(pendingInk, 100)

            setProgress.progress = it.setProgress
            setProgress.applyColorFromProgress()
        }

        on(PlayManager::class).start(id)

        submitButton.setOnClickListener {
            if (blockButtons) return@setOnClickListener
            showAnswer(true)
        }

        submitCorrectButton.setOnClickListener {
            if (blockButtons) return@setOnClickListener
            on(PlayManager::class).submitAnswer(true)
        }

        submitIncorrectButton.setOnClickListener {
            if (blockButtons) return@setOnClickListener
            on(PlayManager::class).submitAnswer(false)
        }

        on(PlayManager::class).next()
    }

    private fun showAnswer(show: Boolean) {
        blockButtons = true
        flashCardFlipView.setOnFlipListener { _, _ -> blockButtons = false }

        if (show) {
            if (!flashCardFlipView.isFrontSide) {
                flashCardFlipView.flipDuration = 400
                flashCardFlipView.flipTheView()
            }

            submitButton.visibility = View.GONE
            submitCorrectButton.visibility = View.VISIBLE
            submitIncorrectButton.visibility = View.VISIBLE
        } else {
            if (!flashCardFlipView.isBackSide) {
                flashCardFlipView.flipDuration = 200
                flashCardFlipView.flipTheView()
            }

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