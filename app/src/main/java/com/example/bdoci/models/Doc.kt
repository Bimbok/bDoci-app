package com.example.bdoci.models

data class Doc(
    val _id: String,
    val title: String,
    val document: String,
    val code: String?,
    val category: String
)