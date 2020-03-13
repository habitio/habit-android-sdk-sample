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

Unzip the [lib code](https://github.com/habitio/habit-android-sdk-sample/tree/master/lib) into your mavenLocal dir (${user.home}/.m2/repository/)

Add dependencies to app/build.gradle
```
dependencies {
    implementation "io.habit.analytics:analytics:0.0.3"
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
  PROVIDED_APP_NAMESPACE_STRING, // contact us to obtain this
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

## Analysed Data 

| Property  | Received on event (Background) | Received on event (Foreground) | Query by snapshot | Permission requested to the user | Frequency |
| ------------- | ------------- | ------------- | ------------- | ------------- | ------------- |
| network / current_wifi | NO (>= 24) | YES | YES | ACCESS_FINE_LOCATION | all events*  except itself |
| network / carrier | NO | NO | YES | Not necessary | all events*|
| network / internet_connection |  |  |  |  |  |
| network / scan |  |  |  |  |  |
| network / roaming |  |  |  |  |  |
| network / flight_mode |  |  |  |  |  |
| network / mobile_data_enabled |  |  |  |  |  |
| location / location |  YES |  YES |  YES |  ACCESS_FINE_LOCATION |  all events* except itself |
| location / visit |  NA |  NA |  NA |  NA |  NA |
| movement / activity |  NO |  YES |  YES |  ACTIVITY_RECOGNITION (automatic) |  all events* except itself |
| movement / activity_history |  NA |  NA |  NA |  NA |  NA |
| battery / charge |  NO (>= 26) |  YES |  YES |  Not necessary |  all events  except itself |
| battery / level |  NO (>= 26) |  YES (on low threshold) |  YES |  Not necessary |  all events* except itself |
| devices / bluetooth |  NO | YES | YES | BLUETOOTH (automatic, but must be ON) |  all events* |
| current_device / agent | NO | NO | YES | Not necessary | all events* |
| current_device / installed_packages | NO | NO | YES | Not necessary | all events* except itself |
| current_device / added_package | NO (>= 26) | YES | NO | Not necessary | when a package is added |
| current_device / removed_package | NO (>= 26) | YES | NO | Not necessary | when a package is removed |
| screen / status | NO | YES | YES | Not necessary | all events |

*all events = start, time, wifi, location, awareness, battery, bluetooth, screen, packages

## Future work

- Configuration of which data is supposed to be analysed by the SDK.

## Troubeshooting

- If you need to turn on debug logs, add this to your dependencies block:
```
implementation 'com.jakewharton.timber:timber:4.7.1'
```
Also add this to your Application class:
```
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
}
```

## Contact Us

For more information contact us at support@habit.io  

