package com.example.restaurantrecommender.ui.login.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    private lateinit var button: Button
    private lateinit var price: RadioGroup
    private lateinit var atMost: RadioButton
    private lateinit var exactly: RadioButton
    private lateinit var atLeast: RadioButton
    private lateinit var any: RadioButton
    private lateinit var seekPrice: SeekBar
    private lateinit var priceView: LinearLayout
    private lateinit var seekDistance: SeekBar
    private lateinit var displayRange: TextView

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
        button = root.findViewById(R.id.button)
        price = root.findViewById(R.id.priceRadioGroup)
        atMost = root.findViewById(R.id.radioAtMost)
        exactly = root.findViewById(R.id.radioExactly)
        atLeast = root.findViewById(R.id.radioAtLeast)
        any = root.findViewById(R.id.radioAny)
        seekPrice = root.findViewById(R.id.seekPrice)
        priceView = root.findViewById(R.id.priceLayout)
        seekDistance = root.findViewById(R.id.seekDistance)
        displayRange = root.findViewById(R.id.displayRange)

        anyCuisine.setOnCheckedChangeListener { buttonView, _ ->
            if (buttonView.isChecked) chipGroup.visibility = View.GONE
            else                      chipGroup.visibility = View.VISIBLE
        }

        atMost.setOnClickListener {
            seekPrice.visibility = View.VISIBLE
            priceView.visibility = View.VISIBLE
        }
        exactly.setOnClickListener {
            seekPrice.visibility = View.VISIBLE
            priceView.visibility = View.VISIBLE
        }
        atLeast.setOnClickListener {
            seekPrice.visibility = View.VISIBLE
            priceView.visibility = View.VISIBLE
        }
        any.setOnClickListener {
            seekPrice.visibility = View.GONE
            priceView.visibility = View.GONE
        }

        /*
        * Update the displayed distance
        * Following snippet derived from a geeksforgeeks article on seekbars in Kotlin:
        * https://www.geeksforgeeks.org/seekbar-in-kotlin/
        */
        seekDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                // Live updates distance textView when sliding distance seekbar
                val getDistance = (seekDistance.progress / 3.33).toInt().toString()
                displayRange.text = getDistance
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })
        button.setOnClickListener {
            val prices = listOf(1,2,3,4)
            var selectedPrices = listOf<Int>()
            val selectedPrice = seekPrice.progress + 1

            // Get the prices the user wants to filter by
            when (price.checkedRadioButtonId) {
                R.id.radioAtMost -> {
                    selectedPrices = prices.subList(0, selectedPrice)
                    Log.d("filter", "prices selected: $selectedPrices")
                }
                R.id.radioExactly -> {
                    selectedPrices = prices.subList(selectedPrice-1, selectedPrice)
                    Log.d("filter", "price selected: exactly $selectedPrices")
                }
                R.id.radioAtLeast -> {
                    selectedPrices = prices.subList(selectedPrice-1, 4)
                    Log.d("filter", "price selected: $selectedPrices")
                }
                R.id.radioAny -> {
                    Log.d("filter", "price selected: $selectedPrices")
                }
                else -> {
                    Log.d("error", "An unexpected error occurred: a price radio button was not selected")
                }
            }

            // Get the range the user wants to filter by
            val selectedDistance = (seekDistance.progress / 3.33).toInt()
            Log.d("filter", "distance is $selectedDistance")
            //findNavController().navigate(R.id.action_navigation_home_to_blankFragment)
        }

        return root
    }
}