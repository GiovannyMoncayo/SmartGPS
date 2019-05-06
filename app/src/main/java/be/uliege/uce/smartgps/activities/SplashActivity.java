package be.uliege.uce.smartgps.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import be.uliege.uce.smartgps.R;
import be.uliege.uce.smartgps.entities.User;
import be.uliege.uce.smartgps.utilities.Constants;
import be.uliege.uce.smartgps.utilities.DataSession;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getName();
    private LayoutInflater inflater;
    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }


        new Handler().postDelayed(new Runnable() {
            public void run() {

                Class<?> classInit = null;

                if(DataSession.onSession(getApplicationContext(), Constants.INFO_SESSION_KEY)) {
                    classInit = MainActivity.class;
                    User user = new Gson().fromJson(DataSession.returnDataSession(getApplicationContext(), Constants.INFO_SESSION_KEY), User.class);

                    Map<String, String> params = new HashMap<>();
                    params.put("type","getDevice");
                    params.put("dspId",String.valueOf(user.getDspId()));

                    sendResponse(params, user);

                }else{
                    classInit = ViewActivity.class;
                }

                SystemClock.sleep(1000);
                Intent intent = new Intent(SplashActivity.this, classInit);
                startActivity(intent);
                finish();

            }
        }, Constants.DURACION_SPLASH);


    }

    public void sendResponse(final Map<String, String> params, final User user){

        RequestQueue queue = Volley.newRequestQueue(SplashActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.URL_CONSUMMER, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG+".onResponse", response);

                try {
                    User userSinc = new Gson().fromJson(response, User.class);

                    Log.d(TAG+".userResp", userSinc.toString());

                    if (userSinc != null) {
                        user.setFcmToken(userSinc.getFcmToken());
                        DataSession.saveDataSession(SplashActivity.this, Constants.INFO_SESSION_KEY, new Gson().toJson(user).toString());
                        User userFinal = new Gson().fromJson(DataSession.returnDataSession(getApplicationContext(), Constants.INFO_SESSION_KEY), User.class);
                        Log.d(TAG+".userFinal", userFinal.toString());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(SplashActivity.this, "¡Error al verificar dispositivo!", Toast.LENGTH_SHORT).show();
                }
            }
        },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SplashActivity.this,"¡Ops... Algo salió mal.\nIntenta más tarde!",Toast.LENGTH_SHORT).show();
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