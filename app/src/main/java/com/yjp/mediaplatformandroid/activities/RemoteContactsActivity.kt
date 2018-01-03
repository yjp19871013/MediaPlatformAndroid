package com.yjp.mediaplatformandroid.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.SimpleAdapter
import android.widget.Toast
import com.yjp.mediaplatformandroid.R
import com.yjp.mediaplatformandroid.communicator.HttpCommunicator
import com.yjp.mediaplatformandroid.dialogs.WaitDialog
import com.yjp.mediaplatformandroid.entities.RemoteContact
import com.yjp.mediaplatformandroid.entities.RemoteContactQueryResponse
import com.yjp.mediaplatformandroid.entities.RemoteContactUpdateResponse
import com.yjp.mediaplatformandroid.global.MyApplication
import com.yjp.mediaplatformandroid.global.URLTable
import kotlinx.android.synthetic.main.activity_remote_contacts.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.RuntimeException

class RemoteContactsActivity : AppCompatActivity() {

    private var mAdapter: SimpleAdapter? = null
    private var dataKeys = arrayOf("name", "first_phone_num")
    private var data = mutableListOf<Map<String, String>>()
    private var communicator: HttpCommunicator? = null
    private var phoneNumberMap = mutableMapOf<String, ArrayList<String>>()

    private val dataQueryUrl =
            URLTable.CONTACTS_USER_DETAILS_FORMAT.format(MyApplication.loginFormResponse!!.id)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_contacts)

        mAdapter = SimpleAdapter(this,
                data,
                android.R.layout.simple_list_item_2,
                dataKeys,
                intArrayOf(android.R.id.text1, android.R.id.text2))
        listView.adapter = mAdapter

        listView.setOnItemClickListener {
            _,  _, postion,  _ ->
            val intent = Intent(this@RemoteContactsActivity,
                    RemoteContactsDetailsActivity::class.java)
            val name = data[postion][dataKeys[0]]
            val phoneNumbers = phoneNumberMap[name]
            intent.putExtra(RemoteContactsDetailsActivity.NAME_INTENT, name)
            intent.putStringArrayListExtra(
                    RemoteContactsDetailsActivity.PHONE_NUMBERS_INTENT, phoneNumbers)
            startActivity(intent)
        }

        listView.emptyView = emptyView
        startBackupButton.setOnClickListener {
            doBackUp()
        }

        communicator = HttpCommunicator(this)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

        loadData()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.refresh -> loadData()
            else -> throw RuntimeException("Should not come here.")
        }
        return true
    }

    private fun loadData() {
        WaitDialog.showWaitDialog(this, "加载数据")
        communicator!!.getAsync(dataQueryUrl)
    }

    private fun clearData() {
        phoneNumberMap.clear()
        data.clear()
    }

    private fun doBackUp() {
        val user_id = MyApplication.loginFormResponse!!.id
        val contact1 = RemoteContact(userId = user_id, name = "yjp", phoneNumber = "123456789")
        val contact2 = RemoteContact(userId = user_id, name = "yjp", phoneNumber = "012345678")
        val contact3 = RemoteContact(userId = user_id, name = "yjp1", phoneNumber = "123456789")
        val contact4 = RemoteContact(userId = user_id, name = "yjp2", phoneNumber = "123456789")
        val contacts = arrayListOf(contact1, contact2, contact3, contact4)

        WaitDialog.showWaitDialog(this, "正在备份")
        val json = MyApplication.GSON.toJson(contacts)
        communicator!!.postAsync(URLTable.CONTACTS_UPDATE, json, HttpCommunicator.MEDIA_TYPE_JSON)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun backupComplete(event: HttpCommunicator.HttpEvent) {
        WaitDialog.dismissWaitDialog()

        if (URLTable.CONTACTS_UPDATE != event.url) {
            return
        }

        if (!event.success) {
            Toast.makeText(this, "备份失败", Toast.LENGTH_SHORT).show()
            return
        }

        val response = MyApplication.GSON.fromJson(event.data, RemoteContactUpdateResponse::class.java)
        if (!response.error.isEmpty()) {
            Toast.makeText(this, response.error, Toast.LENGTH_SHORT).show()
            return
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loadDataComplete(event: HttpCommunicator.HttpEvent) {
        WaitDialog.dismissWaitDialog()

        if (dataQueryUrl != event.url) {
            return
        }

        if (!event.success) {
            Toast.makeText(this, "数据获取失败", Toast.LENGTH_SHORT).show()
            return
        }

        val response = MyApplication.GSON.fromJson(event.data, RemoteContactQueryResponse::class.java)
        if (!response.error.isEmpty()) {
            Toast.makeText(this, response.error, Toast.LENGTH_SHORT).show()
            return
        }

        clearData()

        val remoteContacts = response.data
        remoteContacts
                .sortedBy { it.name }
                .forEach {
                    val name = it.name
                    val phoneNumber = it.phoneNumber

                    if (phoneNumberMap.containsKey(name)) {
                        phoneNumberMap[name]!!.add(phoneNumber)
                    } else {
                        phoneNumberMap.put(name, arrayListOf(phoneNumber))
                        data.add(mapOf(
                                dataKeys[0] to name,
                                dataKeys[1] to phoneNumber
                        ))
                    }
                }

        mAdapter!!.notifyDataSetChanged()
    }
}
