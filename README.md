# Jetpack之分页组件 - Paging

## 配置

```gradle
implementation "androidx.paging:paging-runtime-ktx:2.1.2"
```

## 基本使用

1. 构建数据源

    自定义`DataSource.Factory<Key, Value>`：

    ```kotlin
    class UserDataSourceFactory : DataSource.Factory<Int, User>() {
        val sourceLiveData = MutableLiveData<UserDataSource3>()

        override fun create(): DataSource<Int, User> {
            val latestSource = UserDataSource3()
            sourceLiveData.postValue(latestSource)
            return latestSource
        }
    }

    // 自定义数据源，可根据实际使用场景选择要实现的数据源(ItemKeyedDataSource/PositionalDataSource/PageKeyedDataSource)
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
    ```

2. 关联数据

    在`ViewModel`中使用`LivePagedListBuilder`关联数据:

    ```kotlin
    // 自定义数据源工厂(也可以配合Room组件使用)
    private val factory = UserDataSourceFactory()

    // 分页配置
    private val config = PagedList.Config.Builder()
        .setPageSize(10) // 每页大小
        .setEnablePlaceholders(false) // 是否启用占位符
        .setInitialLoadSizeHint(10) // 初始加载数目
        .setPrefetchDistance(2) // 自动开启预加载间距数目
        .build()

    val users: LiveData<PagedList<User>> = LivePagedListBuilder(factory, config).build()
    ```

3. 构建adapter

    实现自定义`PagedListAdapter`：

    ```kotlin
    class UserListAdapter : PagedListAdapter<User, UserListAdapter.UserViewHolder>(DIFF_CALLBACK) {

        companion object {
            // 比对接口
            private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
                override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.uid == newItem.uid
                override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserViewHolder(parent)

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) =
            holder.bindData(getItem(position))

        class UserViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        ) {

            private val tvName = itemView.findViewById<TextView>(R.id.tv_name)

            fun bindData(item: User?) {
                tvName.text = item?.name
            }
        }
    }
    ```

4. 展示数据

    在界面中监听数据变化，显示数据：

    ```
    vm.users.observe(this) {
        adapter.submitList(it)
    }
    ```

如此就完成基于`RecyclerView`的数据自动分页加载逻辑。