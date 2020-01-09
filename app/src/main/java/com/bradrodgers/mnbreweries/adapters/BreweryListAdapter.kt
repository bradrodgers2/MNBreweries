package com.bradrodgers.mnbreweries.adapters

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bradrodgers.mnbreweries.R
import com.bradrodgers.mnbreweries.database.BreweryInfoDB
import com.bradrodgers.mnbreweries.database.getDatabase
import com.bradrodgers.mnbreweries.domain.BreweryInfo
import com.bradrodgers.mnbreweries.repository.Repository

class BreweryListAdapter: RecyclerView.Adapter<BreweryListAdapter.ViewHolder>() {

    var data = listOf<BreweryInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.brewery_list_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.breweryListTitle.text = item.name

        val foodCider = if(item.cider && item.food){
            "Serves food and cider"
        }else if(item.cider){
            "Serves cider, but no food"
        }else if(item.food){
            "Serves food, but no cider"
        }else{
            "Serves only beer"
        }
        holder.breweryListFoodCider.text = foodCider

        if(item.distance >= 0.0) {
            val distanceString = item.distance.toString() + " miles away"
            holder.breweryListDistance.text = distanceString
        }else{
            holder.breweryListDistance.visibility = View.GONE
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val breweryListTitle: TextView = itemView.findViewById(R.id.breweryListTitle)
        val breweryListFoodCider: TextView = itemView.findViewById(R.id.breweryListFoodCider)
        val breweryListDistance: TextView = itemView.findViewById(R.id.breweryListDistance)
    }

}