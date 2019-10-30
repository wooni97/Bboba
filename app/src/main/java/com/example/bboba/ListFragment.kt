package com.example.bboba


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_list.*

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {
    private val reqData = ArrayList<Prints_Request>()
    private val database = FirebaseDatabase.getInstance()
    private val reqRef = database.getReference("PRINTS_REQUEST")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        reqRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                reqData.clear()
                for(h in p0.children) {
                    reqData.add(0,
                        Prints_Request(h.child("name").value as String, h.child("id").value as String,
                            h.child("total_page").value as String, h.child("detail_request").value as String,
                            h.child("date").value as String, h.child("time").value as String,
                            h.child("locationx").value as String, h.child("locationy").value as String,
                            h.child("location_name").value as String, h.child("per_page").value as String,
                            h.child("print_fb").value as String, h.child("print_color").value as String,
                            h.child("picture_location").value as String))
                }
                list_recyclerview.apply { //데이터 뽑은 후 출력
                    layoutManager = LinearLayoutManager(activity?:return)
                    adapter = ReqCardAdapter(reqData)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ReqCardAdapter(reqData)
        }
    }

    companion object {
        fun newInstance(): ListFragment = ListFragment()
    }
}
