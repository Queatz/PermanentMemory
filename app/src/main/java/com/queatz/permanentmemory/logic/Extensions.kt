package com.queatz.permanentmemory.logic

import android.graphics.PorterDuff
import android.widget.ProgressBar
import com.queatz.permanentmemory.R
import java.util.*

fun <E> List<E>.randomOrNull(): E? = if (isEmpty()) null else get(Random().nextInt(size))

fun ProgressBar.applyColorFromProgress() {
    if (progress < 100)
        progressDrawable.colorFilter = null
    else
        progressDrawable.setColorFilter(resources.getColor(R.color.green), PorterDuff.Mode.MULTIPLY)
}