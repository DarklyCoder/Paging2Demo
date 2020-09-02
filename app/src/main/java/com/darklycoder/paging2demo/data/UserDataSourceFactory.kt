package com.darklycoder.paging2demo.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PositionalDataSource
import kotlin.math.min

class UserDataSourceFactory : DataSource.Factory<Int, User>() {
    val sourceLiveData = MutableLiveData<UserDataSource3>()

    override fun create(): DataSource<Int, User> {
        val latestSource = UserDataSource3()
        sourceLiveData.postValue(latestSource)
        return latestSource
    }
}

/**
 * 适用于目标数据的加载依赖特定条目的信息（比如聊天消息）
 */
class UserDataSource : ItemKeyedDataSource<Int, User>() {
    override fun getKey(item: User): Int {
        return item.uid
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<User>) {
        val items = fetchItems(
            params.requestedInitialKey,
            params.requestedLoadSize
        )
        Log.d("test", "loadInitial")
        callback.onResult(items)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<User>) {
        val items = fetchItems(
            params.key,
            params.requestedLoadSize
        )
        Log.d("test", "loadAfter")
        callback.onResult(items)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<User>) {
        val items = fetchItems(
            params.key,
            params.requestedLoadSize
        )
        Log.d("test", "loadBefore")
        callback.onResult(items)
    }

    private fun fetchItems(key: Int?, requestedLoadSize: Int): ArrayList<User> {
        val index = key ?: 1
        val start = (index - 1) * requestedLoadSize
        val end = start + requestedLoadSize - 1
        val users = ArrayList<User>()
        for (i in start..end) {
            users.add(User(i, "name$i"))
        }

        Thread.sleep(500)

        return users
    }
}

/**
 * 适用于总数固定的场景（Room内置）
 */
class UserDataSource2 : PositionalDataSource<User>() {

    private val users = ArrayList<User>()

    init {
        for (i in 0..100) {
            users.add(User(i, "name$i"))
        }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<User>) {
        val items = fetchItems(
            params.requestedStartPosition,
            params.requestedLoadSize
        )
        Log.d("test", "loadInitial")
        callback.onResult(items, 0, 101)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<User>) {
        val items = fetchItems(
            params.startPosition,
            params.loadSize
        )
        Log.d("test", "loadRange")
        callback.onResult(items)
    }

    private fun fetchItems(position: Int, size: Int): List<User> {
        Thread.sleep(500)

        return users.subList(position, min(101, position + size))
    }
}

class UserDataSource3 : PageKeyedDataSource<Int, User>() {

    // 下拉刷新状态
    val refreshState = MutableLiveData<Boolean>()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, User>
    ) {
        refreshState.postValue(true)
        val items = fetchItems(
            1,
            params.requestedLoadSize
        )
        Log.d("test", "loadInitial")
        callback.onResult(items, null, 2)
        refreshState.postValue(false)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        val items = fetchItems(
            params.key,
            params.requestedLoadSize
        )
        Log.d("test", "loadAfter")
        callback.onResult(items, params.key + 1)
    }

    private fun fetchItems(key: Int?, requestedLoadSize: Int): ArrayList<User> {
        val index = key ?: 1
        val start = (index - 1) * requestedLoadSize
        val end = start + requestedLoadSize - 1
        val users = ArrayList<User>()
        for (i in start..end) {
            users.add(User(i, "name$i"))
        }

        Thread.sleep(500)

        return users
    }

}