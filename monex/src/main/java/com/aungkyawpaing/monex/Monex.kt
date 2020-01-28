package com.aungkyawpaing.monex

import android.content.Context
import android.content.Intent
import com.aungkyawpaing.monex.internal.ui.main.MainActivity

/**
 * Helper class to get Intent
 */
object Monex {

  /**
   * @param context Android Context object
   * @return An intent that links to [MainActivity] that can be launch with [Context.startActivity]
   */
  fun getLaunchIntent(context: Context): Intent {
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    return intent
  }
}

