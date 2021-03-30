package com.example.restaurantrecommender.ui.login.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurantrecommender.R
import com.example.restaurantrecommender.ui.login.Restaurant
import com.example.restaurantrecommender.ui.login.RestaurantAdapter
import androidx.recyclerview.widget.RecyclerView

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var recyclerView: RecyclerView

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

        val restaurants = getFakeRestaurants()
        val adapter = RestaurantAdapter(restaurants)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)


        return root
    }


    fun getFakeRestaurants(): List<Restaurant> {
        return listOf(
            Restaurant(
                name = "McDonald's",
                description = "since 1888",
                address = "literally everywhere",
                menu = "no menu",
                iconUrl = "null",
            ),
        )
    }
}