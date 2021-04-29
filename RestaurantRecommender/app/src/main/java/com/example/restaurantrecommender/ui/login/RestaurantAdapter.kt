package com.example.restaurantrecommender.ui.login

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantrecommender.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import org.jetbrains.anko.find
import org.w3c.dom.Text
import android.graphics.*
import android.widget.ImageButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/*
* Following class taken from user Chandler's Kotlin translation of stevyhacker's answer
* to the following stackoverflow thread on making rounded corners with picasso:
* https://stackoverflow.com/questions/30704581/make-imageview-with-round-corner-using-picasso
 */
class RoundCornersTransform(private val radiusInPx: Float) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val bitmap = Bitmap.createBitmap(source.width, source.height, source.config)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        val shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        val rect = RectF(0.0f, 0.0f, source.width.toFloat(), source.height.toFloat())
        canvas.drawRoundRect(rect, radiusInPx, radiusInPx, paint)
        source.recycle()

        return bitmap
    }

    override fun key(): String {
        return "round_corners"
    }

}
class RestaurantAdapter(val sources: List<Restaurant>) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Need to render a new row -- inflate (load) the XML file and return a ViewHolder
        // Need to:
        // 1. Read in the XML file for the row type
        // 2. Use the new row to build a ViewHolder to return

        // Step 1
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView: View = layoutInflater.inflate(R.layout.row_restaurant, parent, false)

        // Step 2
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load data into a new row
        // The RecyclerView is ready to display a new (or recycled) row on the screen
        // for position indicated -- override the UI elements with the correct data
        val currentSource = sources[position]
        holder.name.text = currentSource.name
        holder.title.text = currentSource.title
        holder.rating.text = currentSource.rating.toString()
        holder.price.text = currentSource.price
        holder.transaction.text = currentSource.transaction
        var setChecked = false

        if (holder.name.text == "null") holder.name.visibility = View.GONE
        if (currentSource.iconUrl.isNotBlank()) {
            val image = Picasso.get()
                .load(currentSource.iconUrl)
                //.resize(0, 512)
                .centerCrop()
                .resize(341, 512)
                .onlyScaleDown()
                .transform(RoundCornersTransform(32.0f))
                .into(holder.icon)


                //Log.d("resize", "${image.width}")
        }

        // TODO: Fill this out later for favorites fragment
        // Set this particular restaurant card's star to checked if user has already favorited it
        // TODO: This works but takes O(n) time for every card loaded. Should be able to be done in O(1)
        firebaseDatabase = FirebaseDatabase.getInstance()
        val preferences = holder.favorite.context.getSharedPreferences("restaurantRecommender", Context.MODE_PRIVATE)
        val getUserId = preferences.getString("username", "")
        Log.d("test3", "username gotten in adapter: $getUserId")
        val reference = firebaseDatabase.getReference("users/$getUserId")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("favorite", "couldn't get users from database: ${databaseError.message}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val favorites = mutableListOf<String>()
                dataSnapshot.children.forEach { data ->
                    val favorite = data.getValue(String::class.java)
                    if (favorite != null) {
                        Log.d("favorite", favorite)
                        if (favorite == currentSource.name) {
                            setChecked = true
                            holder.favorite.setImageResource(R.drawable.ic_favorite_checked)
                        }
                    }
                }
            }
        })

        holder.favorite.setOnClickListener {
            setChecked = !setChecked
            //firebaseDatabase = FirebaseDatabase.getInstance()
            //val preferences = holder.favorite.context.getSharedPreferences("restaurantRecommender", Context.MODE_PRIVATE)
            //val getUserId = preferences.getString("username", "")
            //val reference = firebaseDatabase.getReference("users/$getUserId")
            // Add the favorite restaurant to the database for this user
            if (setChecked) {
                holder.favorite.setImageResource(R.drawable.ic_favorite_checked)
                Log.d("favorite", "${currentSource.businessID}")
                reference.child(currentSource.businessID).setValue(currentSource.name)

            }
            // Remove the restaurant from the database for this user
            else {
                holder.favorite.setImageResource(R.drawable.ic_favorite_unchecked)

                reference.child(currentSource.businessID).removeValue()
            }
        }

        holder.click.setOnClickListener {
            Log.d("test4",currentSource.description)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(currentSource.description)
            holder.name.context.startActivity(intent)
        }

        /*
        val test = currentSource.url
        holder.click.setOnClickListener() {
            Log.d("BUTTON", "Was URL passed: ${currentSource.iconUrl}")
            // TODO: come back to this

            val url = test
            if (url != "goToResults") {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                holder.url.getContext().startActivity(intent)
            }
            else {
                val intent = Intent(holder.url.getContext(), ResultsActivity::class.java)
                intent.putExtra("SOURCE", holder.username.text)
                intent.putExtra("TERM", currentSource.term)
                intent.putExtra("SOURCEID", currentSource.source)
                holder.url.getContext().startActivity(intent)
            }
        }*/
    }

    // Return number of (total) rows to render
    override fun getItemCount(): Int {
        return sources.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val title: TextView = itemView.findViewById(R.id.RestaurantTitle)
        val rating: TextView = itemView.findViewById(R.id.rating)
        val price: TextView = itemView.findViewById(R.id.cost)
        val click: ConstraintLayout = itemView.findViewById(R.id.card_view_layout)
        val icon: ImageView = itemView.findViewById(R.id.image)
        val transaction: TextView = itemView.findViewById(R.id.transaction)
        val favorite: ImageButton = itemView.findViewById(R.id.favorite)
    }

}