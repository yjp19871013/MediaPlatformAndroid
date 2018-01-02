package com.yjp.mediaplatformandroid.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import com.yjp.mediaplatformandroid.R
import com.yjp.mediaplatformandroid.adapter.HomeAdapter
import com.yjp.mediaplatformandroid.communicator.HttpCommunicator
import com.yjp.mediaplatformandroid.dto.LogoutForm
import com.yjp.mediaplatformandroid.global.MyApplication
import com.yjp.mediaplatformandroid.global.URLTable
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    companion object {
        private val ITEM_TITLES = arrayListOf("通讯录", "敬请期待")
        private val ITEM_ACTIVITY_MAP = mapOf(
                "通讯录" to ContactsActivity::class.java,
                "敬请期待" to null
        )
    }

    private var communicator: HttpCommunicator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        communicator = HttpCommunicator(this)

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        val adapter = HomeAdapter(this, ITEM_TITLES)

        recyclerView?.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        MyApplication.loginFormResponse?.let {
            val logoutForm = LogoutForm()
            logoutForm.id = it.id
            logoutForm.username = it.username

            val jsonLogoutForm = MyApplication.GSON.toJson(logoutForm)
            communicator!!.postAsync(URLTable.LOGOUT,
                    jsonLogoutForm, HttpCommunicator.MEDIA_TYPE_JSON)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHomeItemClicked(event: HomeAdapter.ItemClicked) {
        val title = event.title
        val cls = ITEM_ACTIVITY_MAP.get(title)
        if (cls != null) {
            val intent = Intent(this, cls)
            startActivity(intent)
        } else {
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
        }
    }
}
