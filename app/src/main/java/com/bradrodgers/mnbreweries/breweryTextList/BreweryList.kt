package com.bradrodgers.mnbreweries.breweryTextList

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bradrodgers.mnbreweries.R
import com.bradrodgers.mnbreweries.adapters.BreweryListAdapter
import com.bradrodgers.mnbreweries.domain.BreweryInfo
import timber.log.Timber

class BreweryList : Fragment() {


    private val viewModel: BreweryListViewModel by lazy {
        val activity = requireNotNull(this.activity)

        ViewModelProviders.of(this, BreweryListViewModel.Factory(activity.application))
            .get(BreweryListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.brewery_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val breweryListRecyclerView: RecyclerView = activity!!.findViewById(R.id.breweryListRecyclerView)
        val viewManager: RecyclerView.LayoutManager =
            LinearLayoutManager(activity!!.applicationContext)

        breweryListRecyclerView.layoutManager = viewManager
        val breweryListAdapter = BreweryListAdapter()
        breweryListRecyclerView.adapter = breweryListAdapter


        val breweryListTitle: TextView = activity!!.findViewById(R.id.breweryListTitle)
        var title = ""

        when(arguments?.getString("textProduct") as String){
            "full"->{
                title = "Full Brewery List"
                viewModel.breweryInfoList.observe(viewLifecycleOwner, Observer { info ->

                    val list = info.sortedBy { it.name }

                    breweryListAdapter.data = list
                })
            }
            "nearby"->{
                viewModel.breweryInfoList.observe(viewLifecycleOwner, Observer { info ->
                    title = "Nearby Breweries"
                    val mutableList: MutableList<BreweryInfo> = mutableListOf()
                    for(value in info){
                        if(value.distance < 25){
                            mutableList.add(value)
                        }
                    }

                    val list = mutableList.sortedBy { it.distance }

                    breweryListAdapter.data = list
                })
            }
            "history"->{

            }
        }

        breweryListTitle.text = title
    }

}
