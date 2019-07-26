package io.habit.android.sdklogin.entity

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val name: String,
    val email: String,
    val password: String,
    val appId: String ,
    val type: String = "account",
    val device:String = "android"
)