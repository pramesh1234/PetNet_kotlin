package com.naestem.petnet.model

data class AddressModel(
    val subLocality: String?=null,
    val postalCode: String?=null,
    val city: String?=null,
    val state: String?=null,
    val country: String?=null,
    val longitude: Double?=null,
    val latitude: Double?=null
)