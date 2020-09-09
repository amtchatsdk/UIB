package com.unified.inbox.ui

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.unified.inbox.R
import com.unified.inbox.utils.hideKeyboardByView
import com.unified.inbox.utils.AppConstants
import java.util.*


class UIBMapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    View.OnClickListener, LocationListener {
    private lateinit var mView: View
    private lateinit var mapView: MapView
    private var lat: Double? = 0.0
    private var lang: Double? = 0.0
    private var map: GoogleMap? = null
    private var AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var tvSelectedLocation: TextView
    private lateinit var ivSearch: ImageView
    private lateinit var locationContainer: LinearLayout
    private var geoCoder: Geocoder? = null
    private var appId: String? = null
    private var botId: String? = null
    private var userId: String? = null
    private var isInIt: Boolean? = true
    private var locationManager: LocationManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = this.arguments
        appId = args?.getString(AppConstants.APP_ID)
        botId = args?.getString(AppConstants.BOT_ID)
        userId = args?.getString(AppConstants.USER_ID)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_place_picker, container, false)
        mapView = mView.findViewById(R.id.uib_map_view)
        ivSearch = mView.findViewById(R.id.iv_search)
        tvSelectedLocation = mView.findViewById(R.id.tv_selected_location)
        locationContainer = mView.findViewById(R.id.location_info_container)
        ivSearch.setOnClickListener(this)
        locationContainer.setOnClickListener(this)
        mapView.onCreate(savedInstanceState)
        geoCoder = Geocoder(activity, Locale.getDefault())
        MapsInitializer.initialize(activity)

        mapView.getMapAsync(this)
        locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return mView
    }

    override fun onMapReady(p0: GoogleMap?) {
        this.map = p0
        p0?.addMarker(
            MarkerOptions().position(LatLng(0.0, 0.0))
        )
        p0?.setOnMapClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(activity!!, permissions, 1001)
            return
        }
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            600000, 0f, this
        )
        mapView.onStart()

    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }


    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapClick(p0: LatLng?) {
        locationContainer.hideKeyboardByView()
        val markerOptions = MarkerOptions()
        markerOptions.position(p0!!)
        val address = geoCoder?.getFromLocation(p0.latitude, p0.longitude, 1)
        val name = address?.get(0)?.getAddressLine(0)
        markerOptions.title(name)
        map?.clear()
        map?.animateCamera(CameraUpdateFactory.newLatLng(p0))
        map?.addMarker(markerOptions)

        this.lat = p0.latitude
        this.lang = p0.longitude
        locationContainer.visibility = View.VISIBLE
        tvSelectedLocation.text = name
    }

    override fun onClick(v: View?) {

        val viewId = v?.id
        if (viewId == R.id.iv_search) {
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)


            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .build(activity!!)

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        } else if (viewId == R.id.location_info_container) {
            /*targetFragment!!.onActivityResult(
                targetRequestCode,
                RESULT_OK,
                Intent().putExtra(AppConstants.LAT, lat)
                    .putExtra(AppConstants.LANG, lang).putExtra(AppConstants.USER_ID, userId)
                    .putExtra(AppConstants.APP_ID, appId).putExtra(AppConstants.BOT_ID, botId)

            )*/
            UIBChatFragment.uibLocation?.setLatLang(lat = lat!!, lang = lang!!)
            activity!!.supportFragmentManager.popBackStackImmediate()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            locationContainer.hideKeyboardByView()
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                Log.i("TAG", "Place: " + place.name + ", " + place.id)
                onMapClick(place.latLng)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                //val status: Status = Autocomplete.getStatusFromIntent(data!!)
                //Log.i(TAG, status.getStatusMessage())
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    override fun onLocationChanged(p0: Location?) {
        if (isInIt!!) {
            isInIt = false
            onMapClick(p0 = LatLng(p0?.latitude!!, p0.longitude))
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }


}