package com.example.userstat.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.userstat.R
import com.example.userstat.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        intent = getIntent()
        val avatar_url  = intent.getStringExtra("avatar_url")
        val login= intent.getStringExtra("login")
        val url = intent.getStringExtra("url")

        supportActionBar?.setTitle(login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loginView.text = login
        urlView.text = url
        urlView.setOnClickListener(View.OnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(url)
            startActivity(openURL)
        })

        Picasso.with(this)
            .load(avatar_url)
            .placeholder(R.drawable.circle_shape)
            .error(R.drawable.notloaded)
            .into(imageView)

        val model = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {

                return UserViewModel(login, applicationContext) as T
            }
        })[UserViewModel::class.java]

        model.getUsers().observe(this, Observer<User>{ user ->
            public_reposView.text = getString(R.string.repos) + user.public_repos.toString()
            public_gistsView.text = getString(R.string.gists) + user.public_gists.toString()
            followersView.text = getString(R.string.follower) +  user.followers.toString()
        })
    }
}