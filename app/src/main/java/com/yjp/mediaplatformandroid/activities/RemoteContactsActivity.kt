package com.yjp.mediaplatformandroid.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
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
import com.yjp.mediaplatformandroid.tools.LocalContactsTools
import kotlinx.android.synthetic.main.activity_remote_contacts.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.RuntimeException
import java.util.concurrent.Executors

class RemoteContactsActivity : AppCompatActivity() {

    private var mAdapter: SimpleAdapter? = null
    private var dataKeys = arrayOf("name", "first_phone_num")
    private var data = mutableListOf<Map<String, String>>()
    private var communicator: HttpCommunicator? = null
    private var phoneNumberMap = mutableMapOf<String, ArrayList<String>>()

    private val executorService = Executors.newSingleThreadExecutor()

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
        executorService.shutdownNow()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.backup -> doBackUp()
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
        mAdapter!!.notifyDataSetChanged()
    }

    private fun doBackUp() {
        AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("本操作会将您本地通信录中的手机号码上传到服务器，确定进行操作?")
                .setCancelable(false)
                .setPositiveButton("确定") {
                    _, _ ->
                    uploadData()
                }
                .setNegativeButton("取消", null)
                .show()
    }

    private fun uploadData() {
        WaitDialog.showWaitDialog(this, "正在备份")
        executorService.execute(QueryContactsRunnable())
    }

    @Subscribe
    fun queryContactsComplete(event: QueryContactsRunnable.QueryContactsEvent) {
        val json = MyApplication.GSON.toJson(event.contacts)
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

        loadData()
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
        } else {
            clearData()

            val remoteContacts = response.data
            remoteContacts.forEach {
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

    inner class QueryContactsRunnable: Runnable {
        inner class QueryContactsEvent(val contacts: ArrayList<RemoteContact>)

        override fun run() {
            val localContacts =
                    LocalContactsTools.queryContacts(this@RemoteContactsActivity)
            val userId = MyApplication.loginFormResponse!!.id
            val contacts = arrayListOf<RemoteContact>()

            localContacts.keys.forEach {
                name ->
                val phoneNumbers = localContacts[name]!!
                phoneNumbers.forEach {
                    val contact = RemoteContact(userId = userId, name = name, phoneNumber = it)
                    contacts.add(contact)
                }
            }

            EventBus.getDefault().post(QueryContactsEvent(contacts))
        }
    }
}
