package be.uliege.uce.smartgps.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import be.uliege.uce.smartgps.R;
import be.uliege.uce.smartgps.entities.User;
import be.uliege.uce.smartgps.utilities.Constants;
import be.uliege.uce.smartgps.utilities.DataSession;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Version 5 o superior
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Class<?> classInit = null;
                if (DataSession.onSession(getApplicationContext(), Constants.INFO_SESSION_KEY)) {
                    classInit = MainActivity.class;
                    String userJson = DataSession.returnDataSession(getApplicationContext(), Constants.INFO_SESSION_KEY);
                    System.out.println(userJson);
                    User user = new Gson().fromJson(userJson, User.class);
                    System.out.println(user);
                    if(!user.getFcmToken().equals("1")){//Dispositivo Inactivo
                        Map<String, String> params = new HashMap<>();
                        params.put("type", "getDevice");
                        params.put("dspId", String.valueOf(user.getDspId()));
                        validateDeviceActivation(params, user);
                    }
                } else {
                    classInit = ViewActivity.class;
                }

                SystemClock.sleep(1000);
                Intent intent = new Intent(SplashActivity.this, classInit);
                startActivity(intent);
                finish();
            }
        }, Constants.SPLASH_TIME_IN_MILLISECONDS);
    }

    public void validateDeviceActivation(final Map<String, String> params, final User user) {
        RequestQueue queue;
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        queue = new RequestQueue(cache, network);
        // Start the queue
        queue.start();
        //RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_CONSUMMER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG + ".onResponseVDA", response);
                        try {
                            User userTmp = new Gson().fromJson(response, User.class);
                            Log.d(TAG + ".deviceResp", userTmp.toString());
                            if (userTmp != null && userTmp.getFcmToken().equals("1")) {
                                user.setFcmToken(userTmp.getFcmToken());
                                DataSession.saveDataSession(SplashActivity.this, Constants.INFO_SESSION_KEY, new Gson().toJson(user));
                                Toast.makeText(SplashActivity.this, getString(R.string.msgDspVerified), Toast.LENGTH_SHORT).show();
                                response= null;
                            }else{
                                Toast.makeText(SplashActivity.this, getString(R.string.msgDspUnverified), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SplashActivity.this, getString(R.string.msgErrorDspVerification), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SplashActivity.this, getString(R.string.msgErrorServerResponse), Toast.LENGTH_SHORT).show();
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
}