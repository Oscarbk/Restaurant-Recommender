package com.example.restaurantrecommender.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.example.restaurantrecommender.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var firebase: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.name)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)
        val register = findViewById<Button>(R.id.register)
        firebase  = FirebaseAuth.getInstance()
        val preferences = getSharedPreferences("restaurantRecommender", Context.MODE_PRIVATE)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        register.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                                username.text.toString(),
                                password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                val inputUsername = username.text.toString().trim()
                val inputPassword = password.text.toString()

                firebase.signInWithEmailAndPassword(inputUsername, inputPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val savedSearch = preferences.getString("username", "")
                        Log.d("test3", savedSearch!!)
                        //if (savedSearch.isNullOrEmpty()) {
                            firebaseDatabase = FirebaseDatabase.getInstance()
                            val reference = firebaseDatabase.getReference("users")

                            // Check if this user is already in database
                            reference.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(databaseError: DatabaseError) {

                                }

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    dataSnapshot.children.forEach { data ->
                                        val username = data.key
                                        val toCompare = inputUsername.replace(".", "")
                                        Log.d("test3", "current value: $username and looking for: $toCompare")
                                        if ((username != null) && (username == toCompare)) {
                                            preferences.edit()
                                                    .putString("username", toCompare)
                                                    .apply()
                                            Log.d("test3", "found username in database: $username")
                                            Log.d("test3", "updated sp with key = ${data.key}")
                                        }
                                    }
                                }
                            })


                       /* val pushReference = reference.push()
                            pushReference.setValue(inputUsername)
                            val uniqueId = pushReference.key
                            preferences.edit()
                                .putString("username", uniqueId)
                                .apply()*/
                                //Log.d("test4", uniqueId!!)

                       // }

                        Toast.makeText(this@LoginActivity, "Logged in as $inputUsername", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, RestaurantActivity::class.java)
                        startActivity(intent)

                    } else {
                        val exception = task.exception
                        Toast.makeText(this@LoginActivity, "Failed: $exception", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
                applicationContext,
                "$welcome $displayName",
                Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}