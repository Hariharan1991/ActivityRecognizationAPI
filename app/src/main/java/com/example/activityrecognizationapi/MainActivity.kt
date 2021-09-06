package com.example.activityrecognizationapi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.activityrecognizationapi.databinding.ActivityMainBinding
import com.example.activityrecognizationapi.service.BackgroundDetectedActivitiesService
import com.example.activityrecognizationapi.utils.Constants
import com.google.android.gms.location.DetectedActivity
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    lateinit var broadcastReceiver: BroadcastReceiver
    lateinit var binding:ActivityMainBinding
    lateinit var sleepRequestManager: SleepRequestManager
    private var isSleep = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.executePendingBindings()

        sleepRequestManager = SleepRequestManager(this)

        requestActivityRecognitionPermission()

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                if (intent.action == Constants.BROADCAST_DETECTED_ACTIVITY) {
                    val type = intent.getIntExtra("type", -1)
                    val confidence = intent.getIntExtra("confidence", 0)
                    provideUserStateOutput(type, confidence)
                }
            }
        }


        binding.btnStartTracking.setOnClickListener {
            startTracking()
        }

        binding.btnStopTracking.setOnClickListener {
            stopTracking()
        }

        binding.btnSleep.setOnClickListener{
            if(!isSleep) {
                isSleep = true
                sleepRequestManager.subscribeToSleepUpdates()
            }else {
                isSleep = false
                sleepRequestManager.unsubscribeFromSleepUpdates()
            }
        }
    }

    private fun startTracking(){
        val intent = Intent(this, BackgroundDetectedActivitiesService::class.java)
        startService(intent)
    }

    private fun stopTracking(){
        val intent = Intent(this, BackgroundDetectedActivitiesService::class.java)
        stopService(intent)
    }

    private fun provideUserStateOutput(type: Int, confidence: Int){
        var label = getString(R.string.activity_unknown)
        var icon = R.drawable.ic_still

        when (type) {
            DetectedActivity.IN_VEHICLE -> {
                label = getString(R.string.activity_in_vehicle)
                icon = R.drawable.ic_driving
            }
            DetectedActivity.ON_BICYCLE -> {
                label = getString(R.string.activity_on_bicycle)
                icon = R.drawable.ic_on_bicycle
            }
            DetectedActivity.ON_FOOT -> {
                label = getString(R.string.activity_on_foot)
                icon = R.drawable.ic_walking
            }
            DetectedActivity.RUNNING -> {
                label = getString(R.string.activity_running)
                icon = R.drawable.ic_running
            }
            DetectedActivity.STILL -> {
                label = getString(R.string.activity_still)
            }
            DetectedActivity.TILTING -> {
                label = getString(R.string.activity_tilting)
                icon = R.drawable.ic_tilting
            }
            DetectedActivity.WALKING -> {
                label = getString(R.string.activity_walking)
                icon = R.drawable.ic_walking
            }
            DetectedActivity.UNKNOWN -> {
                label = getString(R.string.activity_unknown)
            }
        }

        Log.e("ActivityDetection", "User activity: $label, Confidence: $confidence")

        if(confidence > Constants.CONFIDENCE){
            binding.txtActivity.text = label
            binding.txtConfidence.text ="Confidence : $confidence"
            binding.imgActivity.setImageResource(icon)
        }
    }

    private fun requestActivityRecognitionPermission(){
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), 101)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            101 -> {
                if (grantResults.isNotEmpty()) {
                    for (i in grantResults.indices) {
                        val permission = permissions[i]
                        if (android.Manifest.permission.ACTIVITY_RECOGNITION.equals(permission,
                                ignoreCase = true)) {
                            if (grantResults[i] === PackageManager.PERMISSION_GRANTED) {
                                // you now have permission
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
            IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY))
    }

    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}