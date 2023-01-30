package com.example.my3rdappdesign

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.my3rdappdesign.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    //default value of status download
    private var downloadStatus: String = "Failed"
    var positionUrl = ""
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private var notificationId = 0


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //to setup the toolbar
        setSupportActionBar(binding.toolbarMain)

        //to sense about the finished of download
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


        //when any item from radioGroup is selected
        binding.contentMainId.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                //when the user choose radio Button of the glide
                R.id.glideRadioButton -> {
                    Toast.makeText(this, "Glide Is Selected", Toast.LENGTH_LONG).show()
                    binding.contentMainId.customButton.setOnClickListener {
                        //checking on the Internet connection
                        val connectManager =
                            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val capabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            connectManager.getNetworkCapabilities(connectManager.activeNetwork)
                        } else {
                            TODO("VERSION.SDK_INT < M")
                        }

                        if (capabilities != null) {
                            binding.contentMainId.customButton.stateOfTheButton =
                                ButtonState.Loading
                            //passing URL throw a fun download
                            download(URLGlide)
                            //positionUrl variable to determine which URL is used to use it in  the broadcast receiver
                            positionUrl = URLGlide
                        } else {
                            Toast.makeText(this, "Your Network Is Offline", Toast.LENGTH_LONG)
                                .show()
                        }

                    }
                }

                //when the user choose radio Button of the appLoad
                R.id.appLoadRadioButton -> {
                    Toast.makeText(this, "Appload Is Selected", Toast.LENGTH_LONG).show()
                    binding.contentMainId.customButton.setOnClickListener {

                        //checking on the Internet connection
                        val connectManager =
                            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val capabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            connectManager.getNetworkCapabilities(connectManager.activeNetwork)
                        } else {
                            TODO("VERSION.SDK_INT < M")
                        }

                        if (capabilities != null) {
                            binding.contentMainId.customButton.stateOfTheButton =
                                ButtonState.Loading
                            download(URLAppLoad)
                            //positionUrl variable to determine which URL is used to use it in  the broadcast receiver
                            positionUrl = URLAppLoad
                        } else {
                            Toast.makeText(this, "Your Network Is Offline", Toast.LENGTH_LONG)
                                .show()
                        }

                    }
                }

                //when the user choose radio Button of the retrofit
                R.id.retrofitRadioButton -> {
                    Toast.makeText(this, "Retrofit Is Selected", Toast.LENGTH_LONG).show()
                    binding.contentMainId.customButton.setOnClickListener {

                        //checking on the Internet connection
                        val connectManager =
                            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val capabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            connectManager.getNetworkCapabilities(connectManager.activeNetwork)
                        } else {
                            TODO("VERSION.SDK_INT < M")
                        }

                        if (capabilities != null) {
                            binding.contentMainId.customButton.stateOfTheButton =
                                ButtonState.Loading
                            download(URLRetrofit)
                            //positionUrl variable to determine which URL is used to use it in  the broadcast receiver
                            positionUrl = URLRetrofit
                        } else {
                            Toast.makeText(this, "Your Network Is Offline", Toast.LENGTH_LONG)
                                .show()
                        }

                    }
                }
            }
        }

    }


    //To create a Notification when the download is finished , the receiver is sense about the download is finished or not
    private val receiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                downloadStatus = "Success"
                binding.contentMainId.customButton.stateOfTheButton = ButtonState.Completed
                var y: String
                if (positionUrl == URLGlide) {
                    Toast.makeText(context, "Glide Is Downloaded", Toast.LENGTH_LONG).show()
                    y = downloadStatus
                    createNotification(
                        NOTIFICATION_TITLE_GLIDE,
                        NOTIFICATION_DESCRIPTION_GLIDE,
                        NOTIFICATION_TITLE_GLIDE,
                        y
                    )
                }
                if (positionUrl == URLAppLoad) {
                    Toast.makeText(context, "AppLoad Is Downloaded", Toast.LENGTH_LONG).show()
                    y = downloadStatus
                    createNotification(
                        NOTIFICATION_TITLE_APPLOAD,
                        NOTIFICATION_DESCRIPTION_APPLOAD,
                        NOTIFICATION_TITLE_APPLOAD,
                        y
                    )
                }
                if (positionUrl == URLRetrofit) {
                    Toast.makeText(context, "Retrofit Is Downloaded", Toast.LENGTH_LONG).show()
                    y = downloadStatus
                    createNotification(
                        NOTIFICATION_TITLE_RETROFIT,
                        NOTIFICATION_DESCRIPTION_RETROFIT, NOTIFICATION_TITLE_RETROFIT, y
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }


    //creating Notification
    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNotification(title: String, description: String, x: String, y: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_Name,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.YELLOW
                enableLights(true)
                enableVibration(true)

            }
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        //create the intent to move from notification to detail screen
        val intent = Intent(this, DetailActivity::class.java)

        //use putExtras to sending data when press on the button at notification to move to detail screen
        intent.putExtras(
            withExtras(
                x, y
            )
        )
        Log.d("amr", "onCreate: $x + $y")

        @RequiresApi(Build.VERSION_CODES.S)
        pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)


        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_assistant_black_24dp, "Press", pendingIntent)
            .build()

        val notificationM = NotificationManagerCompat.from(this)

        notificationM.notify(notificationId, notification)
    }


    companion object {
        private const val URLGlide =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val URLAppLoad =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URLRetrofit =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_Name = "channelName"
        private const val NOTIFICATION_TITLE_GLIDE =
            "GLIDE_DOWNLOAD, Image Loading Library By BumpTech"
        private const val NOTIFICATION_DESCRIPTION_GLIDE = "The Data From The Glide Is Downloaded"
        private const val NOTIFICATION_TITLE_APPLOAD =
            "APPLOAD_DOWNLOAD, Current Repository By Udacity"
        private const val NOTIFICATION_DESCRIPTION_APPLOAD =
            "The Data From The Appload Is Downloaded"
        private const val NOTIFICATION_TITLE_RETROFIT =
            "RETROFIT_DOWNLOAD, Type-Safe HTTP Client For Android And JAVA By Square, Inc"
        private const val NOTIFICATION_DESCRIPTION_RETROFIT =
            "The Data From The Retrofit Is Downloaded"


        fun withExtras(title: String, message: String): Bundle {
            return Bundle().apply {
                putString("Title", title)
                putString("Status", message)
            }
        }

    }
}