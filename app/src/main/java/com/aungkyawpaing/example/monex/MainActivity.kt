package com.aungkyawpaing.example.monex

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aungkyawpaing.monex.MonexGitlabConfig
import com.aungkyawpaing.monex.MonexInterceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)


    val okHttpClient = OkHttpClient.Builder()
      .addNetworkInterceptor(MonexInterceptor(this))
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
  }
}
