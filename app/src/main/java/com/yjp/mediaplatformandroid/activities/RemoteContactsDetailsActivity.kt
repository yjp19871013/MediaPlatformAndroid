package com.yjp.mediaplatformandroid.activities

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.yjp.mediaplatformandroid.R
import com.yjp.mediaplatformandroid.databinding.ActivityRemoteContactsDetailsBinding
import kotlinx.android.synthetic.main.activity_remote_contacts_details.*

class RemoteContactsDetailsActivity : AppCompatActivity() {

    companion object {
        val NAME_INTENT = "name_intent"
        val PHONE_NUMBERS_INTENT = "phone_number_intent"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataBinding: ActivityRemoteContactsDetailsBinding =
                DataBindingUtil.setContentView(
                        this, R.layout.activity_remote_contacts_details)

        val name = intent.getStringExtra(NAME_INTENT)
        dataBinding.title = "$name 号码列表"

        val phoneNumbers = intent.getStringArrayListExtra(PHONE_NUMBERS_INTENT)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, phoneNumbers)
        phoneNumberListView.adapter = adapter
    }
}
