package com.example.bboba


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.*
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
//RequestActivity를 매개변수로 받아와서 RequestActivity의 텍스트뷰 값을 변경할 수 있도록 함
class LocationPickerDialog(private val req_activity: RequestActivity, private val lat: Double=37.5999500, private val lng: Double=126.8642749) : DialogFragment() {
    lateinit var customView: View //뒤에서 초기화한다
    private val DEFAULT_ZOOM = 17f
    private var mapFragment: SupportMapFragment? = null //널처리를 뒤에서 해준다
    lateinit var googleMap: GoogleMap
    lateinit var selectedPosition: LatLng
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
                object:DialogInterface.OnClickListener { //선택하기를 누르면 요청하기 페이지의 위치 변수들을 갱신한다
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        req_activity.locationx = selectedPosition.latitude.toString()
                        req_activity.locationy = selectedPosition.longitude.toString()
                    }
                })
            .setNegativeButton("취소", null)
        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapFragment =
            childFragmentManager.findFragmentByTag("select_map_view") as SupportMapFragment?
        if (mapFragment == null) { //mapFragment가 널이면 새로 생성해준다
            mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction()
                .replace(R.id.select_map_view, mapFragment!!, "select_map_view").commit()
        }

        //mapFragment를 이어지는 블록의 인자로 넘기고, 블록의 결과값을 반환.
        mapFragment.let { mapFragment ->
            mapFragment!!.getMapAsync { map -> //여기서는 mapFragment가 널값을 가지지 않는다
                googleMap = map
                var markerOptions: MarkerOptions = MarkerOptions()//마커를 하나만 찍어야 하기 때문에 선언하고 이를 이용한다
                fun placeMarker(position: LatLng) {//마커 찍어주는 함수
                    markerOptions.position(position)
                    map.clear() // 기존 마커 지우기
                    map.animateCamera(CameraUpdateFactory.newLatLng(position)) //내가 찍은 위치로 시점 이동
                    map.addMarker(markerOptions)
                    selectedPosition = position
                }
                map.setOnMapClickListener {
                    selectedPosition = it
                    placeMarker(it) //맵을 클릭하면 클릭한 위치를 저장하고, 마커를 찍는다
/*
역지오코딩 : 좌표를 통해 주소를 받아오는 코드
                    //주소 받아오기
                    //현재 사용하지 않기 때문에 주석처리
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
                    //구글 플레이스의 api를 사용하여 데이터를 얻는 링크
                    val strlink = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+it.latitude+"%2C"+it.longitude+"&rankby=distance&key=AIzaSyAwmGNBhCUZ017Y1cN5aPOozOTeOt7yTmY"
                    if(isNetworkAvailable()) { //메인쓰레드에서 불가하므로, 쓰레드를 만들어서 진행한다.(구글에서 이 api(Google Place)를 메인쓰레드에서 사용불가하게 했다)
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

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) { //만든 쓰레드에서 얻은 값을 받는 핸들러를 선언한다
        override fun handleMessage(inputMessage: Message) {
            try{
                if (inputMessage.what == 1) { //건물 선택
                    val locationText = inputMessage.obj.toString()
                    Toast.makeText(context, locationText+" 선택", Toast.LENGTH_SHORT).show()
                    this@LocationPickerDialog.req_activity.location_select.text=locationText
                }
            } catch(e: Exception) { //예외가 발생하면 장소명을 한국 항공대학교 로 설정
                //값은 위치선택을 눌렀을 때 바꿔줌(이미 완료)
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
    //핸들러에게 api에서 받은 건물 이름을 전달해주는 객체
    class Conn(mHand: Handler, val link: URL): Runnable {
        val myHandler = mHand
        var locationName = ""
        override fun run() {
            var content = StringBuilder()
            val myUrl = link
            val urlConnection = myUrl.openConnection() as HttpURLConnection
            val inputStream = urlConnection.inputStream

            val allText = inputStream.bufferedReader().use { it.readText() }
            content.append(allText)
            val str = content.toString()
            locationName = JSONObject(str).getJSONArray("results").getJSONObject(0).get("name").toString() //0번째 인덱스 정보(가장 가까운 건물)의 정보를 받아온다
            var msg: Message = myHandler.obtainMessage()
            msg.what = 1
            msg.obj = locationName
            myHandler.sendMessage(msg)
        }
    }
}