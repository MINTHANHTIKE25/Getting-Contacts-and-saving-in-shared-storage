package com.minthanhtike.phonecontactaccessing

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.Data
import android.provider.Telephony.Mms.Addr
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull


fun Cursor.asContactDetails(): ContactDetail {
    val id = getStringByName(Data._ID)

    val displayName = getStringByName(Data.DISPLAY_NAME_PRIMARY)
    val contactId = getStringByName(Data.CONTACT_ID)
    val accountType = optStringByName(Data.ACCOUNT_TYPE_AND_DATA_SET)
    val mimeType = getStringByName(Data.MIMETYPE)
    Log.d("Image", "id: $contactId , mimeType: $mimeType, accountType: $accountType")
    return ContactDetail(
        id = getStringByName(Data._ID).toLong(),
        displayName = getStringByName(Data.DISPLAY_NAME_PRIMARY),
        contactId = getStringByName(Data.CONTACT_ID),
        accountType = optStringByName(Data.ACCOUNT_TYPE_AND_DATA_SET),
        email = mutableMapOf("email" to getEmail(CommonDataKinds.Email.ADDRESS)!!),
        phoneNumber = mutableMapOf("phNo" to getPhone(CommonDataKinds.Phone.NUMBER)!!),
    )

//    return when (mimeType) {
//
//        CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {
//            val type = getInt(4) // a numeric value representing type: e.g. home / office / personal
//            val label =getString(5)
//            val phMap = mutableMapOf<String, String>()
//            phMap[getStringOrNull(getColumnIndex(CommonDataKinds.Phone.TYPE))!!] =
//                getStringOrNull(getColumnIndex(CommonDataKinds.Phone.NUMBER))!!
//            ContactDetail(
//                id = id,
//                contactId = contactId,
//                accountType = accountType,
//                displayName = displayName,
//                phoneNumber = phMap
//            )
//        }
//
//        CommonDataKinds.Email.CONTENT_ITEM_TYPE -> {
//            val emailMap = mutableMapOf<String, String>()
//            emailMap[getStringOrNull(getColumnIndex(CommonDataKinds.Email.TYPE))!!] =
//                getStringOrNull(getColumnIndex(CommonDataKinds.Email.ADDRESS))!!
//            ContactDetail(
//                id = id,
//                contactId = contactId,
//                accountType = accountType,
//                displayName = displayName,
//                email = emailMap
//            )
//        }
//
//
//        else -> null
//    }


}

data class ContactDetail(
    var id: Long? = null,
    val contactId: String? = null,
    var accountType: String? = null,
    var displayName: String? = null,
    var phoneNumber: MutableMap<String, String> = mutableMapOf(),
    var email: MutableMap<String, String> = mutableMapOf(),
    var address: MutableMap<String, String> = mutableMapOf(),
    var photos: String? = null
)

sealed interface ContactDetails {
    val id: String
    val contactId: String
    val accountType: String?
    val dataType: DetailsType
    val displayName: String?

    enum class DetailsType {
        Name, Phone, Email, WebSite
    }

    data class Name(
        override val id: String,
        override val contactId: String,
        override val accountType: String?,
        override val displayName: String? = null,
        override val dataType: DetailsType = DetailsType.Name
    ) : ContactDetails

    data class Phone(
        override val id: String,
        override val contactId: String,
        override val accountType: String?,
        override val displayName: String? = null,
        override val dataType: DetailsType = DetailsType.Phone,
        val phoneType: Int = -1,
        val phoneNumber: String? = null,
        val phoneLabel: String? = null,
    ) : ContactDetails

    data class Email(
        override val id: String,
        override val contactId: String,
        override val accountType: String?,
        override val displayName: String? = null,
        override val dataType: DetailsType = DetailsType.Email,
        val email: String? = null
    ) : ContactDetails

    data class Website(
        override val id: String,
        override val contactId: String,
        override val accountType: String?,
        override val displayName: String? = null,
        override val dataType: DetailsType = DetailsType.WebSite,
        val url: String? = null
    ) : ContactDetails
}

