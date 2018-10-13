package com.queatz.permanentmemory

import android.app.Service
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.screens.PlayScreen
import com.queatz.permanentmemory.screens.SetScreen
import com.queatz.permanentmemory.screens.SubjectScreen
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationView.setNavigationItemSelectedListener(this)

        menuButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        show(SubjectScreen())
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_vietnamese -> show(SubjectScreen())
            R.id.nav_german -> show(PlayScreen())
            R.id.nav_russian -> show(SetScreen())
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    private fun show(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .commit()
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
