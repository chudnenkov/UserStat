package com.example.userstat.model

data class User(
    val id: Int, val avatar_url: String, val login: String, val url: String,
    val public_repos: Int, val public_gists: Int, val followers : Int
)