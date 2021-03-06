 [ ![Download](https://api.bintray.com/packages/vincent-paing/maven/monex/images/download.svg) ](https://bintray.com/vincent-paing/maven/monex/_latestVersion) [![Build Status](https://api.travis-ci.com/vincent-paing/monex.svg?branch=master)](https://travis-ci.com/vincent-paing/monex)

# Monex

Monex is an in-app HTTP inspector for Android OkHttp clients, which is a fork of [Chuck](https://github.com/jgilfelt/chuck) made initially for using internally within [nexlabs](https://nexlabs.co/). 

The bionomial name of Groundhog, also known as Wood*chuck*, is called Marmota Monax. Monax is taken from this name, and repurposed to *nex* as in *nexlabs*. Thus, the name **Monex**

![ ](./images/preview.gif  "Preview")

## How it works

Monex displays a low-priority notification every time a network request is made through your `OkHttpClient`. Tapping on the notification will display a list of network requests you have made for your app. You can then tap on any item to see full details and share those easily.

## Getting Started

### Add Dependency

#### Gradle

If you use gradle, add this repository first 
```groovy 
repositories {
	maven { url "https://dl.bintray.com/vincent-paing/maven" } 
} 
```

Then add the following dependencies:

```groovy
dependencies {
  def monex_version = '0.4.0'
  
  debugImplementation "com.aungkyawpaing.monex:monex:$monex_version"
  releaseImplementation "com.aungkyawpaing.monex:monex-no-op:$monex_version"
}
```

### Setup

Inside your app where you build your `OkHttpClient`, you can add `MonexInterceptor`

```kotlin
val okHttpClient = OkHttpClient.Builder()
.addNetworkInterceptor(MonexInterceptor(context))
.build()
```

Then just hit run and it will start logging each request you made.

## Configuration

### Hide Notification

You can also pass `showNotification` boolean variable inside `MonexInterceptor` constructor. Set this to `false` if you don't want to see any notifications. You can still manually launch the history activity through `Monex.getLaunchIntent(context))` which return the Launcher activity of Monex.

### Decay Time

Monex can automatically clear historical data. If you don't want a lot of data populated into your local database, then you can add  `decayTimeInMiliSeconds` to parameter in the constructor. By default, it is set to 0, which is never delete automatically.

```
MonexInterceptor(this, decayTimeInMiliSeconds = Duration.ofDays(1).toMillis()) //Clear after one day
```

### Gitlab setup

Monex can uses Gitlab's [snippet API](https://docs.gitlab.com/ee/api/snippets.html) to automatically generate snippet link for sharing a transaction detail. By default, this is disabled. To enable sharing as Gitlab snippet, you will have to provide a `MonexGitlabConfig` in the constructor. The constructor has two parameters; `accessToken`, and `baseUrl`

To add an access token,

- Go to `{YOUR_GITLAB_LINK}/profile/personal_access_tokens` 
- Copy the token
- Provide it inside `MonexGitlabConfig`constructor
- Pass the config into constructor of `MonexInterceptor`

```kotlin
val gitlabConfig = MonexGitlabConfig(
    accessToken = "{YOUR_ACCESS_TOKEN}",
    baseUrl = "{YOUR_GITLAB_LINK}" //By default, uses "https://gitlab.com/"
)
```

**WARNING: Make sure your personal access token was not leaked when providing source code to someone. If you feel like it might has been breached, disable the token immediately**

## Contribution

The tool is in very early stage and we need your help to grow this further. You can help the growth by 

- Providing PR for existing isses
- Suggesting new issues, and feature requests
- Commenting your thoughts on existing issues
- Using the library yourself

