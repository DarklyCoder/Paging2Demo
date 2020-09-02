package com.darklycoder.paging2demo.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

class UserViewModel : ViewModel() {

    // 自定义数据源工厂(也可以配合Room组件使用)
    private val factory = UserDataSourceFactory()

    // 分页配置
    private val config = PagedList.Config.Builder()
        .setPageSize(10) // 每页大小
        .setEnablePlaceholders(false) // 是否启用占位符
        .setInitialLoadSizeHint(10) // 初始加载数目
        .setPrefetchDistance(2) // 自动开启预加载间距数目
        .build()

    // 用户列表
    val users: LiveData<PagedList<User>> = LivePagedListBuilder(factory, config).build()

    // 下拉刷新状态
    val refreshState: LiveData<Boolean> = Transformations.switchMap(factory.sourceLiveData) {
        it.refreshState
    }

    /**
     * 重新刷新列表
     */
    fun invalidate() {
        factory.sourceLiveData.value?.invalidate()
    }

}