package com.example.activityrecognizationapi

import android.content.Context
import android.widget.Toast
import com.example.activityrecognizationapi.receiver.SleepServiceReceiver
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest

class SleepRequestManager(private val context: Context) {

    fun subscribeToSleepUpdates(){
        ActivityRecognition.getClient(context)
            .requestSleepSegmentUpdates(
                SleepServiceReceiver.createPendingIntent(context),
            SleepSegmentRequest.getDefaultSleepSegmentRequest())

        Toast.makeText(context,"Start to Make Sleep Updates",Toast.LENGTH_LONG).show()
    }

    fun unsubscribeFromSleepUpdates() {
        ActivityRecognition.getClient(context)
            .removeSleepSegmentUpdates(SleepServiceReceiver.createPendingIntent(context))
        Toast.makeText(context,"Stopped Sleep Updates",Toast.LENGTH_LONG).show()
    }
}