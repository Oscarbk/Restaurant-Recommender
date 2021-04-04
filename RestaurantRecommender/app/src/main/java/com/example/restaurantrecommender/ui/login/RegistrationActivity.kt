@file:Suppress("PrivatePropertyName")

package com.example.restaurantrecommender.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.restaurantrecommender.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegistrationActivity : AppCompatActivity(){

    private lateinit var username:  EditText
    private lateinit var password:  EditText
    private lateinit var password2: EditText
    private lateinit var register:  Button
    private lateinit var firebase:  FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        username  = findViewById(R.id.username2)
        password  = findViewById(R.id.password2)
        password2 = findViewById(R.id.password3)
        register  = findViewById(R.id.register2)
        firebase  = FirebaseAuth.getInstance()

        // Register a new user
        register.setOnClickListener {
            val inputUsername  = username.text.toString().trim()
            val inputPassword  = password.text.toString()

            // TODO: use string resources when second language is required
            firebase.createUserWithEmailAndPassword(inputUsername, inputPassword).addOnCompleteListener { task ->
                if (task.isSuccessful)
                    Toast.makeText(this, "Successfully registered as $inputUsername", Toast.LENGTH_LONG).show()
                else {
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "An account already exists for $inputUsername", Toast.LENGTH_LONG).show()
                    } else {
                        // We could also split out other exceptions to further customize errors
                        // https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthException
                        Toast.makeText(this, "Failed to register: $exception", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        username .addTextChangedListener(TextWatcher)
        password .addTextChangedListener(TextWatcher)
        password2.addTextChangedListener(TextWatcher)
    }

    // Detect when fields have input
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
        // Not needed
        override fun afterTextChanged(s: Editable?) {}
    }
}