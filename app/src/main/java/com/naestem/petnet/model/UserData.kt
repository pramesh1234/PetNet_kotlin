package com.naestem.petnet.model

data class UserData(
    val userId: String?=null,
    val fullName: String?=null,
    val email: String?=null,
    val phoneNo: String?=null,
    val password: String?=null,
    val address:AddressModel?=null,
    val emailVerified: Boolean?=null,
    val mobileNoVerified: Boolean?=null
)