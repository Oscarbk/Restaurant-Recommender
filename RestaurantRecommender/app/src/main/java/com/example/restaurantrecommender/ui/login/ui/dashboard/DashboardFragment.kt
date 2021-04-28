package com.example.restaurantrecommender.ui.login.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurantrecommender.R
import com.example.restaurantrecommender.ui.login.Restaurant
import com.example.restaurantrecommender.ui.login.RestaurantAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.doAsync
import org.json.JSONObject

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var firebaseDatabase: FirebaseDatabase
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        recyclerView = root.findViewById(R.id.recyclerView)
/*
        val restaurants = getFavorites()
        val adapter = RestaurantAdapter(restaurants)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)*/
        getFavorites(recyclerView)

        return root
    }

    // Get the user's favorite restaurants from the database
    private fun getFavorites(recyclerView: RecyclerView): List<Restaurant> {
        firebaseDatabase = FirebaseDatabase.getInstance()
        val preferences = this.activity?.getSharedPreferences("restaurantRecommender", Context.MODE_PRIVATE)
        val getUserId = preferences?.getString("username", "")
        val reference = firebaseDatabase.getReference("users/$getUserId")
        val oAuthToken = resources.getString(R.string.yelpKey)
        val favorites = mutableListOf<Restaurant>()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("dashboard", "couldn't get users from database: ${databaseError.message}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                doAsync {
                dataSnapshot.children.forEach { data ->
                    val favorite = data.key

                    // Get business id
                    if (favorite != null) {
                        Log.d("dashboard", favorite)

                            // add the restaurant to the list
                            val request = Request.Builder()
                                .get()
                                .url("https://api.yelp.com/v3/businesses/$favorite")
                                .header("Authorization", "Bearer $oAuthToken")
                                .build()
                            val response: Response = okHttpClient.newCall(request).execute()
                            val responseBody: String? = response.body?.string()

                            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                                Log.d("dashboard", "got a good response")
                                val json = JSONObject(responseBody)
                                val categories = json.getJSONArray("categories")
                                val title1 = categories.getJSONObject(0).getString("title")
                                var title2 = ""
                                if (categories.length() > 1) {
                                    title2 = " ${resources.getString(R.string.bullet)} ${categories.getJSONObject(1).getString("title")}"
                                }
                                var price = ""
                                try {
                                    price = " ${resources.getString(R.string.bullet)} ${json.getString("price")}"
                                } catch (e: org.json.JSONException) {}

                                // TODO: Probably more efficient way to do this with regex
                                val transactions = json.getJSONArray("transactions")
                                    .toString()
                                    .replace(",", resources.getString(R.string.bullet))
                                    .replace("[", "")
                                    .replace("]", "")
                                    .replace("\"${resources.getString(R.string.bullet)}\"", " ${resources.getString(R.string.bullet)} ")
                                    .replace("\"", "")
                                    .replace("restaurant_", "")
                                val restaurant = Restaurant(
                                    name = json.getString("name"),
                                    title = "$title1$title2",
                                    rating = json.getDouble("rating"),
                                    price = price,
                                    description = "",
                                    address = json.getJSONObject("location").getString("address1"),
                                    menu = "",
                                    iconUrl = json.getString("image_url"),
                                    transaction = transactions,
                                    businessID = json.getString("id"),
                                )
                                Log.d("dashboard", "Added ${json.getString("name")} to favorites list")
                                favorites.add(restaurant)
                                Log.d("dashboard", "list so far: ${favorites.toString()}")
                            }

                            Log.d("dashboard", "test: $favorites")
                        }
                    }

                    activity?.runOnUiThread {
                        val adapter = RestaurantAdapter(favorites)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(activity)
                    }
                }
            }
        })
        Log.d("dashboard", favorites.toString())
        return favorites
    }

    /*
    fun getFakeRestaurants(): List<Restaurant> {
        return listOf(
            Restaurant(
                name = "McDonald's",
                title = "heart disease",
                rating = 2.5,
                price = "$",
                description = "since 1888",
                address = "literally everywhere",
                menu = "no menu",
                iconUrl = "null",
                transaction = "",
                businessID = "none",
            ),
        )
    }
    */
}