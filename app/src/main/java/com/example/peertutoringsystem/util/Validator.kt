package com.example.peertutor.util

object Validator {

    fun isNotBlank(text: String?): Boolean =
        !text.isNullOrBlank()

    // Phone: exactly 10 digits
    fun isValidPhone(phone: String?): Boolean =
        phone != null && phone.matches(Regex("^\\d{10}$"))

    // Fee: a number above 0
    fun isValidFee(fee: String?): Boolean {
        val value = fee?.trim()?.toDoubleOrNull() ?: return false
        return value > 0
    }

    // Level: not blank (change to a fixed list if your group agrees on levels)
    fun isValidLevel(level: String?): Boolean =
        !level.isNullOrBlank()

    // Mode: must be exactly ONLINE or IN_PERSON
    fun isValidMode(mode: String?): Boolean =
        mode == "ONLINE" || mode == "IN_PERSON"
}