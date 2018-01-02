package com.yjp.mediaplatformandroid.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.yjp.mediaplatformandroid.R
import com.yjp.mediaplatformandroid.communicator.HttpCommunicator
import com.yjp.mediaplatformandroid.dto.LogoutForm
import com.yjp.mediaplatformandroid.global.MyApplication
import com.yjp.mediaplatformandroid.global.URLTable
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    private var communicator: HttpCommunicator? = null
    private var contactsQueryUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        communicator = HttpCommunicator(this)

        MyApplication.loginFormResponse?.let {
            contactsQueryUrl = URLTable.CONTACTS_USER_DETAILS_FORMAT.format(it.id)
        }

        logoutButton.setOnClickListener {
            MyApplication.loginFormResponse?.let {
                val logoutForm = LogoutForm()
                logoutForm.id = it.id
                logoutForm.username = it.username

                val jsonLogoutForm = MyApplication.GSON.toJson(logoutForm)
                communicator!!.postAsync(URLTable.LOGOUT,
                        jsonLogoutForm, HttpCommunicator.MEDIA_TYPE_JSON)
            }
        }

        queryButton.setOnClickListener {
            communicator!!.getAsync(contactsQueryUrl, null)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun contactsQueryComplete(event: HttpCommunicator.HttpEvent) {
        if (contactsQueryUrl != event.url) {
            return
        }

        if (event.success == false) {
            Toast.makeText(this, event.data, Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: query complete
    }
}
