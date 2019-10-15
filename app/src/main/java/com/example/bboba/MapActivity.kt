package com.example.bboba


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private var mLayout : SlidingUpPanelLayout? = null
    //private var mapFragment : MapFragment? = null

    private lateinit var mMap: GoogleMap


    private lateinit var firstMarker : Marker
    private lateinit var secondMarker : Marker
    private lateinit var thirdMarker : Marker
    private lateinit var fourthMarker : Marker
    private lateinit var fifthMarker : Marker


    val locationManager : LocationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager}




    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val first_building = LatLng(37.601532, 126.865107)
        firstMarker = mMap.addMarker(MarkerOptions().position(first_building).title("과학관"))

        val second_building = LatLng(37.601253, 126.864479)
        secondMarker = mMap.addMarker(MarkerOptions().position(second_building).title("기계관"))

        val third_building = LatLng(37.600031, 126.864632)
        thirdMarker = mMap.addMarker(MarkerOptions().position(third_building).title("학생회관"))

        val fourth_building = LatLng(37.600149, 126.866046)
        fourthMarker = mMap.addMarker(MarkerOptions().position(fourth_building).title("강의동"))

        val fifth_building = LatLng(37.598067, 126.866433)
        fifthMarker = mMap.addMarker(MarkerOptions().position(fifth_building).title("기숙사"))

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10.0f, this)
        }else{
            if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                requestPermissions(Array<String>(1){android.Manifest.permission.ACCESS_FINE_LOCATION}, 1)
            }

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10.0f, this)
                }
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        mMap.isMyLocationEnabled =true
        if(location != null){

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(LatLng(location.latitude, location.longitude), 14.0f, 0.0f, location.bearing)))
        }
    }

    override fun onProviderDisabled(p0: String?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        setSupportActionBar(findViewById<View>(R.id.main_toolbar) as Toolbar)

        val lv = findViewById<View>(R.id.list) as ListView
        lv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> Toast.makeText(this@MapActivity, "onItemClick", Toast.LENGTH_SHORT).show() }

        val your_array_list = Arrays.asList(
            "This",
            "Is",
            "An",
            "Example",
            "ListView"

        )

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        val arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            your_array_list)

        lv.adapter = arrayAdapter

        mLayout = findViewById<View>(R.id.sliding_layout) as SlidingUpPanelLayout
        mLayout!!.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                Log.i(TAG, "onPanelSlide, offset $slideOffset")
            }

            override fun onPanelStateChanged(panel: View, previousState: PanelState, newState: PanelState) {
                Log.i(TAG, "onPanelStateChanged $newState")
            }
        })
        mLayout!!.setFadeOnClickListener { mLayout!!.panelState = PanelState.COLLAPSED }

        val t = findViewById<View>(R.id.name) as TextView
        t.text = Html.fromHtml(getString(R.string.hello))
        val f = findViewById<View>(R.id.follow) as Button
        f.text = Html.fromHtml(getString(R.string.follow))
        f.movementMethod = LinkMovementMethod.getInstance()
        f.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("http://www.twitter.com/umanoapp")
            startActivity(i)
        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.demo, menu)
        val item = menu.findItem(R.id.action_toggle)
        if (mLayout != null) {
            if (mLayout!!.panelState == PanelState.HIDDEN) {
                item.setTitle(R.string.action_show)
            } else {
                item.setTitle(R.string.action_hide)
            }
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_toggle -> {
                if (mLayout != null) {
                    if (mLayout!!.panelState != PanelState.HIDDEN) {
                        mLayout!!.panelState = PanelState.HIDDEN
                        item.setTitle(R.string.action_show)
                    } else {
                        mLayout!!.panelState = PanelState.COLLAPSED
                        item.setTitle(R.string.action_hide)
                    }
                }
                return true
            }
            R.id.action_anchor -> {
                if (mLayout != null) {
                    if (mLayout!!.anchorPoint == 1.0f) {
                        mLayout!!.anchorPoint = 0.7f
                        mLayout!!.panelState = PanelState.ANCHORED
                        item.setTitle(R.string.action_anchor_disable)
                    } else {
                        mLayout!!.anchorPoint = 1.0f
                        mLayout!!.panelState = PanelState.COLLAPSED
                        item.setTitle(R.string.action_anchor_enable)
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (mLayout != null && (mLayout!!.panelState == PanelState.EXPANDED || mLayout!!.panelState == PanelState.ANCHORED)) {
            mLayout!!.panelState = PanelState.COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private val TAG = "DemoActivity"
    }


}


