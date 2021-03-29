package com.example.restaurantrecommender.ui.login.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "A recyclerview of recent restaurants will go here"
    }
    val text: LiveData<String> = _text
}