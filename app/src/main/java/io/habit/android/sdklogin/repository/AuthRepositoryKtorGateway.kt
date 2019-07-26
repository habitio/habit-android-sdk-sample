package io.habit.android.sdklogin.repository

import io.habit.android.sdklogin.entity.Account
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.response.readText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryKtorGateway(val host: String, val appNamespace: String, val appClientId: String) : AuthRepository {
    private val BASE_PATH = "/v3/i18n"

    private val client = HttpClient(Android) {
        engine {
            connectTimeout = 30_000
            socketTimeout = 30_000
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }


    override suspend fun signup(email: String, password: String, name: String): String =
        handleResult {
            client.post<String>("$host$BASE_PATH/legacy/account") {
                contentType(ContentType.Application.Json)
                body = Account(name, email, password, appNamespace)
            }
            //ignore result
            authorize(email, password)
        }


    override suspend fun authorize(email: String, password: String): String =
        handleResult{
            client.get<String>("$host$BASE_PATH/auth/authorize?response_type=password&scope=sysadmin%2Capplication%2Cuser") {
                url {
                    parameters.apply {
                        append("username", email)
                        append("password", password)
                        append("client_id", appClientId)
                    }
                }
            }
        }

    private suspend fun <T> handleResult(block: suspend () -> T): T =
        try {
            withContext(Dispatchers.IO) {
                block()
            }
        } catch (e: Throwable) {
            throw when(e){
                is ClientRequestException -> AuthException(
                    e.response.readText(),//todo: we could parse json body and use just "text" as message
                    e,
                    e.response.headers["X-Error"]?.toIntOrNull() ?: e.response.status.value
                )
                else -> AuthException(e.message,e,0)
            }

        }
}