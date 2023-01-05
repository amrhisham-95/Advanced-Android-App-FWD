package com.example.my3rdappdesign

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.my3rdappdesign.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailBinding

    private var notificationTitle: String? = null
    private var notificationMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarDetail)


        //getting the data that sending from notification and Loading the title & message
        val extras = intent.extras
        extras?.let {
            notificationTitle = it.getString("Title")

            notificationMessage = it.getString("Status")
        }

        //dispalying the title & message on the datail screen
        binding.contentDetailId.apply {
            TextTitle.text=notificationTitle
            TextStatus.text=notificationMessage
        }

        //To return to mainScreen when pressing on the return buttons
        binding.contentDetailId.buttonReturn.setOnClickListener {
            finish()
        }

        }


    //passing DATA (TITLE AND STATUS)
    companion object {
        fun withExtras(title: String, message: String): Bundle {
            return Bundle().apply {
                putString("Title", title)
                putString("Status", message)
            }
        }
    }

}
