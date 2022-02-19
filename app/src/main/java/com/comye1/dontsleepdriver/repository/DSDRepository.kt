package com.comye1.dontsleepdriver.repository

import android.util.Log
import com.comye1.dontsleepdriver.data.DSDApi
import com.comye1.dontsleepdriver.data.model.*
import com.comye1.dontsleepdriver.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class DSDRepository @Inject constructor(
    private val api: DSDApi
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
        Log.d("repo 4 success", response.success.toString())
        return Resource.Success(response)
    }
}