package com.comye1.dontsleepdriver.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.comye1.dontsleepdriver.data.DSDApi
import com.comye1.dontsleepdriver.data.model.*
import com.comye1.dontsleepdriver.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class DSDRepository @Inject constructor(
    private val api: DSDApi,
    context: Context
) {
    suspend fun requestEmailVerificationCode(email: String): Resource<DSDResponse> {
        val response = try {
            api.emailVerificationCode(StringBody(email))
        } catch (e: Exception) {
            Log.d("repo 1 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 1 success", response.success.toString())
        return Resource.Success(response)
    }

    suspend fun verifyEmailCode(email: String, token: String): Resource<DSDResponse> {
        val response = try {
            api.verifyEmailCode(EmailVerificationBody(email, token))
        } catch (e: Exception) {
            Log.d("repo 2 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 2 success", response.success.toString())
        return Resource.Success(response)
    }

    suspend fun signUp(email: String, name: String, password: String): Resource<DSDResponse> {
        val response = try {
            api.signUp(SignUpBody(email = email, name = name, password = password))
        } catch (e: Exception) {
            Log.d("repo 3 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 3 success", response.success.toString())
        return Resource.Success(response)
    }

    suspend fun signIn(email: String, password: String): Resource<DSDResponse> {
        val response = try {
            api.signIn(SignInBody(email = email, password = password))
        } catch (e: Exception) {
            Log.d("repo 4 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 4 token", response.data?.token!!)
        saveToken(prefix = "Bearer",response.data?.token!!)
        return Resource.Success(response)
    }

    private val tokenSharedPref: SharedPreferences = context.getSharedPreferences(
        "token",
        Context.MODE_PRIVATE
    )

    private fun saveToken(prefix: String, token: String) {
        tokenSharedPref.edit {
            putString("TOKEN", "$prefix $token")
            commit()
        }
    }

    private fun getSavedToken(): String? = tokenSharedPref.getString("TOKEN", null)

    private fun String.toTokenMap(): Map<String, String>
    = mapOf(Pair("Authorization", this))


    suspend fun getUser(): Resource<DSDResponse> {
        Log.d("repo 5", "Get user")
        val token = getSavedToken() ?: return Resource.Error("token does not exist")
        val response = try {
            api.getUser(token.toTokenMap())
        } catch (e: Exception) {
            Log.d("repo 5 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 5 success", response.data!!.email)
        return Resource.Success(response)
    }

    private val soundSharedPref: SharedPreferences = context.getSharedPreferences(
        "sound",
        Context.MODE_PRIVATE
    )

    fun saveSound(id: Int, email: String) {
        soundSharedPref.edit {
            putInt(email, id)
            commit()
        }
    }

    fun getSavedSound(email: String): Int = tokenSharedPref.getInt(email, -1)

    suspend fun kakaoSignIn(accessToken: String): Resource<DSDResponse>{
        val response = try {
            api.kakaoSignIn(OAuthBody(accessToken))
        } catch (e: Exception) {
            Log.d("repo 6 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 5 success", response.data!!.email)
        return Resource.Success(response)
    }
}