package com.darklycoder.paging2demo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.darklycoder.paging2demo.data.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val vm: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = UserListAdapter()
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter

        vm.users.observe(this) {
            adapter.submitList(it)
        }

        vm.refreshState.observe(this) {
            srl_refresh.isRefreshing = it
        }

        srl_refresh.setOnRefreshListener {
            vm.invalidate()
        }
    }

}