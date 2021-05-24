package com.ennote.android.database

import android.net.Uri
import androidx.room.TypeConverter
import java.util.*

class NoteTypeConverters {

    @TypeConverter
    fun fromEndDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toEndDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let { Date(it) }
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun fromImageUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toImageUri(uri: String?): Uri? {
        return if (uri != null) {
            Uri.parse(uri)
        } else {
            null
        }
    }
}