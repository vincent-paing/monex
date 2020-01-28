package com.aungkyawpaing.example.monex

import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by Vincent on 1/22/20
 */
interface Service {

  @GET("todos/1")
  fun call(): Call<Unit>
}
