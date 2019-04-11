package com.example.userstat.repository


import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.userstat.api.ApiService
import com.example.userstat.model.User
import java.util.concurrent.Executor

class UsersDataSourceFactory(
        private val apiService: ApiService,
        private val since: String,
        private val retryExecutor: Executor) : DataSource.Factory<String, User>() {
    val sourceLiveData = MutableLiveData<PageKeyedUsersDataSource>()
    override fun create(): DataSource<String, User> {
        val source = PageKeyedUsersDataSource(apiService, since, retryExecutor)
        sourceLiveData.postValue(source)
        return source
    }
}
