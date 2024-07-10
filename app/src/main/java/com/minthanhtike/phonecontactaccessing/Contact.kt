package com.minthanhtike.phonecontactaccessing

import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract

fun Cursor.asContact(): Contact {
    return Contact(
        id = getStringByName(ContactsContract.Contacts._ID),
        name = getStringByName(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY),
        hasPhoneNumber = getIntByName(ContactsContract.Contacts.HAS_PHONE_NUMBER) == 1,
        photoUri = getImageUriByName(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
    )
}

typealias Contacts = List<Contact>

data class Contact(
    val id: String,
    val name: String,
    val hasPhoneNumber: Boolean = false,
    val photoUri:String? ,
)