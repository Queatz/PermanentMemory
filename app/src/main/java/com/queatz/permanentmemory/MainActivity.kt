package com.queatz.permanentmemory

import android.app.AlertDialog
import android.app.Service
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.view.GravityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.adapters.WorldAdapter
import com.queatz.permanentmemory.screens.SetScreen
import com.queatz.permanentmemory.screens.SubjectScreen
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_main.*

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menuButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        worldRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        worldRecyclerView.adapter = WorldAdapter { _ ->
            show(SubjectScreen())
            drawerLayout.closeDrawer(GravityCompat.START)
        }

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
}
