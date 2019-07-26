package io.habit.android.sdklogin.repository

interface AuthRepository {

    suspend fun signup(email: String, password: String, name: String): String
    suspend fun authorize(email: String, password: String): String

}