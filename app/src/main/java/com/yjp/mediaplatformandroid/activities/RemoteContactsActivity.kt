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
import com.yjp.mediaplatformandroid.global.MyApplication
import com.yjp.mediaplatformandroid.global.URLTable
import com.yjp.mediaplatformandroid.global.jsonToArrayList
import kotlinx.android.synthetic.main.activity_listview.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.RuntimeException

class RemoteContactsActivity : AppCompatActivity() {

    private var mAdapter: SimpleAdapter? = null
    private var dataKeys = arrayOf("name", "first_phone_num")
    private var data = mutableListOf<Map<String, String>>()
    private var communicator: HttpCommunicator? = null
    private var phoneNumberMap = mutableMapOf<String, MutableList<String>>()

    private val dataQueryUrl =
            URLTable.CONTACTS_USER_DETAILS_FORMAT.format(MyApplication.loginFormResponse!!.id)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listview)

        mAdapter = SimpleAdapter(this,
                data,
                android.R.layout.simple_list_item_2,
                dataKeys,
                intArrayOf(android.R.id.text1, android.R.id.text2))
        listView.adapter = mAdapter
        listView.setOnItemClickListener {
            adapterView, view, postion, id ->
            val intent = Intent(this@RemoteContactsActivity,
                    RemoteContactsDetailsActivity::class.java)
            startActivity(intent)
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

        clearData()
        val remoteContacts = MyApplication.GSON.jsonToArrayList(event.data, RemoteContact::class.java)
        remoteContacts
                .sortedBy { it.name }
                .forEach {
                    val name = it.name
                    val phoneNumber = it.phoneNumber

                    if (phoneNumberMap.containsKey(name)) {
                        phoneNumberMap[name]!!.add(phoneNumber)
                    } else {
                        phoneNumberMap.put(name, mutableListOf(phoneNumber))
                        data.add(mapOf(
                                dataKeys[0] to name,
                                dataKeys[1] to phoneNumber
                        ))
                    }
                }
        mAdapter!!.notifyDataSetChanged()
    }
}
