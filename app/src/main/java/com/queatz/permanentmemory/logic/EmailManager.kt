package com.queatz.permanentmemory.logic

import android.content.Intent
import android.net.Uri
import com.queatz.permanentmemory.R
import com.queatz.permanentmemory.pool.PoolMember



class EmailManager : PoolMember() {
    fun sendFeedback() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "jacobaferrero@gmail.com", null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Permanent Memory feedback")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "I have some feedback")
        on(ContextManager::class).context.startActivity(Intent.createChooser(emailIntent, on(ContextManager::class).context.getString(R.string.send_feedback)))
    }
}