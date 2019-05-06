package be.uliege.uce.smartgps.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import be.uliege.uce.smartgps.utilities.Constants;


public class SensorService extends Service implements SensorEventListener {

    private static final String TAG = SensorService.class.getSimpleName();

    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorGyr;

    IBinder mBinder = new SensorService.LocalBinder();

    public class LocalBinder extends Binder {
        public SensorService getServerInstance() {
            return SensorService.this;
        }
    }

    public SensorService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGyr = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mSensorAcc, SensorManager.SENSOR_DELAY_UI, new Handler());
        mSensorManager.registerListener(this, mSensorGyr, SensorManager.SENSOR_DELAY_UI, new Handler());

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        be.uliege.uce.smartgps.entities.Sensor sensor = new be.uliege.uce.smartgps.entities.Sensor();

        /*
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {

            /*if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                sensor.setAclX(null);
                sensor.setAclY(null);
                sensor.setAclZ(null);

            } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                sensor.setGrsX(null);
                sensor.setGrsY(null);
                sensor.setGrsZ(null);

            return;
        }

        }*/

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensor.setAclX(event.values[0]);
            sensor.setAclY(event.values[1]);
            sensor.setAclZ(event.values[2]);
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            sensor.setGrsX(event.values[0]);
            sensor.setGrsY(event.values[1]);
            sensor.setGrsZ(event.values[2]);
        }

        broadcastSensor(sensor);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void broadcastSensor(be.uliege.uce.smartgps.entities.Sensor sensor) {
        Intent intent = new Intent(Constants.SENSOR_ACTIVITY);
        intent.putExtra("sensorDates", sensor);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


    }


}