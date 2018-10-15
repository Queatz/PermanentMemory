package com.queatz.permanentmemory

import android.app.Service
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.view.GravityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.queatz.permanentmemory.adapters.WorldAdapter
import com.queatz.permanentmemory.logic.ContextManager
import com.queatz.permanentmemory.logic.DataManager
import com.queatz.permanentmemory.logic.NavigationManager
import com.queatz.permanentmemory.models.SubjectModel
import com.queatz.permanentmemory.pool.on
import com.queatz.permanentmemory.pool.onEnd
import com.queatz.permanentmemory.screens.SubjectScreen
import io.objectbox.android.AndroidScheduler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_main.*

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.on(ContextManager::class).context = this
        app.on(NavigationManager::class).view = this::show

        setContentView(R.layout.activity_main)

        menuButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        worldRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val worldAdapter = WorldAdapter({
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

        //TODO("Show last active subject")
        show(SubjectScreen())
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.fragments.forEach { fragmentTransaction.remove(it) }
            }
            fragmentTransaction.commit()
            super.onBackPressed()
        }
    }

    fun show(fragment: Fragment, addToBackStack: Boolean = false) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
                .replace(R.id.contentFrame, fragment)

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }

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
        super.onDestroy()
    }
}

const val app = 1