package com.example.restaurantrecommender.ui.login.ui.home

import android.R.attr.key
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.restaurantrecommender.R
import com.google.android.material.chip.Chip
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
                val getDistance = (seekDistance.progress / 4).toString()
                displayRange.text = getDistance
            }

            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })
        button.setOnClickListener {
            val prices = listOf(1, 2, 3, 4)
            var selectedPrices = listOf<Int>()
            val selectedPrice = seekPrice.progress + 1

            // Get the prices the user wants to filter by
            when (price.checkedRadioButtonId) {
                R.id.radioAtMost -> {
                    selectedPrices = prices.subList(0, selectedPrice)
                    Log.d("filter", "prices selected: $selectedPrices")
                }
                R.id.radioExactly -> {
                    selectedPrices = prices.subList(selectedPrice - 1, selectedPrice)
                    Log.d("filter", "price selected: exactly $selectedPrices")
                }
                R.id.radioAtLeast -> {
                    selectedPrices = prices.subList(selectedPrice - 1, 4)
                    Log.d("filter", "price selected: $selectedPrices")
                }
                R.id.radioAny -> {
                    Log.d("filter", "price selected: $selectedPrices")
                }
                else -> {
                    Log.d("error", "An unexpected error occurred: a price radio button was not selected")
                }
            }
            val inputPrices = selectedPrices.toString()
                .replace("[", "")
                .replace("]", "")
            // TODO:
            /*
            * if the user selects prices to be [1, 2, 3, 4], i.e. any
            * the api will filter out restaurants with no price attribute
            * change this so that price is not included in the api call for this case
             */

            // Get the range the user wants to filter by
            var selectedDistance: Int = (seekDistance.progress / 4) * 1609
            if (selectedDistance > 40000) selectedDistance = 40000
            Log.d("filter", "distance is $selectedDistance")

            // Get the cuisines the user wants to filter by
            val checkedChips = arrayListOf<String>()
            val ids = chipGroup.checkedChipIds
            for (id in ids) {
                val chip: Chip = chipGroup.findViewById(id)
                checkedChips.add("${chip.text}")
            }
            val cuisines = checkedChips.toString()
                .replace("[", "")
                .replace("]", "")
                .toLowerCase()

            val userLocation = "D.C."
            val apiCall = "https://api.yelp.com/v3/businesses/search?location=$userLocation&radius=$selectedDistance&food=restaurants&categories=$cuisines&price=$inputPrices"
            val bundle = bundleOf("apiCall" to apiCall)

            findNavController().navigate(R.id.action_navigation_home_to_blankFragment, bundle)
        }

        return root
    }
}