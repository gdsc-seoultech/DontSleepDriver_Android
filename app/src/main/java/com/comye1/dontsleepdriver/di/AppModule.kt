package com.comye1.dontsleepdriver.di

import com.comye1.dontsleepdriver.data.DSDApi
import com.comye1.dontsleepdriver.repository.DSDRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDSDRepository(
        api: DSDApi
    ) = DSDRepository(api)

    @Singleton
    @Provides
    fun provideDSDApi(): DSDApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://54.180.86.178/")
            .build()
            .create(DSDApi::class.java)
    }
}