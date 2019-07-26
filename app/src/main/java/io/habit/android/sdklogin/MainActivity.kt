package io.habit.android.sdklogin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import io.habit.analytics.SDK
import io.habit.android.sdklogin.repository.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() , CoroutineScope by MainScope(){

    lateinit var localRepository: LocalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        localRepository = LocalRepositoryPreferencesGateway(this)

        val authRepository: AuthRepository = AuthRepositoryKtorGateway(
            "https://api.platform.muzzley.com",
            "REPLACE_ME_APP_NAMESPACE",
            "REPLACE_ME_APP_CLIENT_ID"
        )

        updateUi()

        create.setOnClickListener {
            validateAndCall("None can be blank", emailEt, passwordEt, nameEt) {
                authRepository.signup(it[0], it[1], it[2])
            }
        }

        login.setOnClickListener {
            validateAndCall("Email and password can't be blank", emailEt, passwordEt) {
                authRepository.authorize(it[0], it[1])
            }
        }

        logout.setOnClickListener {
            localRepository.auth = null
            updateUi()
            SDK.logout {
                println("logout result: $it")
            }
        }

        track.setOnClickListener {
            SDK.tracker().track("start", mapOf("demo" to "started"))
        }

    }

    fun validateAndCall(err: String, vararg et: EditText, block: suspend (l: List<String>) -> String) {

        et.map { it.editableText.toString() }.let {
            if (it.any { it.isBlank() }) {
                showMsg(err)
            } else {
                handleAuth {
                    block(it)
                }
            }
        }
    }

    fun handleAuth(block: suspend () -> String) =
        launch {
            try {
                showProgress(true)
                hideKeyboard()
                if (!locationGranted()) {
                    showMsg("Location not granted, not requesting Authorization")
                    return@launch
                }
                block().let {
                    println("result: $it")
                    localRepository.auth = it
                    SDK.setAuthorization(it)
                }
                updateUi()
            } catch (e: Throwable) {
//                e.printStackTrace()
                showMsg(
                    when (e) {
                        is AuthException -> when (e.code) {
                            1601 -> "User credentials are invalid"
                            1211 -> "User already exists"
                            else -> {
                                when (e.cause) {
                                    is SocketTimeoutException -> "Timeout: Server took too long"
                                    is IOException -> "No internet connection"
                                    else -> "Got error code ${e.code}, message: ${e.message}"
                                }
                            }
                        }
                        else -> "Got error: ${e.message}"
                    }
                )
            } finally {
                showProgress(false)
            }
        }

    fun showProgress(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    fun hideKeyboard() =
        currentFocus?.let {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.run {
                hideSoftInputFromWindow(it.windowToken, 0)
            }
        }

    private fun updateUi() {
        localRepository.auth.isNullOrBlank().let {
            create.isEnabled = it
            login.isEnabled = it
            logout.isEnabled = !it
            track.isEnabled = !it
            statusTv.text = "Logged: ${!it}"
        }
    }

    private fun showMsg(string: String) =
        Snackbar.make(statusTv,string,Snackbar.LENGTH_LONG).show()

    fun hasLocation() =
        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private var permissionContinuation: Continuation<Boolean>? = null
    private suspend fun locationGranted(): Boolean =
        hasLocation() ||
        confirm("The SDK will need Location permission to be most useful. Please grant in the following step") &&
                suspendCoroutine { continuation ->
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 303)
                    permissionContinuation = continuation
                }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionContinuation?.resume(grantResults[0] == PackageManager.PERMISSION_GRANTED)
    }

    suspend fun confirm(str: String): Boolean = suspendCoroutine { cont ->
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setMessage(str)
            .setPositiveButton(android.R.string.ok){ _,_ -> cont.resume(true)}
            .setNegativeButton(android.R.string.cancel){ _,_ -> cont.resume(false)}
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}
