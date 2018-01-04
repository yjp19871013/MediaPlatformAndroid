package com.yjp.mediaplatformandroid.tools

import android.content.Context
import android.provider.ContactsContract


object LocalContactsTools {

    fun queryContacts(context: Context): MutableMap<String, ArrayList<String>> {
        val cr = context.contentResolver
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                arrayOf(ContactsContract.Contacts._ID, ContactsContract.PhoneLookup.DISPLAY_NAME),
                null,
                null,
                ContactsContract.PhoneLookup.DISPLAY_NAME)

        val contacts = mutableMapOf<String, ArrayList<String>>()

        while(cursor.moveToNext()) {
            val nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
            val name = cursor.getString(nameFieldColumnIndex)

            val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val phoneCursor = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                    arrayOf(contactId),
                    null)

            val phoneNumbers = arrayListOf<String>()
            while(phoneCursor.moveToNext()) {
                val phoneNumber = phoneCursor.getString(
                        phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                phoneNumbers.add(phoneNumber)
            }

            phoneCursor.close()

            contacts.put(name, phoneNumbers)
        }

        cursor.close()
        return contacts
    }

}