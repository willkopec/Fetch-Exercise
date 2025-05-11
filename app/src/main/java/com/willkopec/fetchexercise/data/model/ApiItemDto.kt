package com.willkopec.fetchexercise.data.model

import com.google.gson.annotations.SerializedName

data class ApiItemDto(

    @SerializedName("id")
    val id: Int,
    @SerializedName("listId")
    val listId: Int,
    @SerializedName("name")
    val name: String?

)