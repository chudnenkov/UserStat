package com.example.userstat.view



import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList

import com.example.userstat.R
import com.example.userstat.adapter.UsersAdapter
import com.example.userstat.api.ApiService
import com.example.userstat.model.User
import com.example.userstat.repository.Repository
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var model: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        model = getViewModel()
        initAdapter()
        model.showTest("")
    }

    private fun getViewModel(): UsersViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = Repository(ApiService.create(applicationContext), Executors.newFixedThreadPool(5))
                @Suppress("UNCHECKED_CAST")
                return UsersViewModel(repo) as T
            }
        })[UsersViewModel::class.java]
    }
    private fun initAdapter() {
        val adapter = UsersAdapter(){
            clickListener(it)
        }
        mainRecyclerView.adapter = adapter
        model.posts.observe(this, Observer<PagedList<User>> {
            adapter.submitList(it)
        })
        model.networkState.observe(this, Observer {

        })
    }

    private fun clickListener(user : User){
        intent = Intent(this, UserActivity::class.java)
        intent.putExtra("avatar_url", user.avatar_url )
        intent.putExtra("login" , user.login)
        intent.putExtra("url", user.url )
        startActivity(intent)
    }

}
