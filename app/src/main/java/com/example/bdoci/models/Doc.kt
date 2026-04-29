package com.example.bdoci.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "documents")
data class Doc(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("document")
    val document: String,
    @SerializedName("code")
    val code: String?,
    @SerializedName("category")
    val category: String
) {
    @Ignore
    var isFavorite: Boolean = false
}
