package be.uliege.uce.smartgps.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import be.uliege.uce.smartgps.utilities.Constants;

public class DetectedActivitiesService extends Service {

    private static final String TAG = DetectedActivitiesService.class.getSimpleName();

    private Intent mIntentService;
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;

    IBinder mBinder = new DetectedActivitiesService.LocalBinder();

    public class LocalBinder extends Binder {
        public DetectedActivitiesService getServerInstance() {
            return DetectedActivitiesService.this;
        }
    }

    public DetectedActivitiesService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mIntentService = new Intent(this, DetectedActivitiesIntentService.class);
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesButtonHandler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void requestActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                mPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.i( TAG,  "Starting detected activity.");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i( TAG,  "Requesting activity updates failed to start.");
            }
        });

    }

    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                mPendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.i( TAG,  "Removed activity updates successfully!");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i( TAG,  "Failed to remove activity updates!");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //removeActivityUpdatesButtonHandler();
    }
}