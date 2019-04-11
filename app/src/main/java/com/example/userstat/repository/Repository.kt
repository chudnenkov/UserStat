/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.userstat.repository



import androidx.annotation.MainThread
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData

import com.example.userstat.api.ApiService
import com.example.userstat.model.User
import java.util.concurrent.Executor


class Repository(private val api: ApiService,
                 private val networkExecutor: Executor) {
    @MainThread
    fun postsOfSubreddit(since: String, pageSize: Int): Listing<User> {
        val sourceFactory = UsersDataSourceFactory(api, since, networkExecutor)


        val livePagedList = sourceFactory.toLiveData(
                pageSize = pageSize,
                fetchExecutor = networkExecutor)

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }
        return Listing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                  it.networkState
                },
                retry = {
                    sourceFactory.sourceLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.sourceLiveData.value?.invalidate()
                },
                refreshState = refreshState
        )
    }
}

