package com.queatz.permanentmemory.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.adapters.SetAdapter
import com.queatz.permanentmemory.app
import com.queatz.permanentmemory.logic.*
import com.queatz.permanentmemory.models.ItemModel
import com.queatz.permanentmemory.models.SetModel
import com.queatz.permanentmemory.models.SetModel_
import com.queatz.permanentmemory.models.SettingsModel
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import io.objectbox.android.AndroidScheduler
import io.objectbox.query.OrderFlags.DESCENDING
import kotlinx.android.synthetic.main.screen_home.*

class HomeScreen : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        app.on(ThemeManager::class)
                .inflatorWithThemeFromSettings(this, inflater)
                .inflate(R.layout.screen_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titleTextView.isFocusable = true
        titleTextView.requestFocus()
        keepPlayingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        keepPlayingRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val subjectAdapter = SetAdapter(
                { app.on(NavigationManager::class).playSet(it.objectBoxId) },
                { app.on(NavigationManager::class).playSet(it.objectBoxId, true) },
                { app.on(NavigationManager::class).playSet(it.objectBoxId, true) },
                { app.on(NavigationManager::class).practiceSet(it.objectBoxId) },
                { app.on(NavigationManager::class).editSet(it.objectBoxId) },
                {},
                showSubjectName = true
        )
        subjectAdapter.isActionVisible = false

        app.on(DataManager::class).box(SetModel::class).query()
                .less(SetModel_.progress, 100)
                .order(SetModel_.updated, DESCENDING)
                .build()
                .find()
                .let { subjectAdapter.items = it.asSequence().take(3).toMutableList() }

        keepPlayingRecyclerView.adapter = subjectAdapter
        keepPlayingRecyclerView.isNestedScrollingEnabled = false

        app.on(SettingsManager::class).get().wordOfTheDay?.let {
            app.on(DataManager::class).box(ItemModel::class).get(it)?.let { item ->
                playWordOfTheDayButton.setOnClickListener { app.on(NavigationManager::class).playSet(item.set) }

                worldOfTheDay.text = item.question
                wordOfTheDayProgress.progress = app.on(ProgressManager::class).getProgress(item)
                wordOfTheDayProgress.applyColorFromProgress()

                val animation = AlphaAnimation(1f, 0f)
                animation.duration = 500L
                animation.startOffset = 7500L
                animation.repeatCount = Animation.INFINITE
                animation.repeatMode = Animation.REVERSE
                animation.setAnimationListener(object : Animation.AnimationListener {

                    private var flip = false

                    override fun onAnimationRepeat(animation: Animation) {
                        val hidden = animation.startOffset == 7500L
                        animation.startOffset = if (hidden) 0L else 7500L

                        if (hidden) {
                            if (flip) {
                                worldOfTheDay.text = item.question
                            } else {
                                worldOfTheDay.text = item.answer
                            }

                            flip = !flip
                        }
                    }

                    override fun onAnimationEnd(animation: Animation) {
                    }

                    override fun onAnimationStart(animation: Animation) {
                    }
                })
                worldOfTheDay.startAnimation(animation)
            }
        }

        playModeRandom.setOnClickListener { app.on(SettingsManager::class).get().apply {
            this.playMode = PlayMode.RANDOM
            app.on(SettingsManager::class).save(this)
        } }
        playModeNormal.setOnClickListener { app.on(SettingsManager::class).get().apply {
            this.playMode = PlayMode.NORMAL
            app.on(SettingsManager::class).save(this)
        } }
        playModeInverse.setOnClickListener { app.on(SettingsManager::class).get().apply {
            this.playMode = PlayMode.INVERSE
            app.on(SettingsManager::class).save(this)
        } }
        playTypeText.setOnClickListener { app.on(SettingsManager::class).get().apply {
            this.gameMode = GameMode.TEXT
            app.on(SettingsManager::class).save(this)
        } }
        playTypeFlashCard.setOnClickListener { app.on(SettingsManager::class).get().apply {
            this.gameMode = GameMode.FLASH_CARD
            app.on(SettingsManager::class).save(this)
        } }

        app.on(DataManager::class).box(SettingsModel::class).query().build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .observer {
                    if (getView() == null) return@observer
                    app.on(SettingsManager::class).get().playMode.let {
                        playModeRandom.setTextColor(resources.getColor(if (it == PlayMode.RANDOM) R.color.colorAccent else R.color.colorPrimary))
                        playModeNormal.setTextColor(resources.getColor(if (it == PlayMode.NORMAL) R.color.colorAccent else R.color.colorPrimary))
                        playModeInverse.setTextColor(resources.getColor(if (it == PlayMode.INVERSE) R.color.colorAccent else R.color.colorPrimary))
                    }
                    app.on(SettingsManager::class).get().gameMode.let {
                        playTypeText.setTextColor(resources.getColor(if (it == GameMode.TEXT) R.color.colorAccent else R.color.colorPrimary))
                        playTypeFlashCard.setTextColor(resources.getColor(if (it == GameMode.FLASH_CARD) R.color.colorAccent else R.color.colorPrimary))
                    }
                }
                .addToScope(app.on(ScopeManager::class))

        exportButton.setOnClickListener { app.on(ImportManager::class).export() }
        importButton.setOnClickListener { app.on(ImportManager::class).import() }

        darkModeSwitch.isChecked = app.on(SettingsManager::class).get().darkMode

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            app.on(SettingsManager::class).get().apply {
                darkMode = isChecked
                app.on(SettingsManager::class).save(this)
                app.on(NavigationManager::class).fallback()
            }

        }
    }

    override fun onDestroy() {
        onEnd()
        super.onDestroy()
    }
}