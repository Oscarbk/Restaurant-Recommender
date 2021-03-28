package com.example.restaurantrecommender.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import com.example.restaurantrecommender.R

class RegistrationActivity : AppCompatActivity() {

    private lateinit var username:  EditText
    private lateinit var password:  EditText
    private lateinit var password2: EditText
    private lateinit var register:  Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        username  = findViewById(R.id.username2)
        password  = findViewById(R.id.password2)
        password2 = findViewById(R.id.password3)
        register  = findViewById(R.id.register2)

        register.setOnClickListener {
            val inputUsername  = username.text.toString()
            val inputPassword  = password.text.toString()

        }

        username .addTextChangedListener(TextWatcher)
        password .addTextChangedListener(TextWatcher)
        password2.addTextChangedListener(TextWatcher)
    }
    // Detect when search bar has input
    private val TextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputUsername  = username .text.toString()
            val inputPassword  = password .text.toString()
            val inputPassword2 = password2.text.toString()

            // TODO: Add a more robust registration system:
            // * verify email is valid
            // * verify email is not already registered
            // * ensure password is strong
            var allowRegistration = false
            if ((inputUsername.isNotEmpty()) && (inputPassword.isNotEmpty()) && (inputPassword == inputPassword2))
                allowRegistration = true
            register.isEnabled = allowRegistration
        }
        override fun afterTextChanged(s: Editable?) {
        }

    }
}