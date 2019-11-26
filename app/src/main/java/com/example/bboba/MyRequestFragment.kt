package com.example.bboba

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_my_giving.*
import kotlinx.android.synthetic.main.fragment_my_request.*

/**
 * A simple [Fragment] subclass.
 */
class MyRequestFragment : Fragment() {
    private val reqData = ArrayList<Prints_Request>()
    private val database = FirebaseDatabase.getInstance()
    private val reqRef = database.getReference("PRINTS_REQUEST")
    private val dateRef = reqRef.child("date")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        //카카오 api에서 정보 받아오기
        UserManagement.getInstance().me(object: MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
                Log.d("example", "aaabb=실패")
            }
            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.d("example", "aaabb=세션 닫힘")
            }
            override fun onSuccess(result: MeV2Response) {
                val userEmail = result.kakaoAccount.email

                //파이어베이스에서 데이터 받아오기
                //파이어베이스 데이터를 받아오는 부분을 카카오api정보 받아오는 곳 속에 넣음
                //카카오api 받아오는 속도가 느려서 파이어베이스 데이터를 받는 부분에서 자신의 글을 필터링 할 때
                //정보를 받아오지 못함(널 값이 들어감)
                //따라서 카카오에서 정보를 받은 이후에 파이어베이스 데이터 받는 것을 진행하기 위해서 이렇게 함
                dateRef.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(eachUserData: DataSnapshot) {
                        reqData.clear()
                        for(eud in eachUserData.children) {//eud : 날짜 별 유저 데이터
                            for(h in eud.children) {//한 날짜 대한 유저의 요청 정보
                                if(h.child("email").value==userEmail) {
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
                                }
                            }
                        }
                        list_myrequest.apply { //데이터 뽑은 후 출력
                            layoutManager = LinearLayoutManager(activity?:return)
                            adapter = ReqCardAdapter(reqData)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_myrequest.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ReqCardAdapter(reqData)
        }
    }
}
