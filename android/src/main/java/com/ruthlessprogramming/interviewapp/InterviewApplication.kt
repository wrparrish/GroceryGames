package com.ruthlessprogramming.interviewapp

import android.app.Application
import com.jakewharton.picasso.OkHttp3Downloader
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ruthlessprogramming.interviewapp.common.dagger.ContextModule
import com.ruthlessprogramming.interviewapp.common.dagger.DaggerSingletonComponent
import com.ruthlessprogramming.interviewapp.common.dagger.SingletonComponent
import com.squareup.picasso.Picasso
import timber.log.Timber

class InterviewApplication : Application() {
    val component: SingletonComponent by lazy {
        DaggerSingletonComponent.builder()
            .contextModule(ContextModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        setupLogging()
        setupTime()
        setupPicassoInstance()
    }

    private fun setupTime() {
        AndroidThreeTen.init(this)
    }

    private fun setupPicassoInstance() {
        val picasso = Picasso.Builder(this)
            .downloader(OkHttp3Downloader(this))
            .build()
        Picasso.setSingletonInstance(
            picasso
        )
    }

    private fun setupLogging() {
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var INSTANCE: InterviewApplication
            private set
    }
}