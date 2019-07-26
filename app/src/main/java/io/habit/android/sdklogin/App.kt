package io.habit.android.sdklogin

import android.app.Application
import io.habit.analytics.HabitStatusCodes
import io.habit.analytics.SDK
import io.habit.android.sdklogin.repository.LocalRepository
import io.habit.android.sdklogin.repository.LocalRepositoryPreferencesGateway
import timber.log.Timber

class App : Application(){

    lateinit var localRepository: LocalRepository

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        localRepository = LocalRepositoryPreferencesGateway(this)

        SDK.init(this,"REPLACE_ME_INIT_STRING") {
            println("init result: $it")
            if (it == HabitStatusCodes.HABIT_SDK_SET_AUTHENTICATION ){
                localRepository.auth?.let { SDK.setAuthorization(it) }
            }
        }

    }
}