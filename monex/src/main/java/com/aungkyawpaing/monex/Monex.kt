package com.aungkyawpaing.monex

import android.content.Context
import android.content.Intent
import com.aungkyawpaing.monex.internal.helper.ClearTransactionService
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

  /**
   * @param context Android Context object
   * @return An intent that links to [ClearTransactionService] that can be launch with [Context.startService]
   */
  fun getClearServiceIntent(context: Context): Intent {
    val intent = Intent(context, ClearTransactionService::class.java)
    return intent
  }

}

