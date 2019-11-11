package com.example.bboba


import android.app.Dialog
import android.app.DownloadManager
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_request.*

/**
 * A simple [Fragment] subclass.
 */
//https://code.luasoftware.com/tutorials/android/android-google-maps-load-supportmapfragment-in-alertdialog-dialogfragment/
//https://codelabs.developers.google.com/codelabs/location-places-android/index.html?index=..%2F..index#7
//https://mainia.tistory.com/736
//https://nittaku.tistory.com/67
//위 사이트 참고함
class LocationPickerDialog(private val req_activity: RequestActivity, private val lat: Double=37.5999500, private val lng: Double=126.8642749) : DialogFragment() {
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
            .setPositiveButton("선택하기",
                object:DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        req_activity.locationx = selected_position.latitude.toString()
                        req_activity.locationy = selected_position.longitude.toString()
                    }
                })
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
                var markerOptions: MarkerOptions = MarkerOptions()//마커를 하나만 찍어야 하기 때문에 선언
                fun placeMarker(position: LatLng) {//마커 찍어주는 함수
                    //마커 디자인 바꾸기
                    //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_3g))
                    markerOptions.position(position)
                    map.clear() // 기존 마커 지우기
                    map.animateCamera(CameraUpdateFactory.newLatLng(position))
                    map.addMarker(markerOptions)
                    selected_position = position
                }
                map.setOnMapClickListener {
                    selected_position = it
                    placeMarker(it)
                    Log.d("example", "위치 : x : ${it.latitude}, y : ${it.longitude}")
                }
                map.setOnMapLoadedCallback {
                    val latLng = LatLng(lat, lng)
//                    googleMap!!.addMarker(MarkerOptions()
//                        .position(latLng)
//                    )
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
                }
            }
        }
    }
}