package com.example.userstat.view


import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

import com.example.userstat.repository.Repository

class UsersViewModel(private val repository: Repository): ViewModel() {
    private val name = androidx.lifecycle.MutableLiveData<String>()
    private val repoResult = Transformations.map(name) {
        repository.postsOfSubreddit(0.toString(), 10)
    }
    val posts = Transformations.switchMap(repoResult, { it.pagedList })!!
    val networkState = Transformations.switchMap(repoResult, { it.networkState })!!

    val refreshState = Transformations.switchMap(repoResult, { it.refreshState })!!

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun showTest(nm: String): Boolean {
        if (name.value == nm) {
            return false
        }
        name.value = nm
        return true
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }

    fun currentName(): String? = name.value
}


