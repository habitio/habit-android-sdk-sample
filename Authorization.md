# Authorization

Given that sending user email and passwords is usually a more sensitive operation, and also because user credentials could be provisioned in other ways, the SDK doesn't provide a way to obtain the Authorization string directly.

The usual way to obtain it, is through HTTP calls to our backend.

You can check the class [AuthRepositoryKtorGateway.kt] for a sample on how to do this, but in case you want to implement yourself, here's what you have to do:

## Account login
Do an HTTP GET to https://api.platform.muzzley.com/v3/i18n/auth/authorize?response_type=password&scope=application%2Cuser with additional query parameters "username","password" and "client_id" using the email,password and appClientId respectively.

The appClientId is provisioned previously in our [selfcare back office]

If it succeeds, the result of this operation is the Authorization string.

In case of error, you can examine the header "X-Error" for a fine grained cause of error.

If it's 1601, it means that "User credentials are invalid"

## Account creation
Do an HTTP POST to https://api.platform.muzzley.com/v3/i18n/legacy/account with a json body payload in the format
```
{
  "name": name,
  "email": email,
  "password": password,
  "appId": appNamespace,
  "type": "account",
  "device": "android"
}
```
The name,email and password are the ones requested to the user.

The appNamespace is provisioned previously in our [selfcare back office]

If it succeeds, you can safely ignore the resulting payload, and proceed to do an Account login, as described previously.

In case of error, you can examine the header "X-Error" for a fine grained cause of error.

If it's 1211, it means "User already exists".

[selfcare back office]:https://selfcare.habit.io/
[AuthRepositoryKtorGateway.kt]:./app/src/main/java/io/habit/android/sdklogin/repository/AuthRepositoryKtorGateway.kt
