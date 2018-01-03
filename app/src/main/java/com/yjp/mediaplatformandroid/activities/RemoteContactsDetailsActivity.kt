package com.yjp.mediaplatformandroid.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.yjp.mediaplatformandroid.R

class RemoteContactsDetailsActivity : AppCompatActivity() {

    companion object {
        val NAME_INTENT = "name_intent"
        val PHONE_NUMBERS_INTENT = "phone_number_intent"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_contacts_details)
    }
}
