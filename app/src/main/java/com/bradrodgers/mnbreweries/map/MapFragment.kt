package com.bradrodgers.mnbreweries.map

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer

import com.bradrodgers.mnbreweries.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val viewModel: MapViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this,
            MapViewModel.Factory(activity.application)).get(MapViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.breweryMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        }

        viewModel.breweryInfo.observe(viewLifecycleOwner, Observer {info->

            for(value in info){
                val lat = value.geoPoint.latitude
                val lng = value.geoPoint.longitude

                val markerLocation = LatLng(lat, lng)
                val markerOptions = MarkerOptions()
                markerOptions.position(markerLocation).title(value.name).snippet(value.name)

                googleMap.addMarker(markerOptions)

            }

        })

        viewModel.currentLocation.observe(viewLifecycleOwner, Observer {location->

            val lat = location.latitude
            val lng = location.longitude

            val currentLocation = LatLng(lat, lng)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10.0f))


        })


    }

    override fun onMarkerClick(marker: Marker): Boolean {

        //Can add a dialog box to display information about the brewery

        return false
    }

}
