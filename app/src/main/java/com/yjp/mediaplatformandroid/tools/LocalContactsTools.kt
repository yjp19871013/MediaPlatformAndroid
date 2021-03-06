package com.yjp.mediaplatformandroid.tools

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract


object LocalContactsTools {

    fun queryContacts(context: Context): MutableMap<String, ArrayList<String>> {
        val cr = context.contentResolver
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                arrayOf(ContactsContract.Contacts._ID, ContactsContract.PhoneLookup.DISPLAY_NAME),
                null,
                null,
                null)

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

    fun deleteContacts(context: Context, name: String, phoneNumber: String) {
        val cr = context.contentResolver
        cr.delete(ContactsContract.Data.CONTENT_URI, ContactsContract.Data.DISPLAY_NAME + "=? and "
                + ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                arrayOf(name, phoneNumber))
    }

    fun modifyContacts(context: Context, name: String, oldPhoneNumber: String, newPhoneNumber: String) {
        val cr = context.contentResolver
        val cv = ContentValues()
        cv.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)
        cr.update(ContactsContract.Data.CONTENT_URI, cv,
                ContactsContract.Data.DISPLAY_NAME + "=? and "
                        + ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                arrayOf(name, oldPhoneNumber))
    }

    fun addPhoneNumber(context: Context, name: String, phoneNumber: String) {
        val cr = context.contentResolver
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                arrayOf(ContactsContract.Contacts._ID),
                ContactsContract.Data.DISPLAY_NAME + "=?",
                arrayOf(name),
                null)

        if (cursor.count == 0) {
            // A new contact
            val rawContactUri = cr.insert(ContactsContract.RawContacts.CONTENT_URI, ContentValues())
            val contactId = ContentUris.parseId(rawContactUri).toString()

            val cv = ContentValues()
            cv.put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
            cv.put(ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            cv.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
            cr.insert(ContactsContract.Data.CONTENT_URI, cv)

            insertPhoneNumber(cr, contactId, phoneNumber)
        } else {
            // An exist contact
            while (cursor.moveToNext()) {
                val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val phoneCursor = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=? and " +
                                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                        arrayOf(contactId, phoneNumber),
                        null)
                if (phoneCursor.count == 0) {
                    insertPhoneNumber(cr, contactId, phoneNumber)
                }

                phoneCursor.close()
            }
        }

        cursor.close()
    }
    private fun insertPhoneNumber(cr: ContentResolver, contactId: String, phoneNumber: String) {
        val cv = ContentValues()
        cv.put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
        cv.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        cv.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
        cv.put(ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        cr.insert(ContactsContract.Data.CONTENT_URI, cv)
    }
}