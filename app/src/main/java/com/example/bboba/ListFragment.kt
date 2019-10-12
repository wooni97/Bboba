package com.example.bboba


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.material_cardview.*

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {
    private val reqData = ArrayList<RequestList>()
    private val database = FirebaseDatabase.getInstance()
    private val reqRef = database.getReference("prints_request")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        reqRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                reqData.clear()
                for(h in p0.children) {
                    reqData.add(0,RequestList(h.child("user_name").value as String,
                        h.child("total_page").value as String,
                    h.child("user_tel").value as String,
                    h.child("detail_request").value as String))
                }
                Log.d("example", "value=complete")
                list_recyclerview.apply { //데이터 뽑은 후 출력
                    layoutManager = LinearLayoutManager(activity)
                    adapter = Rec_CardAdapter(reqData)
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
            adapter = Rec_CardAdapter(reqData)
        }
    }

    companion object {
        fun newInstance(): ListFragment = ListFragment()
    }
}
