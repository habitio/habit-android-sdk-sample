# SDK Sample Usage

This sample app shows the bare minimum to integrate with our Analytics SDK.

Add the mavenLocal repository to your project build.gradle
```
repositories {
    google()
    jcenter()
    mavenLocal()
}
```

Unzip the [lib code](https://cdn.muzzley.com/habit-sdk/HabitAnalyticsSDK001.zip) into your mavenLocal dir (${user.home}/.m2/repository/)

Add dependencies to app/build.gradle
```
dependencies {
    implementation "io.habit.analytics:analytics:0.0.1"
}
```

Do the necessary imports
```
import io.habit.analytics.HabitStatusCodes
import io.habit.analytics.SDK
```

Add the SDK intialization to your Application class:
```
SDK.init(this,
  PROVIDED_INIT_STRING, // contact us to obtain this
  authorization // if user already signed in, otherwise null
) {
  // possibly handle error codes
  // you can check the description field for extra detail on status codes (${it.description})
  println("init result: $it")
}
```

If you don't have user's Authorization string yet, than in some activity request user's name, email and password.

Send them to our backend to [obtain the Authorization](./Authorization.md) string, either through signup (for account creation) or through authorize (for previously created accounts).

On success, send it to the SDK:
```
SDK.setAuthorization(authorization)
```
This needs to be done only once per login, or if you acquire a new Authorization string.

If you need to logout, just call
```
SDK.logout()
```

You can also track UI (or other) events like this:
```
SDK.tracker().track("start", mapOf("demo" to "started"))
```

That's it !

Note that, since a lot of useful analytics are gathered from location, you should request ACCESS_FINE_LOCATION permission prior to calling setAuthorization.

We support the SDK back to api level 23, although it could work on older api levels.

Check the rest of the sample app for details.

If want to compile and run, find the strings REPLACE_ME and replace it with the appropriate information.
