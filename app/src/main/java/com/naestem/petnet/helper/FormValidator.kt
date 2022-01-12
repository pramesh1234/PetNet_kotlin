package com.naestem.petnet.helper

import android.util.Patterns
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

object FormValidator {

    //Check for no of character required
    fun requiredField(fieldStr: String?, length: Int): Boolean {
        return if (fieldStr == null) {
            false
        } else {
            fieldStr.length >= length
        }
    }

    //Check for a valid email address
    fun validateEmail(emailAddress: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(emailAddress).matches()
    }

    //Check for a valid 10 digit number
    fun isValidMobile(phone: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PHONE_PATTERN = "^[0-9]{10,13}\$"
        pattern = Pattern.compile(PHONE_PATTERN)
        matcher = pattern.matcher(phone)
        return matcher.matches()
    }

    //Check for a valid SSN (AAA-GG-SSSS)
    fun isValidSSN(ssn: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val SSN_PATTERN = "^(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}\$"
        pattern = Pattern.compile(SSN_PATTERN)
        matcher = pattern.matcher(ssn)
        return matcher.matches()
    }

    //Check for a valid Mobile (xxx-xx-xxxx)
    fun isValidMobileNumber(phone: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PHONE_PATTERN = "^[0-9]{3}-[0-9]{3}-[0-9]{4,10}\$"
        pattern = Pattern.compile(PHONE_PATTERN)
        matcher = pattern.matcher(phone)
        return matcher.matches()
    }

    //checking password req - Password be at least 6 characters long and must contain at least one the following: lowercase letter, uppercase letter, number. Special characters are allowed but spaces are not.
    fun isValidPassword(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).+"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)

        return matcher.matches()

    }

    //Check for a valid website address
    fun validateWebsite(website: String): Boolean {
        return Patterns.WEB_URL.matcher(website).matches()
    }

    fun validateCard(text: String): Boolean {
        val ps = Pattern.compile("^[a-zA-Z ]+$")
        val ms = ps.matcher(text)
        val bs = ms.matches()
        return bs

    }

    fun isPasswordMatches(password: String, confirmPassword: String): String {
        try {
            if (!password.equals(confirmPassword))
                return "false"
            else
                return "true"
        } catch (pE: PatternSyntaxException) {
            return "Invalid password"
        }

    }
}