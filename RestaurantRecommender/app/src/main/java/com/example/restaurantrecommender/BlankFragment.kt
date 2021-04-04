package com.example.restaurantrecommender

import android.R.attr.defaultValue
import android.R.attr.key
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantrecommender.ui.login.Restaurant
import com.example.restaurantrecommender.ui.login.RestaurantAdapter
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.doAsync
import org.json.JSONObject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "apiCall"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var apiCall: String? = null
    private var param2: String? = null

    val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            apiCall = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.d("results", apiCall!!)
        /*val bundle = this.arguments
        val apiCall = bundle!!.getString("apiCall", "received no api call")
        Log.d("results", apiCall)*/

    }

    private fun retrieveRestaurants(): List<Restaurant>{
        val oAuthToken = resources.getString(R.string.yelpKey)
        val searchLocation = "D.C."
        val radius = "30mi"



        val request = Request.Builder()
            .get()
            .url(apiCall!!)
            .header("Authorization", "Bearer $oAuthToken")
            .build()
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()
        val restaurants = mutableListOf<Restaurant>()

        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
            Log.d("result", "got a good response")
            val json = JSONObject(responseBody)
            val businesses = json.getJSONArray("businesses")

            for (i in 0 until businesses.length()) {
                val curr = businesses.getJSONObject(i)
                val name = curr.getString("name")
                val ImageUrl = curr.getString("image_url")
                val isClosed = curr.getString("is_closed")
                val url = curr.getString("url")
                val address = curr.getJSONObject("location").getString("address1")
                val categories = curr.getJSONArray("categories")
                val title1 = categories.getJSONObject(0).getString("title")

                var title2 = ""
                if (categories.length() > 1) {
                    title2 = " ${resources.getString(R.string.bullet)} ${categories.getJSONObject(1).getString("title")}"
                }
                val rating = curr.getDouble("rating")

                var price = ""
                try {
                    price = " ${resources.getString(R.string.bullet)} ${curr.getString("price")}"
                } catch (e: org.json.JSONException) {

                }

                val image = curr.getString("image_url")
                // TODO: Probably more efficient way to do this with regex
                val transactions = curr.getJSONArray("transactions")
                    .toString()
                    .replace(",", resources.getString(R.string.bullet))
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"${resources.getString(R.string.bullet)}\"", " ${resources.getString(R.string.bullet)} ")
                    .replace("\"", "")


                val restaurant = Restaurant(
                        name = name,
                        title = "$title1$title2",
                        rating = 3.0,
                        price = price,
                        description = "",
                        address = address,
                        menu = "",
                        iconUrl = image,
                        transaction = transactions,
                )
                restaurants.add(restaurant)
            }
        }
        return restaurants
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_blank, container, false)
        //val root = inflater.inflate(R.layout.fragment_home, container, false)

        loadingBar = root.findViewById(R.id.progressBar)
        recyclerView = root.findViewById(R.id.recyclerView)
        doAsync {
            val restaurants = retrieveRestaurants()

            activity?.runOnUiThread {
                val adapter = RestaurantAdapter(restaurants)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(activity)
                loadingBar.visibility = View.GONE
            }
        }
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}