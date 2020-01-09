package com.bradrodgers.mnbreweries.homepage

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.bradrodgers.mnbreweries.R
import com.bradrodgers.mnbreweries.breweryTextList.BreweryList
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class Homepage : Fragment(), EasyPermissions.PermissionCallbacks {

    val LOCATION_PERMISSION = 123

    private val viewModel: HomepageViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, HomepageViewModel.Factory(activity.application))
            .get(HomepageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.homepage_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getLocation(activity!!)

        val fullBreweryListBtn = activity!!.findViewById<Button>(R.id.fullBreweryListBtn)
        fullBreweryListBtn.setOnClickListener{

            val pair: Pair<String, String> = "textProduct" to "full"
            val bundle = bundleOf(pair)

            findNavController().navigate(R.id.action_homepage_to_breweryList, bundle)
        }

        val nearbyBreweryListBtn = activity!!.findViewById<Button>(R.id.nearbyBreweryListBtn)
        nearbyBreweryListBtn.setOnClickListener {

            val pair: Pair<String, String> = "textProduct" to "nearby"
            val bundle = bundleOf(pair)

            findNavController().navigate(R.id.action_homepage_to_breweryList, bundle)
        }


    }

    //Permission Getters
    private fun locationAllowed(): Boolean{
        return EasyPermissions.hasPermissions(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    //Grab location if allowed
    private fun getLocation(context: Context): Location?{

        val lm =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
            Timber.e("exception: $ex")
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
            Timber.e("exception: $ex")
        }

        if (gpsEnabled || networkEnabled) {

            if (locationAllowed()) {

                if(activity != null) {
                    if(!activity!!.isFinishing && !activity!!.isDestroyed) {
                        Timber.i("grabbing new location")
                        viewModel.getLocation(activity!!)
                    }
                }

            } else {

                EasyPermissions.requestPermissions(
                    this,
                    "This app would like to use your current location to find nearby breweries",
                    LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

            }
        }else{
            //TODO: Add dialog
            //Location services not turned on.  Open dialog and offer to bring user to settings page
        }

        return null

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(activity != null) {
            if (!activity!!.isFinishing && !activity!!.isDestroyed) {
                Toast.makeText(activity!!, "Location permissions have been denied. " +
                        "You can change this in your application permission settings.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if(requestCode == LOCATION_PERMISSION){
            Timber.i("permission granted")
            if(perms.contains(Manifest.permission.ACCESS_FINE_LOCATION)){
                Timber.i("calling getLocation()")
                getLocation(context!!)
            }
        }
    }

}
