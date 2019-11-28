package com.example.bboba


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_request.*
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

//https://code.luasoftware.com/tutorials/android/android-google-maps-load-supportmapfragment-in-alertdialog-dialogfragment/
//https://codelabs.developers.google.com/codelabs/location-places-android/index.html?index=..%2F..index#7
//https://mainia.tistory.com/736
//https://nittaku.tistory.com/67
//위 사이트 참고함
//RequestActivity를 매개변수로 넘겨줘서 RequestActivity의 텍스트뷰 값을 변경할 수 있도록 함
class LocationPickerDialog(private val req_activity: RequestActivity, private val lat: Double=37.5999500, private val lng: Double=126.8642749) : DialogFragment() {
    lateinit var customView: View //뒤에서 초기화해줌
    private val DEFAULT_ZOOM = 17f
    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    lateinit var selected_position: LatLng
    lateinit var geocoder: Geocoder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return customView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        customView = activity!!.layoutInflater.inflate(R.layout.location_picker_dialog, null) //액티비티는 항상 존재하므로 !!붙임
        geocoder = Geocoder(context, Locale.KOREA) //받아오는 값을 한국어로 설정
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

        mapFragment =
            childFragmentManager.findFragmentByTag("select_map_view") as? SupportMapFragment
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction()
                .replace(R.id.select_map_view, mapFragment!!, "select_map_view").commit()
        }

        //mapFragment를 이어지는 블록의 인자로 넘기고, 블록의 결과값을 반환.
        mapFragment.let { mapFragment ->
            mapFragment!!.getMapAsync { map ->
                googleMap = map
                var markerOptions: MarkerOptions = MarkerOptions()//마커를 하나만 찍어야 하기 때문에 선언하고 이를 이용한다
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
/*
                    //주소 받아오기
                    var list = mutableListOf<Address>()
                    try {
                        list = geocoder.getFromLocation(it.latitude,it.longitude,1)//주소 가져오기

                    } catch (e: Exception) {
                        Log.d("example", "ddee주소 변환 에러")
                    }
                    if(list.size==0) {
                        Log.d("example", "ddee주소가 없음")
                    }
                    else {
                        Log.d("example", "ddee"+ list[0].toString())
                    }

 */
                    //api사용해서 웹에서 json형식으로 받아와서 이를 파싱하여 가까운 영업점(건물)의 이름을 받아온다
                    val strlink = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+it.latitude+"%2C"+it.longitude+"&rankby=distance&key=AIzaSyAwmGNBhCUZ017Y1cN5aPOozOTeOt7yTmY"
                    if(isNetworkAvailable()) { //메인쓰레드에서 불가하므로, 쓰레드를 만들어서 진행한다.(구글에서 이 api를 메인쓰레드에서 사용불가하게 했다)
                        val myRunnable = Conn(mHandler, URL(strlink))
                        val myThread = Thread(myRunnable)
                        myThread.start()
                    }
                }

                map.setOnMapLoadedCallback {
                    val latLng = LatLng(lat, lng)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
                }
            }
        }
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) { //만든 쓰레드에서 얻는 값을 받는 핸들러를 선언한다
        override fun handleMessage(inputMessage: Message) {
            try{
                if (inputMessage.what == 1) { //건물 선택
                    val locationText = inputMessage.obj.toString()
                    Toast.makeText(context, locationText+" 선택", Toast.LENGTH_SHORT).show()
                    this@LocationPickerDialog.req_activity.location_select.text=locationText
                }
                if(inputMessage.what == 2) {
                    Toast.makeText(context, "기준 건물이 없습니다. 다시 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            } catch(e: Exception) { //예외가 발생하면 장소명을 한국 항공대학교 로 설정
                Toast.makeText(context, "한국 항공대학교 선택", Toast.LENGTH_SHORT).show()
            }

        }
    }
    // 네트워크가 이용가능한 상태인지 확인하는 함수
    private fun isNetworkAvailable(): Boolean { val cm = requireActivity().getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager
        return cm.isActiveNetworkMetered
    }
    //create a worker with a Handler as parameter
    class Conn(mHand: Handler, val link: URL): Runnable {
        val myHandler = mHand
        var locationName = ""
        override fun run() {
            var content = StringBuilder()
            try {
                // declare URL to text file, create a connection to it and put into stream.
                val myUrl = link  // or URL to txt file
                val urlConnection = myUrl.openConnection() as HttpURLConnection
                val inputStream = urlConnection.inputStream

                // get text from stream, convert to string and send to main thread.
                val allText = inputStream.bufferedReader().use { it.readText() }
                content.append(allText)
                val str = content.toString()
                locationName = JSONObject(str).getJSONArray("results").getJSONObject(0).get("name").toString() //0번째 인덱스 정보(가장 가까운 건물)의 정보를 받아온다
                var msg: Message = myHandler.obtainMessage()
                msg.what = 1
                msg.obj = locationName
                myHandler.sendMessage(msg)
            } catch (e: Exception) {
                var msg: Message = myHandler.obtainMessage()
                msg.what = 2
                msg.arg1=2
                myHandler.sendMessage(msg)
                Log.d("Error", "qqqqq"+e.toString())
            }
        }
    }
}