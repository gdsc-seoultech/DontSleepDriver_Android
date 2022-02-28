package com.comye1.dontsleepdriver.util

import android.content.Context
import com.comye1.dontsleepdriver.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(Scope(Scopes.PLUS_LOGIN))
        .requestScopes(Scope(Scopes.PLUS_ME))
        .requestEmail()
        .requestIdToken(context.getString(R.string.web_client_id))
        .build()
    return GoogleSignIn.getClient(context, signInOptions)
}
