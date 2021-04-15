package com.example.restaurantrecommender.ui.login

data class Restaurant (
    val name: String,
    val title: String,
    val rating: Double,
    val price: String,
    val description: String,
    val address: String,
    val menu: String,
    val iconUrl: String,
    val transaction: String,
    val businessID: String,
)