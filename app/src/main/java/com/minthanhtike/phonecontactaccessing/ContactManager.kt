package com.minthanhtike.phonecontactaccessing

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections

class ContactManager(
    private val contentResolver: ContentResolver,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private fun query(
        uri: Uri,
        projection: Array<String>,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sort: String? = null
    ): Cursor? {
        return contentResolver.query(uri, projection, selection, selectionArgs, sort)
    }

    suspend fun getContacts(): Contacts = withContext(dispatcher) {
        fetchContacts() ?: Collections.emptyList()
    }

    private fun fetchContacts(): List<Contact>? {
        return try {
            val cursor = query(
                uri = ContactsContract.Contacts.CONTENT_URI,
                projection = CONTACTS_PROJECTION,
                sort = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
            )

            val result: List<Contact>? = cursor.map { it.asContact() }
            result?.let {
                Log.d("LISTRESULT", "fetchContacts: $it")
            }

            cursor?.close()
            result
        } catch (e: Exception) {
            Log.d("Thumb Error", "fetchContacts: ${e.localizedMessage ?: "AN UNKNOWN"}")
            null
        }
    }

    fun getContactDetail(context: Context): List<ContactDetail>? {
        return try {
            val selection = ContactsContract.Data.MIMETYPE + " in (?, ?, ?)";
            val selectArgs = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
            )
            val cursor: Cursor? = query(
                uri = ContactsContract.Data.CONTENT_URI,
                projection = CONTACT_DATA_PROJECTION,
                selection = selection,
                selectionArgs = selectArgs,
                sort = null
            )
//            val result: List<ContactDetail?>? = cursor?.map { it.asContactDetails() }
//            result?.let {
//                Log.d("LISTRESULT", "fetchContacts: $it")
//            }
//            cursor?.close()
            val ans =test(contentResolver, context = context )
            Log.d("ContactDetails", "getContactDetail: ${ans.toList()} ${ans.size}" )
            return ans
        } catch (e: Exception) {
            Log.d("EXCEPTION", "getContactDetail: ${e.localizedMessage}")
            null
        }

    }


    companion object {

        @JvmStatic
        private val CONTACTS_PROJECTION = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        )

        @JvmStatic
        private val CONTACT_DATA_PROJECTION = arrayOf(
            ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.ACCOUNT_TYPE_AND_DATA_SET,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.DATA1,
            ContactsContract.Data.DATA2,
            ContactsContract.Data.DATA3,
            ContactsContract.Data.PHOTO_ID,
        )
    }
}