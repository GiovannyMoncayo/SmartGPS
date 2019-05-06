package be.uliege.uce.smartgps.service;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;

import be.uliege.uce.smartgps.activities.MainActivity;
import be.uliege.uce.smartgps.entities.Sensor;
import be.uliege.uce.smartgps.utilities.Constants;
import be.uliege.uce.smartgps.utilities.NotificationUtils;

public class LocationService extends Service implements GpsStatus.Listener, LocationListener {

    private static final String TAG = LocationService.class.getSimpleName();

    private GpsStatus gpsStatus;
    private LocationManager lm;

    private int nsat, msat;
    private String providerSelect;


    IBinder mBinder = new LocationService.LocalBinder();


    public class LocalBinder extends Binder {
        public LocationService getServerInstance() {
            return LocationService.this;
        }
    }

    public LocationService() {

    }

    @Override
    public void onCreate() {

        Log.i(TAG, "onCreate");

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /*if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            providerSelect = LocationManager.GPS_PROVIDER;
            Log.e("asasasasasa","sasasas");

        }else if(locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            providerSelect = LocationManager.NETWORK_PROVIDER;
        }else{
            providerSelect = LocationManager.NETWORK_PROVIDER;
        }
        */
        providerSelect = LocationManager.GPS_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }else {
            lm.requestLocationUpdates(providerSelect, 0,2 , (LocationListener) this);
            lm.addGpsStatusListener(this);
        }

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onGpsStatusChanged(int event) {

        nsat = 0;
        msat = 0;

        Sensor sensor = new Sensor();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        gpsStatus = lm.getGpsStatus(gpsStatus);

        for (GpsSatellite satelitesItem : gpsStatus.getSatellites()) {
            if (satelitesItem.usedInFix()) {
                nsat++;
            }
            msat++;
        }
        sensor.setNumSatelites(msat);

        List<String> listaProviders = lm.getAllProviders();
        double precisionPromedio = 0.0;
        for (String item : listaProviders) {
            LocationProvider provider = lm.getProvider(item);
            precisionPromedio = precisionPromedio + provider.getAccuracy();
        }
        precisionPromedio = precisionPromedio / listaProviders.size();
        sensor.setPrecision(precisionPromedio);

        broadcastActivity(sensor);

    }

    @Override
    public void onLocationChanged(Location location) {

        Sensor sensor = new Sensor();
        if(location != null) {
            sensor.setLatitude(location.getLatitude());
            sensor.setLongitude(location.getLongitude());
            sensor.setVelocidad(location.getSpeed());
            broadcastActivityLocation(sensor);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "onProviderDisabled: " + provider);
        NotificationUtils mNotificationUtils = new NotificationUtils(this);
        Notification.Builder nb = mNotificationUtils. getAndroidChannelNotification("¡Ha deshabilitado el proveedor "+provider+".!", "Active el proveedor "+provider+" para continuar con la recolección de información.", MainActivity.class);
        mNotificationUtils.getManager().notify(101, nb.build());
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {
        //Log.i(TAG, "onStatusChanged: " + provider);
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    private void broadcastActivityLocation(Sensor sensor) {
        Intent intent = new Intent(Constants.LOCATION_ACTIVITY);
        intent.putExtra("locationDates", sensor);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastActivity(Sensor sensor) {
        Intent intent = new Intent(Constants.GPS_ACTIVITY);
        intent.putExtra("gpsDates", sensor);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}