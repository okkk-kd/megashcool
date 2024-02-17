package ru.sample.duckapp.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import ru.sample.duckapp.domain.Duck

interface DucksApi {
    @GET("random")
    fun getRandomDuck(): Call<Duck>
    
    @GET("http/{code}")
    fun getHttpCodeDuck(
        @Path("code") code: String
    ): Call<ResponseBody>
}