package com.example.activityrecognizationapi.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.SleepClassifyEvent
import com.google.android.gms.location.SleepSegmentEvent

class SleepServiceReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, intent: Intent?) {
        if(SleepSegmentEvent.hasEvents(intent)){
            val events = SleepSegmentEvent.extractEvents(intent)
            Log.d(TAG, "Logging SleepSegmentEvents")
            for (event in events) {
                val startSeconds: Long = event.startTimeMillis / 1000 % 60
                val endSeconds: Long = event.endTimeMillis / 1000 % 60
                Log.d(TAG, "$startSeconds to $endSeconds with status ${event.status}")
            }
        }else if (SleepClassifyEvent.hasEvents(intent)) {
            val events = SleepClassifyEvent.extractEvents(intent)
            Log.d(TAG, "Logging SleepClassifyEvents")
            for (event in events) {
                Log.d(TAG, "Confidence: ${event.confidence} - Light: ${event.light} - Motion: ${event.motion}")
            }
        }
    }

    companion object {
        private const val TAG = "SLEEP_RECEIVER"

        fun createPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, SleepServiceReceiver::class.java)

            return PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }
    }
}