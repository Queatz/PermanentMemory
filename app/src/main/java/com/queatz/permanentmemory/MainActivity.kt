package com.queatz.permanentmemory

import android.app.Service
import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.recyclerview.widget.LinearLayoutManager
import com.queatz.permanentmemory.adapters.SubjectAdapter
import com.queatz.permanentmemory.logic.*
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import io.objectbox.android.AndroidScheduler
import io.objectbox.reactive.DataSubscription
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_main.*

class MainActivity : FragmentActivity() {

    private var settingsObserver: DataSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.on(ContextManager::class).context = this
        app.on(NavigationManager::class).view = this::show

        setContentView(R.layout.activity_main)

        menuButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        worldRecyclerView. layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val worldAdapter = SubjectAdapter({
            app.on(NavigationManager::class).showSubject(it.objectBoxId)
            drawerLayout.closeDrawer(GravityCompat.START)
        }, {
            app.on(DataManager::class).box(SubjectModel::class).put(SubjectModel(name = "New subject"))
        })
        worldRecyclerView.adapter = worldAdapter

        app.on(DataManager::class).box(SubjectModel::class).query().build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .observer { worldAdapter.items = it }
                .addToScope(on(ScopeManager::class))

        app.on(NavigationManager::class).fallback()

        drawerTitle.setOnClickListener {
            app.on(NavigationManager::class).fallback()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        sendFeedbackButton.setOnClickListener { app.on(EmailManager::class).sendFeedback() }

        app.on(InitializationManager::class).initialize()

        settingsObserver = app.on(SettingsManager::class).observe().observer {
            it?.darkMode.apply {
                when (this) {
                    true -> drawerLayout.setBackgroundResource(R.color.windowBackgroundDark)
                    false -> drawerLayout.setBackgroundResource(R.color.windowBackground)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            clearBackStack()
            super.onBackPressed()
        }
    }

    private fun clearBackStack() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            supportFragmentManager.fragments.forEach { fragmentTransaction.remove(it) }
            fragmentTransaction.commit()
        }
    }

    private fun show(fragment: Fragment, addToBackStack: Boolean = false) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        } else {
            supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
        }

        fragmentTransaction.replace(R.id.contentFrame, fragment)
        fragmentTransaction.commit()

        showKeyboard(findViewById(android.R.id.content), false)
    }

    private fun showKeyboard(view: View, show: Boolean) {
        val inputMethodManager = getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager

        when (show) {
            true -> inputMethodManager.showSoftInput(view, 0)
            false -> inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDestroy() {
        onEnd()
        settingsObserver?.cancel()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        app.on(FileManager::class).onActivityResult(requestCode, resultCode, data)
    }
}

const val app = 1