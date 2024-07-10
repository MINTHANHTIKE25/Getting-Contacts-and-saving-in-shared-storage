package com.minthanhtike.phonecontactaccessing

import android.database.Cursor
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull

fun <T> Cursor?.map(block: (Cursor) -> T): List<T>? {
    return this?.let {
        val result = mutableListOf<T>()
        if(moveToFirst()){
            do {
                result.add(block(this))
            }while (moveToNext())
        }
        result
    }
}

fun Cursor.optStringByName(columnName: String): String? {
    val index = getColumnIndex(columnName)
    return getStringOrNull(index)
}
fun Cursor.getEmail(columnName: String):String?{
    val emailIndex = getColumnIndex(columnName)
    return getStringOrNull(emailIndex)
}

fun Cursor.getPhone(columnName: String):String?{
    val phIndex = getColumnIndex(columnName)
    return getStringOrNull(phIndex)
}

fun Cursor.optIntByName(columnName: String, defValue: Int = -1): Int {
    val index = getColumnIndex(columnName)
    return getIntOrNull(index) ?: defValue
}

fun Cursor.getStringByName(columnName: String): String {
    return getString(getColumnIndexOrThrow(columnName))
}

fun Cursor.getImageUriByName(columnName: String): String? {
    return getString(getColumnIndexOrThrow(columnName)) ?: null
}

fun Cursor.getLongByName(columnName: String):Long{
    return getLong(getColumnIndexOrThrow(columnName))
}

fun Cursor.getIntByName(columnName: String): Int {
    return getInt(getColumnIndexOrThrow(columnName))
}
