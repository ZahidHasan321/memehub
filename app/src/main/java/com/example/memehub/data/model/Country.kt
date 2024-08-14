package com.example.memehub.data.model

data class Country(
    val name: String,
    val emoji: String,
    val state: List<State>
)

data class State(
    val name:String
)