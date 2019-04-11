package com.example.userstat.view

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.userstat.api.ApiService
import com.example.userstat.model.User
import java.io.IOException
import java.util.concurrent.Executors

class UserViewModel (val login : String, val context : Context): ViewModel(){
    private val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>().also {
            loadUsers()
        }
    }

    fun getUsers(): MutableLiveData<User> {
        return user
    }

    private fun loadUsers() {

        val service = Executors.newSingleThreadExecutor()
        service.submit(Runnable {
            val request = ApiService.create(context).getUser(login)

            try {
                val response = request.execute()
                user.postValue( response.body())

            } catch (ioException: IOException) {

            }
        })
    }
}