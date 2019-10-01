package be.uliege.uce.smartgps.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import be.uliege.uce.smartgps.entities.Sensor;
import be.uliege.uce.smartgps.utilities.Constants;

public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = DetectedActivitiesIntentService.class.getSimpleName();

    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        Sensor sensor = new Sensor();
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        if (result != null) {
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
            if (detectedActivities != null && detectedActivities.size() > 0) {
                sensor.setActivity(detectedActivities.get(0).getType());
            }
        }
        broadcastActivity(sensor);
        //sendBroadcast(sensor);
    }

    private void broadcastActivity(Sensor sensor) {
        Intent intent = new Intent(Constants.DETECTED_ACTIVITY);
        intent.putExtra(Constants.DETECTED_ACTIVITY, sensor);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}