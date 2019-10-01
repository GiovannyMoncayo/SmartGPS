package be.uliege.uce.smartgps.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import be.uliege.uce.smartgps.entities.Sensor;
import be.uliege.uce.smartgps.utilities.Constants;


public class GoogleLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = GoogleLocationService.class.getSimpleName();;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    public GoogleLocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand");

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connection OK!!! ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {

        }else{
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended!!! " + i + " cause");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed!!! " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {

        }else{
            Sensor sensor = new Sensor();
            sensor.setLatitude(location.getLatitude());
            sensor.setLongitude(location.getLongitude());
            sensor.setAltitude(location.getAltitude());
            sensor.setVelocity(location.getSpeed());

            broadcastGoogleLocation(sensor);

            Log.e(TAG, sensor.toString());
        }

    }

    private synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Build Google API");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }



    private void broadcastGoogleLocation(be.uliege.uce.smartgps.entities.Sensor sensor) {
        Intent intent = new Intent(Constants.GOOGLE_LOCATION_ACTIVITY);
        intent.putExtra(Constants.GOOGLE_LOCATION_ACTIVITY, sensor);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}