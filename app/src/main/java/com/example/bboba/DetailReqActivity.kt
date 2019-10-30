package com.example.bboba

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail_req.*
import java.lang.Exception

class DetailReqActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_req)

        val request_data = intent.getParcelableExtra<Prints_Request>("request_data")
        profile_name.text = request_data.name
        profile_id.text = request_data.id
        edit_total.text = request_data.total_page
        edit_request.text = request_data.detail_request
        edit_date.text = request_data.date
        edit_time.text = request_data.time
        spinner_location.text = request_data.location_name
        if(request_data.print_fb == "true") {
            print_fb.isChecked = true
        }
        if(request_data.print_color == "true") {
            color_print.isChecked = true
        }
    }
}