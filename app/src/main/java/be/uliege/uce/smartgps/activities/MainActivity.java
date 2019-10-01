package be.uliege.uce.smartgps.activities;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.uliege.uce.smartgps.R;
import be.uliege.uce.smartgps.dataBase.SQLiteController;
import be.uliege.uce.smartgps.entities.Sensor;
import be.uliege.uce.smartgps.entities.User;
import be.uliege.uce.smartgps.service.DetectedActivitiesIntentService;
import be.uliege.uce.smartgps.service.DetectedActivitiesService;
import be.uliege.uce.smartgps.service.GoogleLocationService;
import be.uliege.uce.smartgps.service.LocationService;
import be.uliege.uce.smartgps.service.MainService;
import be.uliege.uce.smartgps.service.SensorService;
import be.uliege.uce.smartgps.utilities.Constants;
import be.uliege.uce.smartgps.utilities.DataSession;
import be.uliege.uce.smartgps.utilities.Utilidades;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    private TextView txtGyroscope;
    private TextView txtAccelerometer;
    private TextView txtNSatellites;
    private TextView txtAccuracy;
    private TextView txtVelocity;
    private TextView txtGpsInfo;
    private TextView txtActivity;
    private TextView txtAltitude;
    private TextView txtUserInfo;
    private TextView txtSync;
    private TextView txtVerify;
    private TextView txtTime;
    private FloatingActionButton btnUpdate;

    private BroadcastReceiver broadcastReceiverActivity;
    private BroadcastReceiver broadcastReceiverSensor;
    private BroadcastReceiver broadcastReceiverGps;
    private BroadcastReceiver broadcastReceiverLocation;
    private BroadcastReceiver broadcastReceiverGoogleLocation;

    private Sensor sensorObject;
    private Map<String, String> dataSync;

    private SQLiteController dbSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtGyroscope = findViewById(R.id.txtGyroscope);
        txtAccelerometer = findViewById(R.id.txtAccelerometer);
        txtNSatellites = findViewById(R.id.txtNSatellites);
        txtAccuracy = findViewById(R.id.txtAccuracy);
        txtVelocity = findViewById(R.id.txtVelocity);
        txtActivity = findViewById(R.id.txtActivity);
        txtGpsInfo = findViewById(R.id.txtGpsInfo);
        txtAltitude = findViewById(R.id.txtAltitude);
        txtUserInfo = findViewById(R.id.txtUserInfo);
        txtSync = findViewById(R.id.txtSync);
        txtVerify = findViewById(R.id.txtVerify);
        txtTime = findViewById(R.id.txtTime);
        btnUpdate = findViewById(R.id.btnUpdate);

        User user = new Gson().fromJson(DataSession.returnDataSession(getApplicationContext(), Constants.INFO_SESSION_KEY), User.class);
        txtUserInfo.setText(user.getNombres() + "\n" + user.getCorreoElectronico());
        if (user.getFcmToken().equals("1")){
            txtVerify.setText(getString(R.string.lblTxtVerifyOk));
        }else{
            txtVerify.setText(R.string.lblTxtVerifyNoOk);
        }

        dbSensor = new SQLiteController(getApplicationContext());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, getText(R.string.msgManualSync), Toast.LENGTH_SHORT).show();
                dataSync = dbSensor.getAllData();
                if (dataSync != null && dataSync.size()>0) {
                    List<Sensor> positions = new ArrayList<>();
                    for (Map.Entry<String, String> entry : dataSync.entrySet()) {
                        positions.add(new Gson().fromJson(entry.getValue(), Sensor.class));
                    }
                    Log.i("json", positions.size() + " - " + new Gson().toJson(positions));
                    User user = new Gson().fromJson(DataSession.returnDataSession(getApplicationContext(), Constants.INFO_SESSION_KEY), User.class);
                    Map<String, String> params = new HashMap<>();
                    params.put("type", "setInfoSensor");
                    params.put("dspId", String.valueOf(user.getDspId()));
                    params.put("sensorInfo", new Gson().toJson(positions));
                    Log.i("json2", String.valueOf(positions.size()));
                    manualDataSync(params);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.msgManualSyncEmpty), Toast.LENGTH_SHORT).show();
                }
            }
        });
        initGoogleAPIClient();
        checkPermissions();
        initApp();
    }

    public void manualDataSync(final Map<String, String> params) {
        RequestQueue queue;
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        queue = new RequestQueue(cache, network);
        // Start the queue
        queue.start();
        //RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_CONSUMMER,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG + ".onResponseMDS", response);
                        String palabra = "OK";
                        boolean resultado = response.contains(palabra);
                        if (response != null && resultado) {
                            for (Map.Entry<String, String> entry : dataSync.entrySet()) {
                                try {
                                    dbSensor.deleteData(Integer.parseInt(entry.getKey()));
                                } catch (Exception e) {
                                    Log.e(TAG + ".onResponseMDS", "Error al eliminar por id " + entry.getKey());
                                }
                            }
                            //final String url = Constants.URL_NOTIFICADOR_TELEGRAM + "?msj=" + (Build.MODEL + " --> Ha sincronizado " + dataSync.size() + " elementos manualmente " + new Timestamp(System.currentTimeMillis()) + ".").replace(" ", "%20");
                            //new Thread(new Runnable() {
                            //    public void run() {
                            //        new GetUrlContentTask().execute(url);
                            //    }
                            //}).start();
                            Toast.makeText(MainActivity.this, dataSync.size() +" "+ getString(R.string.msgManualSyncOk), Toast.LENGTH_LONG).show();
                            dataSync = null;
                            response = null;
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.msgManualSyncFail), Toast.LENGTH_SHORT).show();
                            dataSync = null;
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, getString(R.string.msgErrorServerResponse), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        queue.add(postRequest);
    }


    private void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Version 6 Marshmallow o superior
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                showSettingDialog();
            }
        } else
            showSettingDialog();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    private void showSettingDialog() {
        LocationRequest locRequestHighAccuracy = LocationRequest.create();
        locRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locRequestHighAccuracy.setInterval(30 * 1000);
        locRequestHighAccuracy.setFastestInterval(5 * 1000);

        LocationRequest locRequestBalancedPowerAccuracy = LocationRequest.create();
        locRequestBalancedPowerAccuracy.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locRequestBalancedPowerAccuracy);
        builder.addLocationRequest(locRequestHighAccuracy);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        //Task<LocationSettingsResponse> result1 = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e(TAG +".onActivityResultMA", "Settings Result OK");
                        break;
                    case RESULT_CANCELED:
                        Log.e(TAG +".onActivityResultMA", "Settings Result Cancel");
                        break;
                }
                break;
        }
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };

    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e(TAG + ".gpsLocationReceiver", "GPS is Enabled in your device");
                } else {
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    Log.e(TAG + ".gpsLocationReceiver", "GPS is Disabled in your device");
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.msgGPSPermissionNoGranted), Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }


    private void initApp() {
        sensorObject = new Sensor();

        txtGyroscope.setText(getString(R.string.lblTxtNoInformation));
        txtAccelerometer.setText(getString(R.string.lblTxtNoInformation));
        txtNSatellites.setText(getString(R.string.lblTxtNoInformation));
        txtAccuracy.setText(getString(R.string.lblTxtNoInformation));
        txtGpsInfo.setText(getString(R.string.lblTxtNoInformation));
        txtVelocity.setText(getString(R.string.lblTxtNoInformation));
        txtActivity.setText(getString(R.string.lblTxtNoInformation));
        txtAltitude.setText(getString(R.string.lblTxtNoInformation));

        broadcastReceiverSensor = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.SENSOR_ACTIVITY)) {
                    Sensor sensor = (Sensor) intent.getSerializableExtra(Constants.SENSOR_ACTIVITY);
                    if (sensor.getAclX() != null && !sensor.getAclX().equals("null")) {
                        sensorObject.setAclX(sensor.getAclX());
                        sensorObject.setAclY(sensor.getAclY());
                        sensorObject.setAclZ(sensor.getAclZ());
                    }
                    if (sensor.getGrsX() != null && !sensor.getGrsX().equals("null")) {
                        sensorObject.setGrsX(sensor.getGrsX());
                        sensorObject.setGrsY(sensor.getGrsY());
                        sensorObject.setGrsZ(sensor.getGrsZ());
                    }
                    txtAccelerometer.setText("X: " + sensorObject.getAclX() + "\nY: " + sensorObject.getAclY() + "\nZ: " + sensorObject.getAclZ());
                    txtGyroscope.setText("X: " + sensorObject.getGrsX() + "\nY: " + sensorObject.getGrsY() + "\nZ: " + sensorObject.getGrsZ());
                }
            }
        };

        broadcastReceiverGps = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.GPS_ACTIVITY)) {
                    Sensor sensor = (Sensor) intent.getSerializableExtra(Constants.GPS_ACTIVITY);
                    sensorObject.setAccuracy(sensor.getAccuracy());
                    sensorObject.setnSatellites(sensor.getnSatellites());
                    txtNSatellites.setText(sensorObject.getnSatellites().toString());
                    txtAccuracy.setText(sensorObject.getAccuracy().toString());
                }
            }
        };

        broadcastReceiverLocation = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.LOCATION_ACTIVITY)) {
                    Sensor sensor = (Sensor) intent.getSerializableExtra(Constants.LOCATION_ACTIVITY);
                    sensorObject.setVelocity(sensor.getVelocity());
                    sensorObject.setLongitude(sensor.getLongitude());
                    sensorObject.setLatitude(sensor.getLatitude());
                    sensorObject.setAltitude(sensor.getAltitude());

                    txtGpsInfo.setText(getString(R.string.lblTxtGpsInfoLon) + sensorObject.getLongitude() + "\n" +
                            getString(R.string.lblTxtGpsInfoLat) + sensorObject.getLatitude());
                    txtVelocity.setText(sensorObject.getVelocity().toString());
                    txtAltitude.setText(sensorObject.getAltitude().toString());

                    txtSync.setText(getString(R.string.lblTxtSync) + dbSensor.getAllData().size());
                    txtTime.setText(new Date().toString());
                }
            }
        };

        broadcastReceiverActivity = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.DETECTED_ACTIVITY)) {
                    Sensor sensor = (Sensor) intent.getSerializableExtra(Constants.DETECTED_ACTIVITY);
                    if (sensor != null && sensor.getActivity() != null) {
                        sensorObject.setActivity(sensor.getActivity());
                        txtActivity.setText(Utilidades.getActivityLabel(sensorObject.getActivity() != null ? sensorObject.getActivity() : DetectedActivity.UNKNOWN));
                    }
                }
            }
        };

        broadcastReceiverGoogleLocation = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.GOOGLE_LOCATION_ACTIVITY)) {
                    Sensor sensor = (Sensor) intent.getSerializableExtra(Constants.GOOGLE_LOCATION_ACTIVITY);
                    if (sensor != null && sensor.getLatitude() != null) {
                        if (sensorObject.getLatitude() == null) {
                            sensorObject.setLatitude(sensor.getLatitude());
                            txtSync.setText(getString(R.string.lblTxtSync) + dbSensor.getAllData().size());
                            txtTime.setText(new Date().toString());
                        }

                        if (sensorObject.getVelocity() == null) {
                            sensorObject.setVelocity(sensor.getVelocity());
                        }

                        if (sensorObject.getLongitude() == null) {
                            sensorObject.setLongitude(sensor.getLongitude());
                        }

                        sensorObject.setAltitude(sensor.getAltitude());

                        txtVelocity.setText(sensorObject.getVelocity() != null ? sensorObject.getVelocity().toString() : "");
                        txtGpsInfo.setText(getString(R.string.lblTxtGpsInfoLon) + sensorObject.getLongitude() + "\n" + getString(R.string.lblTxtGpsInfoLat) + sensorObject.getLatitude());

                    }
                }
            }
        };
        startTracking();
    }


    private void startTracking() {

        Intent intentActivitiesDetected = new Intent(MainActivity.this, DetectedActivitiesIntentService.class);
        startService(intentActivitiesDetected);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverActivity, new IntentFilter(Constants.DETECTED_ACTIVITY));
        Intent intentActivities = new Intent(MainActivity.this, DetectedActivitiesService.class);
        startService(intentActivities);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverSensor, new IntentFilter(Constants.SENSOR_ACTIVITY));
        Intent intentSensor = new Intent(MainActivity.this, SensorService.class);
        startService(intentSensor);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverGps, new IntentFilter(Constants.GPS_ACTIVITY));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverLocation, new IntentFilter(Constants.LOCATION_ACTIVITY));
        Intent intentLocation = new Intent(MainActivity.this, LocationService.class);
        startService(intentLocation);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverGoogleLocation, new IntentFilter(Constants.GOOGLE_LOCATION_ACTIVITY));
        Intent intentGoogleLocation = new Intent(MainActivity.this, GoogleLocationService.class);
        startService(intentGoogleLocation);

        Intent intentMain = new Intent(MainActivity.this, MainService.class);
        startService(intentMain);
    }


    private class GetUrlContentTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = "", line;
                while ((line = rd.readLine()) != null) {
                    content += line + "\n";
                }
                return content;

            } catch (MalformedURLException e) {
                Log.e(TAG, String.valueOf(e.getMessage()));
            } catch (ProtocolException e) {
                Log.e(TAG, String.valueOf(e.getMessage()));
            } catch (SocketTimeoutException e) {
                Log.e(TAG, String.valueOf(e.getMessage()));
            } catch (IOException e) {
                Log.e(TAG, String.valueOf(e.getMessage()));
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                //Log.e(TAG, result);
            } else {
                Log.e(TAG + " GetUrlContentTask", "Result is null");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        startTracking();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverActivity);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverSensor);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverGps);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverLocation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsLocationReceiver != null)
            unregisterReceiver(gpsLocationReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}