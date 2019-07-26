package io.habit.android.sdklogin.repository

import java.lang.RuntimeException

class AuthException(
    override val message: String? = null,
    override val cause: Throwable? = null,
    val code: Int = 0
) : RuntimeException(message,cause)