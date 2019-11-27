package com.example.bboba


import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * A simple [Fragment] subclass.
 */
//https://code.luasoftware.com/tutorials/android/android-google-maps-load-supportmapfragment-in-alertdialog-dialogfragment/
//https://codelabs.developers.google.com/codelabs/location-places-android/index.html?index=..%2F..index#7
//https://mainia.tistory.com/736
//https://nittaku.tistory.com/67
//위 사이트 참고함
class LocationViewrDialog(private val detail_activity: DetailViewActivity, private val lat: Double=37.5999500, private val lng: Double=126.8642749) : DialogFragment() {
    lateinit var customView: View
    private val DEFAULT_ZOOM = 17f
    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    lateinit var selected_position: LatLng

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return customView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // StackOverflowError
        // customView = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        customView = activity!!.layoutInflater.inflate(R.layout.location_picker_dialog, null)

        val builder = AlertDialog.Builder(context!!)
            .setView(customView)
            .setNegativeButton("취소", null)

        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // if onCreateView didn't return view
        // java.lang.IllegalStateException: Fragment does not have a view
        mapFragment = childFragmentManager.findFragmentByTag("select_map_view") as SupportMapFragment?
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().replace(R.id.select_map_view, mapFragment!!, "select_map_view").commit()
        }

        mapFragment.let { mapFragment ->
            mapFragment!!.getMapAsync { map ->
                googleMap = map
                map.addMarker(MarkerOptions().position(LatLng(lat, lng)))
                map.setOnMapLoadedCallback {
                    val latLng = LatLng(lat, lng)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
                }
            }
        }
    }
}