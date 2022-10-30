package com.example.bboba

import android.os.Bundle
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

class ListFragment : Fragment() {
    private val reqData = ArrayList<Prints_Request>() //요청글들의 정보를 담는 배열
    private val database = FirebaseDatabase.getInstance()
    private val reqRef = database.getReference("PRINTS_REQUEST")
    private val dateRef = reqRef.child("date") //역시간순 정렬을 위해 날짜 데이터에서 데이터들을 읽는다

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        //카카오 api에서 정보 받아오기
        UserManagement.getInstance().me(object: MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
            }
            override fun onSessionClosed(errorResult: ErrorResult?) {
            }
            override fun onSuccess(result: MeV2Response) {
                val userEmail = result.kakaoAccount.email

                //파이어베이스에서 데이터 받아오기
                //파이어베이스 데이터를 받아오는 부분을 카카오api정보 받아오는 곳 속에 넣음
                //카카오api 받아오는 속도가 느려서 파이어베이스 데이터를 받는 부분에서 자신의 글을 필터링 할 때
                //정보를 받아오지 못함(널 값이 들어감)
                //따라서 카카오에서 정보를 받은 이후에 파이어베이스 데이터 받는 것을 진행하기 위해서 내부에 코드 작성
                dateRef.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(eachUserData: DataSnapshot) {
                        reqData.clear() //배열 초기화
                        for(eud in eachUserData.children) {//eud : 날짜 별 유저 데이터
                            for(h in eud.children) {//한 날짜에 대한 유저의 요청 정보
                                if(h.child("email").value==userEmail) continue //자신이 올린 요청은 보여주지 않는다
                                if(h.child("is_selected").value=="1") continue //매칭된 글은 보여주지 않는다
                                if(h.child("matcher").child("email").value == result.kakaoAccount.email) continue
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
                        list_recyclerview.apply {
                            layoutManager = LinearLayoutManager(activity?:return) //다른 곳에서 컨택스트를 사용할 때 이 곳의 컨택스트를 종료한다
                            adapter = ReqCardAdapter(reqData) //데이터를 어댑터에 넘겨준다
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {
                    }
                }) //dateRef.addValueEventListener 끝
            }
        }) //카카오 api 끝
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ReqCardAdapter(reqData)
        }
    }
}
