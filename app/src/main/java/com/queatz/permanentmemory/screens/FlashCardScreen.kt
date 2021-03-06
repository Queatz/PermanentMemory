package com.queatz.permanentmemory.screens


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.queatz.permanentmemory.Extras
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.*
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import kotlinx.android.synthetic.main.screen_flash_card.*

class FlashCardScreen : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            app.on(ThemeManager::class)
                    .inflatorWithThemeFromSettings(this, inflater)
                    .inflate(R.layout.screen_flash_card, container, false)

    private var pendingInk: Runnable? = null
    private var blockButtons = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(Extras.ID) ?: return
        val review = arguments?.getBoolean(Extras.ACTIVITY) ?: false

        on(PlayManager::class).onAnswer = {
            if (!on(PlayManager::class).isReviewing && it.brainSample.correct && it.item.streak >= 10) {
                itemProgressBack.progress = 100
                itemProgressBack.applyColorFromProgress()
                itemProgressBack.visibility = View.VISIBLE
                itemProgressFront.progress = 100
                itemProgressFront.applyColorFromProgress()
                itemProgressFront.visibility = View.VISIBLE
                blockButtons = true
                flashCardFlipView.postDelayed({
                    blockButtons = false
                    on(PlayManager::class).next()
                    flashCardFlipView.postDelayed({
                        itemProgressBack.visibility = View.GONE
                        itemProgressFront.visibility = View.GONE
                    }, 100)
                }, 700)
            } else {
                on(PlayManager::class).next()
            }

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

            it.reviewProgress?.let {
                reviewProgress.visibility = View.VISIBLE
                reviewProgress.progress = it
            } ?: run { reviewProgress.visibility = View.GONE }
        }

        if (!on(PlayManager::class).start(id, review)) {
            AlertDialog.Builder(app.on(ContextManager::class).context)
                    .setMessage(R.string.subject_missing)
                    .setPositiveButton(R.string.bummer) { _: DialogInterface, _: Int -> }
                    .show()
            app.on(NavigationManager::class).fallback()
            return
        }

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