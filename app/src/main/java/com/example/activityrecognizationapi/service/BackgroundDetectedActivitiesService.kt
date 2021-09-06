package com.example.activityrecognizationapi.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import com.example.activityrecognizationapi.utils.Constants
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.tasks.Task

@Suppress("DEPRECATION", "UNREACHABLE_CODE")
class BackgroundDetectedActivitiesService:Service() {

    lateinit var intentService: Intent
    lateinit var activityRecognitionClient: ActivityRecognitionClient
    lateinit var mPendingIntent:PendingIntent

    inner class MyBinder : Binder() {
        fun getService() : BackgroundDetectedActivitiesService? {
            return this@BackgroundDetectedActivitiesService
        }
    }

    override fun onCreate() {
        super.onCreate()

        activityRecognitionClient = ActivityRecognitionClient(this)
        intentService = Intent(this, ActivityDetectionIntentService::class.java)
        mPendingIntent = PendingIntent.getService(
            this,
            1,
            intentService,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        requestActivityUpdatesButtonHandler()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder = MyBinder()

    private fun requestActivityUpdatesButtonHandler() {
        val task: Task<Void> = activityRecognitionClient.requestActivityUpdates(
            Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
            mPendingIntent)
        task.addOnSuccessListener {
            Toast.makeText(
                applicationContext,
                "Successfully requested activity updates",
                Toast.LENGTH_SHORT
            )
                .show()
        }
        task.addOnFailureListener {
            Toast.makeText(
                applicationContext,
                "Requesting activity updates failed to start",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun removeActivityUpdatesButtonHandler() {
        val task: Task<Void> = activityRecognitionClient.removeActivityUpdates(
            mPendingIntent)

        task.addOnSuccessListener {
            Toast.makeText(
                applicationContext,
                "Removed activity updates successfully!",
                Toast.LENGTH_SHORT
            )
                .show()
        }
        task.addOnFailureListener {
            Toast.makeText(
                applicationContext, "Failed to remove activity updates!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        removeActivityUpdatesButtonHandler()
    }
}