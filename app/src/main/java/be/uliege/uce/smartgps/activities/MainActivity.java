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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

    private TextView mGyro;
    private TextView mAcc;
    private TextView nsatelites;
    private TextView presicion;
    private TextView velocidad;
    private TextView posicion;
    private TextView actividad;
    private TextView infoUser;
    private FloatingActionButton btnUpdate;
    private TextView sincronizate;
    private TextView verification;

    private BroadcastReceiver broadcastReceiverActivity;
    private BroadcastReceiver broadcastReceiverSensor;
    private BroadcastReceiver broadcastReceiverGps;
    private BroadcastReceiver broadcastReceiverLocation;
    private BroadcastReceiver broadcastReceiverGoogleLocation;

    private Sensor sensorObject;
    private Map<String, String> dataSinc;

    private SQLiteController dbSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGyro = (TextView) findViewById(R.id.gyro);
        mAcc = (TextView) findViewById(R.id.accele);
        nsatelites = (TextView) findViewById(R.id.nsatelites);
        presicion = (TextView) findViewById(R.id.presicion);
        velocidad = (TextView) findViewById(R.id.velocidad);
        actividad = (TextView) findViewById(R.id.actividad);
        posicion = (TextView) findViewById(R.id.posicion);
        infoUser = (TextView) findViewById(R.id.txtUserInfo);
        sincronizate = (TextView) findViewById(R.id.sincronizate);
        verification = (TextView) findViewById(R.id.verification);
        btnUpdate = findViewById(R.id.btnUpdate);

        User user = new Gson().fromJson(DataSession.returnDataSession(getApplicationContext(), Constants.INFO_SESSION_KEY), User.class);
        infoUser.setText(user.getNombres()+"\n"+user.getCorreoElectronico());
        verification.setText(user.getFcmToken());

        dbSensor = new SQLiteController(getApplicationContext());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Espere un momento...",Toast.LENGTH_SHORT).show();
                dataSinc =  dbSensor.getAllData();
                List<Sensor> positions =  new ArrayList<>();
                for(Map.Entry<String, String> entry : dataSinc.entrySet()) {
                    positions.add(new Gson().fromJson(entry.getValue(), Sensor.class));
                }
                Log.i("json" , String.valueOf(positions.size())+" - "+new Gson().toJson(positions));
                User user = new Gson().fromJson(DataSession.returnDataSession(getApplicationContext(), Constants.INFO_SESSION_KEY), User.class);
                Map<String, String> params = new HashMap<>();
                params.put("type","setInfoSensor");
                params.put("dspId",String.valueOf(user.getDspId()));
                params.put("sensorInfo",new Gson().toJson(positions));
                Log.i("json2" , String.valueOf(positions.size()));
                sendResponse(params);
            }
        });

        initGoogleAPIClient();
        checkPermissions();
        initApp();
    }

    public void sendResponse(final Map<String, String> params){

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.URL_CONSUMMER, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG+".onResponse", response);

                String palabra = "OK";
                boolean resultado = response.contains(palabra);

                if(response != null && resultado){
                    if(dataSinc != null) {
                        for (Map.Entry<String, String> entry : dataSinc.entrySet()) {
                            try {
                                dbSensor.deleteData(Integer.parseInt(entry.getKey()));
                            } catch (Exception e) {
                                Log.e(TAG + ".onResponse", "Error el eliminar por id " + entry.getKey());
                            }
                        }

                        final String url = Constants.URL_NOTIFICATION+"?msj="+ (Build.MODEL+" --> Ha sincronizado "+dataSinc.size()+" elementos manualmente "+String.valueOf(new Timestamp(System.currentTimeMillis()))+"." ).replace(" ", "%20");
                        new Thread(new Runnable() {
                            public void run(){
                                new GetUrlContentTask().execute(url);
                            }
                        }).start();

                        dataSinc = null;
                        Toast.makeText(MainActivity.this, "Sincronizado", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    dataSinc = null;
                    Toast.makeText(MainActivity.this,"No Sincronizado",Toast.LENGTH_SHORT).show();
                }

            }
        },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"¡Ops... Algo salió mal.\nIntenta más tarde!",Toast.LENGTH_SHORT).show();
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
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            }else {
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

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
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
                        Log.e("Settings", "Result OK");
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
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
                    Log.e("About GPS", "GPS is Enabled in your device");
                } else {
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    Log.e("About GPS", "GPS is Disabled in your device");
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
                    Toast.makeText(MainActivity.this, "Permisos de ubicación denegados.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }



    private void initApp(){


        sensorObject = new Sensor();

        mGyro.setText("Sin Información");
        mAcc.setText("Sin Información");
        nsatelites.setText("Sin Información");
        presicion.setText("Sin Información");
        velocidad.setText("Sin Información");
        actividad.setText("Sin Información");
        posicion.setText("Sin Información");


        broadcastReceiverSensor = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.SENSOR_ACTIVITY)) {

                    Sensor sensor = (Sensor) intent.getSerializableExtra("sensorDates");

                    if(sensor.getAclX() != null && !sensor.getAclX().equals("null")) {
                        sensorObject.setAclX(sensor.getAclX());
                        sensorObject.setAclY(sensor.getAclY());
                        sensorObject.setAclZ(sensor.getAclZ());
                    }

                    if(sensor.getGrsX() != null && !sensor.getGrsX().equals("null")) {
                        sensorObject.setGrsX(sensor.getGrsX());
                        sensorObject.setGrsY(sensor.getGrsY());
                        sensorObject.setGrsZ(sensor.getGrsZ());
                    }

                    mAcc.setText("X: " + sensorObject.getAclX() + "\nY: " + sensorObject.getAclY() + "\nZ: " + sensorObject.getAclZ());
                    mGyro.setText("X: " + sensorObject.getGrsX() + "\nY: " + sensorObject.getGrsY() + "\nZ: " + sensorObject.getGrsZ());
                }
            }
        };

        broadcastReceiverGps = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.GPS_ACTIVITY)) {

                    Sensor sensor = (Sensor) intent.getSerializableExtra("gpsDates");

                    sensorObject.setPrecision(sensor.getPrecision());
                    sensorObject.setNumSatelites(sensor.getNumSatelites());

                    nsatelites.setText(sensorObject.getNumSatelites().toString());
                    presicion.setText(sensorObject.getPrecision().toString());
                }
            }
        };

        broadcastReceiverLocation = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.LOCATION_ACTIVITY)) {

                    Sensor sensor = (Sensor) intent.getSerializableExtra("locationDates");

                    sensorObject.setVelocidad(sensor.getVelocidad());
                    sensorObject.setLongitude(sensor.getLongitude());
                    sensorObject.setLatitude(sensor.getLatitude());

                    velocidad.setText(sensorObject.getVelocidad().toString());
                    posicion.setText("Longitud: "+sensorObject.getLongitude()+"\nLatitud: "+sensorObject.getLatitude());

                    sincronizate.setText("Elementos no sincronizados: "+String.valueOf(dbSensor.getAllData().size()));
                }
            }
        };

        broadcastReceiverActivity = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.DETECTED_ACTIVITY)) {

                    Sensor sensor = (Sensor) intent.getSerializableExtra("detectedActivities");

                    if(sensor != null && sensor.getActividad() != null){
                        sensorObject.setActividad(sensor.getActividad());
                        actividad.setText(Utilidades.getActivityLabel(sensorObject.getActividad() != null ? sensorObject.getActividad() : DetectedActivity.UNKNOWN));
                    }
                }
            }
        };

        broadcastReceiverGoogleLocation = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.GOOGLE_LOCATION_ACTIVITY)) {

                    Sensor sensor = (Sensor) intent.getSerializableExtra("googleLocationDate");
                    if(sensor != null && sensor.getLatitude()!= null) {
                        if(sensorObject.getLatitude() == null) {
                            sensorObject.setLatitude(sensor.getLatitude());
                            sincronizate.setText("Elementos no sincronizados: "+String.valueOf(dbSensor.getAllData().size()));
                        }

                        if(sensorObject.getVelocidad() == null) {
                            sensorObject.setVelocidad(sensor.getVelocidad());
                        }

                        if(sensorObject.getLongitude() == null) {
                            sensorObject.setLongitude(sensor.getLongitude());
                        }

                        sensorObject.setAltitude(sensor.getAltitude());

                        velocidad.setText(sensorObject.getVelocidad() != null ? sensorObject.getVelocidad().toString():"");
                        posicion.setText("Longitud: " + sensorObject.getLongitude() + "\nLatitud: " + sensorObject.getLatitude());

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
            } catch (SocketTimeoutException e){
                Log.e(TAG, String.valueOf(e.getMessage()));
            } catch (IOException e) {
                Log.e(TAG, String.valueOf(e.getMessage()));
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            if(result != null) {
                //Log.e(TAG, result);
            }else{
                Log.e(TAG+" GetUrlContentTask", "Result is null");
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
    public void onBackPressed(){
        super.onBackPressed();
    }

}