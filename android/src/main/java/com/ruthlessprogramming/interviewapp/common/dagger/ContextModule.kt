package com.ruthlessprogramming.interviewapp.common.dagger

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule(private val application: Application) {

    @Provides
    @Singleton
    fun providesContext(): Context = application

    @Provides
    @Singleton
    fun providesGson(): Gson = Gson()
}