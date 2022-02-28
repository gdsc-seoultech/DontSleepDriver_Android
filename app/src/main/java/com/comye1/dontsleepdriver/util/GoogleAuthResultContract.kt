package com.comye1.dontsleepdriver.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task

class GoogleAuthResultContract: ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {

        Log.d("google auth", resultCode.toString())
        return when(resultCode) {
            Activity.RESULT_OK -> GoogleSignIn.getSignedInAccountFromIntent(intent)
            else -> null
        }
    }

    override fun createIntent(context: Context, input: Int): Intent {
        return getGoogleSignInClient(context).signInIntent.putExtra("input", input)
    }
}