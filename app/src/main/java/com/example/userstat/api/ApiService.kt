package com.example.userstat.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.userstat.model.User
import io.reactivex.Observable
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    fun getUsers(@Query("since") since: String, @Query("per_page") per_page: Int): Call<List<User>>

    @GET("users/{login}")
    fun getUser(@Path("login") login: String): Call<User>

    companion object {
        fun create(context: Context): ApiService {
            val cacheSize = (5 * 1024 * 1024).toLong()
            val cache = Cache(context.cacheDir, cacheSize)
            val loggingInterceptor = HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            val okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor { chain ->

                    var request = chain.request()
                    if (hasNetwork(context)!!)
                        request.newBuilder().header("Accept", "application/json").build()
                    else
                    /*
                    *  If there is no Internet, get the cache that was stored 7 days ago.
                    *  If the cache is older than 7 days, then discard it,
                    *  and indicate an error in fetching the response.
                    *  The 'max-stale' attribute is responsible for this behavior.
                    *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
                    */
                        request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                    // End of if-else statement
                    chain.proceed(request)
                }
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.github.com/")
                .build();
            return retrofit.create(ApiService::class.java)
        }
    }
}

fun hasNetwork(context: Context): Boolean? {
    var isConnected: Boolean? = false
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    if (activeNetwork != null && activeNetwork.isConnected)
        isConnected = true
    return isConnected
}