package com.example.restaurantrecommender.ui.login.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Maybe blacklisted and manually added restaurants will go here"
    }
    val text: LiveData<String> = _text
}