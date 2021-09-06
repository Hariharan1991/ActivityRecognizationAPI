package com.example.activityrecognizationapi.service

import android.app.IntentService
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.activityrecognizationapi.utils.Constants
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import java.util.*

@Suppress("DEPRECATION")
class ActivityDetectionIntentService : IntentService(ActivityDetectionIntentService::class.simpleName) {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        val result = ActivityRecognitionResult.extractResult(intent)
        val detectedActivities: ArrayList<*> = result.probableActivities as ArrayList<*>

        for (activity in detectedActivities) {
            broadcastActivity(activity!! as DetectedActivity)
        }
    }

    private fun broadcastActivity(activity: DetectedActivity) {
        val intent = Intent(Constants.BROADCAST_DETECTED_ACTIVITY)
        intent.putExtra("type", activity.type)
        intent.putExtra("confidence", activity.confidence)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}