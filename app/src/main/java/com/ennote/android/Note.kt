package com.ennote.android

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var endDate: Date = Date(),
    var content: String = "",
    var isEncrypted: Boolean = false,
    var imageUri: Uri? = null,
)
