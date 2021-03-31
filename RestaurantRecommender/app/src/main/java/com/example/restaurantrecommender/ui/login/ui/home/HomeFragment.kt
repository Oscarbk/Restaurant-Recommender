package com.example.restaurantrecommender.ui.login.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.restaurantrecommender.R
import com.google.android.material.chip.ChipGroup

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var anyCuisine: SwitchCompat
    private lateinit var chipGroup: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        anyCuisine = root.findViewById(R.id.anySwitch)
        chipGroup = root.findViewById(R.id.chipGroup)

        anyCuisine.setOnCheckedChangeListener { buttonView, _ ->
            if (buttonView.isChecked) chipGroup.visibility = View.GONE
            else                      chipGroup.visibility = View.VISIBLE
        }

        return root
    }
}