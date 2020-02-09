object BuildConfig {
  const val compileSdk = 29
  const val minSdk = 21
  const val targetSdk = 29

  private const val versionMajor = 0
  private const val versionMinor = 2
  private const val versionPatch = 2
  private const val versionBuild = 0

  const val artifactName = "monex"
  const val artifactNameNoOp = "monex-no-op"

  const val description = "A fork of chuck with support for sharing Gitlab Snippet"

  const val license = "Apache-2.0"
  const val license_url = "http://www.apache.org/licenses/LICENSE-2.0.txt"


  const val versionName =
    "$versionMajor.$versionMinor.$versionPatch"
  const val versionCode =
    versionMajor * 1000000 + versionMinor * 10000 + versionPatch * 100 + versionBuild

}

object CommonLibs {
  const val android_gradle_plugin = "com.android.tools.build:gradle:3.6.0-rc02"
  const val bintray_plugin = "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4"

  const val junit = "junit:junit:4.12"
}

//region AndroidX
object AndroidXAppCompat {
  const val app_compat = "androidx.appcompat:appcompat:1.1.0"
}

object AndroidXRecyclerView {
  private const val version = "1.1.0-rc01"

  const val recycler_view = "androidx.recyclerview:recyclerview:$version"
  const val selection = "androidx.recyclerview:recyclerview-selection:$version"
}

object AndroidXConstraintLayout {
  private const val version = "1.1.3"

  const val constraint_layout = "androidx.constraintlayout:constraintlayout:$version"
}

object AndroidXViewPager {
  const val view_pager = "androidx.viewpager:viewpager:1.0.0"

  const val view_pager_2 = "androidx.viewpager2:viewpager2:1.0.0-rc01"
}

object AndroidXSqlite {
  private const val version = "2.1.0-alpha01"

  const val sqlite = "androidx.sqlite:sqlite:$version"
  const val sqlite_framework = "androidx.sqlite:sqlite-framework:$version"
  const val sqlite_ktx = "androidx.sqlite:sqlite-ktx:$version"
}

object AndroidArchLifeCycle {
  private const val version = "2.1.0"

  const val lifecycle = "androidx.lifecycle:lifecycle-extensions:$version"
  const val lifecycle_compiler = "androidx.lifecycle:lifecycle-compiler:$version"
  const val common_java8 = "androidx.lifecycle:lifecycle-common-java8:$version"
  const val lives = "com.snakydesign.livedataextensions:lives:1.2.1"
}

object AndroidArchCore {
  const val version = "2.1.0"

  const val common = "androidx.arch.core:core-common:$version"
  const val test = "androidx.arch.core:core-testing:$version"
}


object AndroidXCore {
  private const val version = "1.1.0"

  const val core = "androidx.core:core:$version"
  const val core_ktx = "androidx.core:core-ktx:$version"
}

object AndroidXFragment {
  private const val version = "1.2.0-rc05"

  const val fragment = "androidx.fragment:fragment:$version"
  const val fragment_ktx = "androidx.fragment:fragment-ktx:$version"
  const val fragment_testing = "androidx.fragment:fragment-testing:$version"
}

object AndroidXPaging {

  private const val version = "2.1.1"

  const val common = "androidx.paging:paging-common::$version"
  const val runtime_ktx = "androidx.paging:paging-runtime-ktx:$version"
}

object AndroidXTest {
  private const val version = "1.3.0-alpha03"

  const val core = "androidx.test:core:$version"
  const val core_ktx = "androidx.test:core-ktx:$version"
  const val runner = "androidx.test:runner:$version"
  const val rules = "androidx.test:rules:$version"
  const val roboelectric = "org.robolectric:robolectric:4.3.1"
}

object AndroidXTestExt {
  private const val version = "1.1.2-alpha02"

  const val junit = "androidx.test.ext:junit:$version"
  const val junit_ktx = "androidx.test.ext:junit-ktx:$version"
  const val truth = "androidx.test.ext:truth:1.3.0-alpha02"
}

object AndroidXEspresso {
  private const val version = "3.3.0-alpha02"

  const val core = "androidx.test.espresso:espresso-core:$version"
  const val contrib = "androidx.test.espresso:espresso-contrib:$version"
  const val intents = "androidx.test.espresso:espresso-intents:$version"
  const val idling_resource = "androidx.test.espresso:espresso-idling-resource:$version"
  const val idling_concurrent = "androidx.test.espresso.idling:idling-concurrent:$version"
}

//endregion

object Material {
  const val material = "com.google.android.material:material:1.2.0-alpha02"
}

object Coil {
  const val coil = "io.coil-kt:coil:0.6.1"
}

object Detekt {
  private const val version = "1.1.1"

  const val plugin = "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$version"

}

object Glide {
  private const val version = "4.10.0"

  const val runtime = "com.github.bumptech.glide:glide:$version"
  const val compiler = "com.github.bumptech.glide:compiler:$version"
  const val transformations = "jp.wasabeef:glide-transformations:4.0.1"
}

object Kakao {
  private const val version = "2.2.0"

  const val core = "com.agoda.kakao:kakao:$version"
}

object Kotlin {
  private const val version = "1.3.61"

  const val stdblib_jdk = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$version"
  const val gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
}

object KotlinCoroutine {
  private const val version = "1.3.2"

  const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
  const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
  const val adapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
  const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
}

object Mockito {
  private const val version = "3.0.0"

  const val core = "org.mockito:mockito-core:$version"
  const val android = "org.mockito:mockito-android:$version"
  const val inline = "org.mockito:mockito-inline:$version"
  const val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
}

object Moshi {
  private const val version = "1.9.2"

  const val core = "com.squareup.moshi:moshi:$version"
  const val adapters = "com.squareup.moshi:moshi-adapters:$version"
  const val kotlin = "com.squareup.moshi:moshi-kotlin:$version"
  const val code_gen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
}

object OkHttp {
  private const val version = "4.3.1"

  const val client = "com.squareup.okhttp3:okhttp:$version"
  const val logger = "com.squareup.okhttp3:logging-interceptor:$version"
  const val mock_web_server = "com.squareup.okhttp3:mockwebserver:$version"
}

object Room {
  private const val version = "2.2.3"

  const val runtime = "androidx.room:room-runtime:$version"
  const val compiler = "androidx.room:room-compiler:$version"
  const val ktx = "androidx.room:room-ktx:$version"
  const val testing = "androidx.room:room-testing:$version"
}

object ThreeTenBp {
  private const val version = "1.4.0"

  const val core = "org.threeten:threetenbp:$version"
  const val no_tz_db = "org.threeten:threetenbp:$version:no-tzdb"
  const val android = "com.jakewharton.threetenabp:threetenabp:1.2.1"
}