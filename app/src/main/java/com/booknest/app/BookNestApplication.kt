package com.booknest.app

import android.app.Application
import com.booknest.app.di.AppContainer
import com.booknest.app.di.DefaultAppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BookNestApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)

        applicationScope.launch {
            container.bookRepository.preloadSampleDataIfEmpty()
        }
    }
}
