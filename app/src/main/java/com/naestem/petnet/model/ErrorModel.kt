package com.naestem.petnet.model

import com.naestem.petnet.helper.ErrorMessageType
import java.util.*


class ErrorModel(message: String, val messageType: ErrorMessageType) {
    val message: String = message
        get() = field.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}