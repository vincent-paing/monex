package com.aungkyawpaing.example.monex

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aungkyawpaing.monex.MonexInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.threeten.bp.Duration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val monexInterceptor = MonexInterceptor(
      this,
      decayTimeInMiliSeconds = Duration.ofDays(1).toMillis()
    )

    val okHttpClient = OkHttpClient.Builder()
      .addNetworkInterceptor(monexInterceptor)
      .build()
    val retrofit = Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://jsonplaceholder.typicode.com/")
      .build()

    val service = retrofit.create(Service::class.java)

    val buttonPingApi = findViewById<Button>(R.id.btnPingApi)

    buttonPingApi.setOnClickListener {
      service.call().enqueue(object : Callback<Unit> {
        override fun onFailure(call: Call<Unit>, t: Throwable) {
          t.printStackTrace()
          Toast.makeText(this@MainActivity, "Fail", Toast.LENGTH_LONG).show()
        }

        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
          Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_LONG).show()
        }

      })

    }

    val buttonPingError = findViewById<Button>(R.id.btnPingError)

    buttonPingError.setOnClickListener {
      val request = Request.Builder()
        .url("https://abcdefghijklmnop.com/qwertyuiop")
        .build()

      okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
          Handler(Looper.getMainLooper()).post {
            Toast.makeText(this@MainActivity, "Fail", Toast.LENGTH_LONG).show()
          }

        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
          Handler(Looper.getMainLooper()).post {
            Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_LONG).show()
          }

        }

      })
    }

    val buttonPingImage = findViewById<Button>(R.id.btnPingImage)
    buttonPingImage.setOnClickListener {
      val request = Request.Builder()
        .url("https://www.snopes.com/tachyon/2018/09/elephant-carries-lion.jpg")
        .build()

      okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
          Handler(Looper.getMainLooper()).post {
            Toast.makeText(this@MainActivity, "Fail", Toast.LENGTH_LONG).show()
          }

        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
          Handler(Looper.getMainLooper()).post {
            Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_LONG).show()
          }

        }

      })
    }
  }
}
