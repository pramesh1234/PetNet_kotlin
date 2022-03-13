package com.naestem.petnet.model

data class AddPetModel(
    val species: String? = null,
    val breed: String? = null,
    val location: AddressModel? = null,
    val description: String? = null,
    val images: ArrayList<String>? = null,
    val age: String? = null,
    val forAdoption: Boolean? = null,
    val price: String? = null,
    var createdBy: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)