data class PhNo(
    var phNo: String,
    var label: String
)

data class Address(
    var address: String,
    var label: String
)

data class Email(
    var email: String,
    var label: String
)

fun test(cr: ContentResolver, context: Context): MutableList<ContactDetail> {
    val contactList = mutableListOf<ContactDetail>()
    val projection = arrayOf(
        Data.CONTACT_ID,
        Data.DISPLAY_NAME,
        Data.MIMETYPE,
        Data.DATA1,
        Data.DATA2,
        Data.DATA3,
        Data.PHOTO_ID
    )
    val selection = ContactsContract.Data.MIMETYPE + " in (?, ?, ?)";
    val selectArgs = arrayOf(
        CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
        CommonDataKinds.Email.CONTENT_ITEM_TYPE,
        CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
    )

    val phNo = mutableMapOf<Long, List<PhNo>>()
    val emailMap = mutableMapOf<Long, List<Email>>()
    val addressMap = mutableMapOf<Long, List<Address>>()

    val phList = mutableListOf<PhNo>()
    val emailList = mutableListOf<Email>()
    val addressList = mutableListOf<Address>()

    val cur: Cursor? = cr.query(Data.CONTENT_URI, projection, selection, selectArgs, null)
    while (cur != null && cur.moveToNext()) {
        val id = cur.getLong(0)
        val name = cur.getString(1)
        val mime = cur.getString(2) // email / phone / company
        val data = cur.getString(3) // the actual info, e.g. +1-212-555-1234
        val type = cur.getInt(4) // a numeric value representing type: e.g. home / office / personal
        val label = cur.getString(5) // a custom label in case type is "TYPE_CUSTOM"
        val photoId = cur.getLong(6)
        var kind = "unknown"
        var labelStr = ""
        when (mime) {
            CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {

                kind = "phone"
                labelStr =
                    CommonDataKinds.Phone.getTypeLabel(context.resources, type, label).toString()
                val phno = cur.getStringOrNull(cur.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                phList.add(
                    PhNo(
                        phNo = phno.toString(),
                        label = labelStr
                    )
                )
                phNo[id] = phList

            }

            CommonDataKinds.Email.CONTENT_ITEM_TYPE -> {
                kind = "email"
                labelStr =
                    CommonDataKinds.Email.getTypeLabel(context.resources, type, label).toString()
                val email = cur.getStringOrNull(cur.getColumnIndex(CommonDataKinds.Email.ADDRESS))
                emailList.add(
                    Email(
                        email = email.toString(),
                        label = labelStr
                    )
                )
                emailMap[id] = emailList
            }

            CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE -> {
                kind = "address"
                labelStr =
                    CommonDataKinds.StructuredPostal.getTypeLabel(context.resources, type, label)
                        .toString()
                val address =
                    cur.getStringOrNull(cur.getColumnIndex(CommonDataKinds.StructuredPostal.DATA1))
                addressList.add(
                    Address(
                        address = address.toString(),
                        label = labelStr
                    )
                )
                addressMap[id] = addressList
            }
        }
        Log.d("", "got $id, $name, $kind - $data ($labelStr)")

        val resultContact = ContactDetail(
            id = id,
            displayName = name,
            accountType = null,
            photos = photoId.toString(),
        )
        if (!contactList.contains(resultContact)) {
            contactList.add(resultContact)
        }

    }

    contactList.onEach { contactDetail ->
        println("Processing contact ID: ${contactDetail.id}")

        // Process phone numbers
        phNo[contactDetail.id]?.let { phMap ->
            phMap.forEach { phNo ->
                println("Adding phone number: ${phNo.phNo} with label: ${phNo.label}")
                contactDetail.phoneNumber[phNo.label] = phNo.phNo

            }
        }

        // Process emails
        emailMap[contactDetail.id]?.let { email ->
            email.forEach {
                println("Adding email: ${it.email} with label: ${it.label}")
                contactDetail.email[it.label] = it.email
            }
        }

        // Process addresses
        addressMap[contactDetail.id]?.let { addressMap ->
            addressMap.forEach { address ->
                println("Adding address: ${address.address} with label: ${address.label}")
                contactDetail.address[address.label] = address.address
            }
        }
    }

    cur?.close()
    return contactList
}


fun another(cr: ContentResolver, context: Context): MutableMap<Long, MutableList<ContactDetail>> {
    val contacts: MutableMap<Long, MutableList<ContactDetail>> = HashMap()
    val projection = arrayOf(
        Data.CONTACT_ID,
        Data.DISPLAY_NAME,
        Data.MIMETYPE,
        Data.DATA1,
        Data.DATA2,
        Data.DATA3,
        Data.PHOTO_ID
    )
    val selection = ContactsContract.Data.MIMETYPE + " in (?, ?, ?)";
    val selectArgs = arrayOf(
        CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
        CommonDataKinds.Email.CONTENT_ITEM_TYPE,
        CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
    )

    val phNo = mutableMapOf<Long, PhNo>()
    val emailMap = mutableMapOf<Long, Email>()
    val addressMap = mutableMapOf<Long, Address>()

    val phList = mutableListOf<PhNo>()
    val emailList = mutableListOf<Email>()
    val addressList = mutableListOf<Address>()

    val cur: Cursor? = cr.query(Data.CONTENT_URI, projection, selection, selectArgs, null)
    while (cur != null && cur.moveToNext()) {
        val id = cur.getLong(0)
        val name = cur.getString(1)
        val mime = cur.getString(2) // email / phone / company
        val data = cur.getString(3) // the actual info, e.g. +1-212-555-1234
        val type = cur.getInt(4) // a numeric value representing type: e.g. home / office / personal
        val label = cur.getString(5) // a custom label in case type is "TYPE_CUSTOM"
        val photoId = cur.getLong(6)
        var kind = "unknown"
        var labelStr = ""
        when (mime) {
            CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {

                kind = "phone"
                labelStr =
                    CommonDataKinds.Phone.getTypeLabel(context.resources, type, label).toString()
                val phno = cur.getStringOrNull(cur.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                phNo[id] = PhNo(phno.toString(), labelStr)

            }

            CommonDataKinds.Email.CONTENT_ITEM_TYPE -> {
                kind = "email"
                labelStr =
                    CommonDataKinds.Email.getTypeLabel(context.resources, type, label).toString()
                val email = cur.getStringOrNull(cur.getColumnIndex(CommonDataKinds.Email.ADDRESS))
                emailMap[id] = Email(
                    email = email.toString(),
                    label = labelStr
                )
            }

            CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE -> {
                kind = "address"
                labelStr =
                    CommonDataKinds.StructuredPostal.getTypeLabel(context.resources, type, label)
                        .toString()
                val address =
                    cur.getStringOrNull(cur.getColumnIndex(CommonDataKinds.StructuredPostal.DATA1))
                addressMap[id] = Address(
                    address = address.toString(),
                    label = labelStr
                )
            }
        }
        Log.d("", "got $id, $name, $kind - $data ($labelStr)")

        // add info to existing list if this contact-id was already found, or create a new list in case it's new
        // region getting map values
        var infos: MutableList<ContactDetail> = mutableListOf()

        if (contacts.containsKey(id)) {
            infos = contacts[id]!!
        } else {
            infos.add(
                ContactDetail(
                    id = id,
                    displayName = name,
                    accountType = null,
                    photos = photoId.toString(),
                )
            )
            contacts[id] = infos
        }
        infos.map {
            if (phNo.containsKey(it.id)) {
                it.phoneNumber[phNo[it.id]!!.label] = phNo[it.id]!!.phNo
            }
            if (emailMap.containsKey(it.id)) {
                it.email[emailMap[it.id]!!.label] = emailMap[it.id]!!.email
            }
            if (addressMap.containsKey(it.id)) {
                it.address[addressMap[it.id]!!.label] = addressMap[it.id]!!.address
            }
        }
        //endregion
    }
    cur?.close()
    Log.d("ContactsMap", "another: ${contacts.toList()}")
    return contacts
}