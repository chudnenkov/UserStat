package com.example.userstat.repository

import androidx.lifecycle.MutableLiveData


import androidx.paging.PageKeyedDataSource


import com.example.userstat.api.ApiService
import com.example.userstat.model.User
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

class PageKeyedUsersDataSource(
    private val apiService: ApiService,
    private val since: String,
    private val retryExecutor: Executor
) : PageKeyedDataSource<String, User>() {

    private var retry: (() -> Any)? = null

    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, User>
    ) {
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, User>) {
        networkState.postValue(NetworkState.LOADING)
        apiService.getUsers(
            since = params.key,
            per_page = 10
        ).enqueue(
            object : retrofit2.Callback<List<User>> {
                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    retry = {
                        loadAfter(params, callback)
                    }
                    networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
                }

                override fun onResponse(
                    call: Call<List<User>>,
                    response: Response<List<User>>
                ) {
                    if (response.isSuccessful) {

                        retry = null
                        callback.onResult(response.body() ?: emptyList(), 10.toString())
                        networkState.postValue(NetworkState.LOADED)
                    } else {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(
                            NetworkState.error("error code: ${response.code()}")
                        )
                    }
                }
            }
        )
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, User>
    ) {
        val request = apiService.getUsers(
            since = since,
            per_page = 10
        )
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        try {
            val response = request.execute()
            retry = null
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            callback.onResult(response.body() ?: emptyList(), 0.toString(), 10.toString())
        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(ioException.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }
}