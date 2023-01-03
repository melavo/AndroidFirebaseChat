package com.melavo.firebasechat.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Country(
    val code: String, val name: String, val noCode: String,
    val money: String
) : Parcelable
