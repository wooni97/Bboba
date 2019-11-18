package com.example.bboba

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_list.list_recyclerview
import kotlinx.android.synthetic.main.fragment_map.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MapFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mapView: MapView
    private lateinit var lv : RecyclerView
    private lateinit var mapFmap: GoogleMap
    private var mLayout : SlidingUpPanelLayout? = null

    //Firebase 변수
    private val reqData = ArrayList<Prints_Request>()
    private var tempreqData = ArrayList<Prints_Request>()//슬라이드바
    private val database = FirebaseDatabase.getInstance()
    private val reqRef = database.getReference("PRINTS_REQUEST")
    private val dateRef = reqRef.child("date")
    lateinit var myFbPath: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    //onCreateView에서 View와 GoogleMap 초기화
    //findViewById를 호출해서 지도 객체 핸들 가져옴
    //getMapAsync를 사용해서 지도 콜백 등록
    //OnMapReadyCallback 인터페이스 구현->onMapReady()메서드 재정의
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout: View = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = layout.findViewById(R.id.map) as MapView
        mapView.getMapAsync(this)

        lv = layout.findViewById(R.id.list_recyclerview) as RecyclerView

        mLayout = layout.findViewById(R.id.sliding_layout) as SlidingUpPanelLayout

        mLayout!!.setFadeOnClickListener { mLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED }

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ReqCardAdapterInMap(tempreqData)
        }


    }


    //여기부터

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
    //여기까지

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    //액티비티가 처음 생성될 때 실행되는 함수
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView.onCreate(savedInstanceState)
    }

    override fun onMarkerClick(p0: Marker):Boolean {
        //마커를 클릭했을 때, 가까운 거리에 있는 마커에 대해서 슬라이드뷰에 보여준다
        tempreqData.clear()
        for(each_data in reqData) {
            //자신이 클릭한 마커를 가장 위에 표시함
            if(each_data.locationx.toDouble() == p0.position.latitude && each_data.locationy.toDouble() == p0.position.longitude) {
                tempreqData.add(0,each_data)
                continue
            }
            if(each_data.locationx.toDouble() > p0.position.latitude-0.0002 && each_data.locationx.toDouble() < p0.position.latitude+0.0002 &&
                each_data.locationy.toDouble() > p0.position.longitude-0.0002 && each_data.locationy.toDouble() < p0.position.longitude+0.0002) {
                tempreqData.add(each_data)
            }
        }
        list_recyclerview.layoutManager = LinearLayoutManager(activity)
        list_recyclerview.adapter = ReqCardAdapterInMap(tempreqData)
        return false
    }

    override fun onMapReady(googleMap: GoogleMap) { //?뺌
        mapFmap = googleMap
        //val PLACE1: LatLng = LatLng(37.601536, 126.865027) //과학관
        val PLACE2: LatLng = LatLng(37.600061, 126.864668) // 학생회관
        //val PLACE3: LatLng = LatLng(37.601341, 126.864493) // 기계관
        googleMap.setOnMarkerClickListener(this)
        //FIrebase 값 읽기, kakao 값 읽기
        UserManagement.getInstance().me(object: MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
                Log.d("example", "aaabb=실패")
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.d("example", "aaabb=세션 닫힘")
            }

            override fun onSuccess(result: MeV2Response) {
                val userEmail = result.kakaoAccount.email
                dateRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(eachUserData: DataSnapshot) {
                        reqData.clear()
                        for (eud in eachUserData.children) {
                            for (h in eud.children) {
                                if (h.child("email").value == userEmail) continue //자신이 올린 요청은 보여주지 않는다
                                if (h.child("is_selected").value == "1") continue //매칭된 글은 보여주지 않는다
                                reqData.add(
                                    0,
                                    Prints_Request(
                                        h.child("name").value as String,
                                        h.child("email").value as String,
                                        h.child("total_page").value as String,
                                        h.child("detail_request").value as String,
                                        h.child("date").value as String,
                                        h.child("time").value as String,
                                        h.child("locationx").value as String,
                                        h.child("locationy").value as String,
                                        h.child("location_name").value as String,
                                        h.child("per_page").value as String,
                                        h.child("print_fb").value as String,
                                        h.child("print_color").value as String,
                                        h.child("picture_location").value as String
                                    )
                                )
                                val place = LatLng(
                                    (h.child("locationx").value as String).toDouble(),
                                    (h.child("locationy").value as String).toDouble()
                                )
                                googleMap.addMarker(MarkerOptions().position(place))
                            }
                        }
                        list_recyclerview.apply {
                            //데이터 뽑은 후 출력
                            layoutManager = LinearLayoutManager(activity ?: return)
                            adapter = ReqCardAdapterInMap(tempreqData)
                        }

                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
                //데이터들은 reqData 배열 안에 들어있다
                //        googleMap.addMarker(MarkerOptions().position(PLACE1).title("7건").snippet("흑백 4건 / 컬러 3건"))
                //        googleMap.addMarker(MarkerOptions().position(PLACE2).title("3건").snippet("흑백 3건"))
                //        googleMap.addMarker(MarkerOptions().position(PLACE3).title("2건").snippet("흑백 1건 / 컬러 1건"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PLACE2, 17.0f))
            }
        })
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
