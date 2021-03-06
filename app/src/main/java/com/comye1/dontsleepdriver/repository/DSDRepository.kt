package com.comye1.dontsleepdriver.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.core.content.edit
import com.comye1.dontsleepdriver.data.DSDApi
import com.comye1.dontsleepdriver.data.model.*
import com.comye1.dontsleepdriver.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        saveToken(prefix = "Bearer", response.data.token)
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

    private fun String.toTokenMap(): Map<String, String> = mapOf(Pair("Authorization", this))


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

    suspend fun kakaoSignIn(accessToken: String): Resource<DSDResponse> {
        val response = try {
            api.kakaoSignIn(OAuthBody(accessToken))
        } catch (e: Exception) {
            Log.d("repo 6 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 5 success", response.data!!.email)
        saveToken(prefix = "Bearer", response.data.token)
        return Resource.Success(response)
    }

    suspend fun googleSignIn(idToken: String): Resource<DSDResponse> {
        val response = try {
            api.googleSignIn(OAuthBody(idToken))
        } catch (e: Exception) {
            Log.d("repo 6 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 5 success", response.data!!.email)
        saveToken(prefix = "Bearer", response.data.token)
        return Resource.Success(response)
    }

    suspend fun naverSignIn(accessToken: String): Resource<DSDResponse> {
        val response = try {
            api.naverSignIn(OAuthBody(accessToken))
        } catch (e: Exception) {
            Log.d("repo 6 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 5 success", response.data!!.email)
        saveToken(prefix = "Bearer", response.data.token)
        return Resource.Success(response)
    }

    suspend fun getHistoryPages(): Resource<Int> {
        val token = getSavedToken() ?: return Resource.Error("token does not exist")
        val response = try {
            api.getHistoryPages(token.toTokenMap())
        } catch (e: Exception) {
            Log.d("repo 7 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 7 success", response.data.toString())
        return Resource.Success(response.data)
    }

    suspend fun getHistoryByPage(page: Int): Resource<List<DrivingResponse>> {
        val token = getSavedToken() ?: return Resource.Error("token does not exist")
        val response = try {
            api.getHistoryByPage(token.toTokenMap(), page = page)
        } catch (e: Exception) {
            Log.d("repo 8 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 8 success", response.data.toString())
        return Resource.Success(response.data)
    }

    suspend fun getDrivingItem(id: Int): Resource<DrivingResponse> {
        Log.d("repo 9 id", id.toString())
        val token = getSavedToken() ?: return Resource.Error("token does not exist")
        val response = try {
            api.getHistoryItem(token.toTokenMap(), id)
        } catch (e: Exception) {
            Log.d("repo 9 exception", e.toString())
            return Resource.Error(e.message ?: "")
        }
        Log.d("repo 9 success", response.data.toString())
        return Resource.Success(response.data)
    }

    fun postDrivingItem(driving: DrivingBody, savedId: MutableState<Int>): Resource<DrivingData> {
        val token = getSavedToken() ?: return Resource.Error("token does not exist")

        Log.d("postDrivingItem", "repository")

        api.postDriving(token.toTokenMap(), driving = driving)
            .enqueue(object : Callback<DrivingPostResponse> {
                override fun onResponse(
                    call: Call<DrivingPostResponse>,
                    response: Response<DrivingPostResponse>
                ) {
                   if (response.isSuccessful) {
                       savedId.value = response.body()?.data?.id ?: -1
                   }
                }

                override fun onFailure(call: Call<DrivingPostResponse>, t: Throwable) {

                }
            })

//        val response = try {
//            api.postDriving(token.toTokenMap(), driving)
//        } catch (e: Exception) {
//            Log.d("repo 10 exception", e.localizedMessage?: "")
//            return Resource.Error(e.toString())
//        }
//        Log.d("repo 10 success", response.data.toString())
//        response.data?.let {
//            return Resource.Success(it)
//        }
        return Resource.Error("data null")
    }
}