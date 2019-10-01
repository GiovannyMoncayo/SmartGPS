package be.uliege.uce.smartgps.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import be.uliege.uce.smartgps.R;
import be.uliege.uce.smartgps.entities.Options;
import be.uliege.uce.smartgps.entities.Questions;
import be.uliege.uce.smartgps.entities.User;
import be.uliege.uce.smartgps.utilities.Constants;
import be.uliege.uce.smartgps.utilities.CustomViewPager;
import be.uliege.uce.smartgps.utilities.DataSession;
import be.uliege.uce.smartgps.utilities.Utilidades;

public class ViewActivity extends AppCompatActivity {

    public static final String TAG = ViewActivity.class.getSimpleName();

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static GoogleApiClient mGoogleApiClient;
    private static CustomViewPager mViewPager;
    public static List<Questions> questionsList;
    public static List<Questions> answersList;

    public static int answerCheck;

    public static Context mContext;
    public static Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        mContext = getApplicationContext();
        mActivity = ViewActivity.this;
        answerCheck = 0;
        Map<String, String> params = new HashMap<>();
        params.put("type", "questionsWithoutSession");
        loadQuestions(params);
        initGoogleAPIClient();
        checkPermissions();
    }

    private void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(ViewActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Version 6 Marshmallow o superior
            if (ContextCompat.checkSelfPermission(ViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                showSettingDialog();
            }
        } else
            showSettingDialog();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(ViewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(ViewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
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
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(ViewActivity.this, REQUEST_CHECK_SETTINGS);
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
                        Log.e(TAG + ".onActivityResultVA", "Settings Result OK");
                        break;
                    case RESULT_CANCELED:
                        Log.e(TAG + ".onActivityResultVA", "Settings Result Cancel");
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
                    Toast.makeText(ViewActivity.this, getString(R.string.msgGPSPermissionNoGranted), Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsLocationReceiver != null)
            unregisterReceiver(gpsLocationReceiver);
    }


    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            final ImageView imgView = (ImageView) rootView.findViewById(R.id.imgView);
            final TextView txtDescription = (TextView) rootView.findViewById(R.id.txtDescription);
            final EditText edTxValor = (EditText) rootView.findViewById(R.id.edTxValor);
            Button btnPrev = (Button) rootView.findViewById(R.id.btnPrev);
            Button btnNext = (Button) rootView.findViewById(R.id.btnNext);
            final Button btnEnd;

            RadioButton rdbtnValor1 = (RadioButton) rootView.findViewById(R.id.rdbtnValor1);
            rdbtnValor1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRadioButtonClicked(v);
                }
            });
            RadioButton rdbtnValor2 = (RadioButton) rootView.findViewById(R.id.rdbtnValor2);
            rdbtnValor2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRadioButtonClicked(v);
                }
            });
            RadioButton rdbtnValor3 = (RadioButton) rootView.findViewById(R.id.rdbtnValor3);
            rdbtnValor3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRadioButtonClicked(v);
                }
            });
            RadioButton rdbtnValor4 = (RadioButton) rootView.findViewById(R.id.rdbtnValor4);
            rdbtnValor4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRadioButtonClicked(v);
                }
            });
            RadioButton rdbtnValor5 = (RadioButton) rootView.findViewById(R.id.rdbtnValor5);
            rdbtnValor5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRadioButtonClicked(v);
                }
            });
            final List<RadioButton> options = new ArrayList<>();
            options.add(rdbtnValor1);
            options.add(rdbtnValor2);
            options.add(rdbtnValor3);
            options.add(rdbtnValor4);
            options.add(rdbtnValor5);
            if (questionsList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getOpcOptions() != null && questionsList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getOpcOptions().size() > 0) {
                for (int i = 0; i < questionsList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getOpcOptions().size(); i++) {
                    options.get(i).setText(questionsList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getOpcOptions().get(i).getOpcDescription());
                }
            }

            switch (questionsList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getType()) {
                case 0:
                    edTxValor.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    for (RadioButton item : options) {
                        if (item.getText() != null && item.getText().toString().trim().replace(" ", "").length() > 0) {
                            item.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case 2:
                    edTxValor.setVisibility(View.GONE);
                    for (RadioButton item : options) {
                        if (item.getText() != null && item.getText().toString().trim().replace(" ", "").length() > 0) {
                            item.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
            }

            if (questionsList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getImgView() != null && questionsList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getImgView().length() > 0) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final Bitmap imagenUrl = Utilidades.getBitmapFromURL(questionsList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getImgView());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imgView.setImageBitmap(imagenUrl);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }

            if (questionsList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getTxtDescription() != null
                    && questionsList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getTxtDescription().length() > 0) {
                txtDescription.setText(questionsList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getTxtDescription());
            } else {
                txtDescription.setText(getString(R.string.msgErrorQuestionTxt));
            }

            btnPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnswersReturn(edTxValor, options, getArguments().getInt(ARG_SECTION_NUMBER) - 2);
                }
            });

            btnEnd = (Button) rootView.findViewById(R.id.btnEnd);
            btnEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnEnd.setVisibility(View.GONE);
                    if (setAnswers(edTxValor, getArguments().getInt(ARG_SECTION_NUMBER) - 1, options)) {
                        Map<String, String> params = new HashMap<>();
                        params.put("type", "setUser");
                        params.put("dspSerie", Build.MODEL);
                        params.put("dspDescription", Build.DEVICE);
                        params.put("dspSdkInt", String.valueOf(Build.VERSION.SDK_INT));
                        params.put("answers", new Gson().toJson(answersList));
                        insertData(params, btnEnd);
                    }
                }
            });


            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnswers(edTxValor, getArguments().getInt(ARG_SECTION_NUMBER) - 1, options);
                }
            });

            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                btnEnd.setVisibility(View.GONE);
                btnPrev.setVisibility(View.GONE);
            }

            if (getArguments().getInt(ARG_SECTION_NUMBER) == questionsList.size()) {
                btnEnd.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.GONE);
            }

            return rootView;
        }

        public boolean setAnswers(EditText edtxValor, int position, List<RadioButton> options) {
            boolean nextTo = false;
            switch (questionsList.get(position).getType()) {
                case 0://Text Questions
                    boolean validation = false;
                    if (edtxValor.getText() != null && edtxValor.getText().toString().trim().length() > 0) {
                        switch (questionsList.get(position).getIdQuiz()) {
                            case 3: //Nombre
                                validation = true;
                                break;

                            case 1: //Email
                                if (emailValidation(edtxValor.getText().toString())) {
                                    validation = true;
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msgInvalidEmail), Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case 4: //Year
                                if (yearValidation(edtxValor.getText().toString())) {
                                    validation = true;
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msgInvalidYear), Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        if (validation) {
                            answersList.get(position).setTxtDescription(edtxValor.getText().toString().trim());
                            nextTo = true;
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msgErrorQuestionResp), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1://Option Questions
                    if (answerCheck != 0) {
                        List<Options> answers = new ArrayList<>();
                        answers.add(new Options(answerCheck, "Respuesta"));
                        answersList.get(position).setOpcOptions(answers);
                        answerCheck = 0;
                        nextTo = true;
                    } else if (answersList.get(position).getOpcOptions() != null && answersList.get(position).getOpcOptions().size() > 0) {
                        for (Options entry : answersList.get(position).getOpcOptions()) {
                            answerCheck = entry.getOpcKey();
                            if (answerCheck != 0) {
                                nextTo = true;
                            }
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.msgErrorQuestionOpc), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2://Is not used
                    if (answerCheck != 0) {
                        List<Options> answers = new ArrayList<>();
                        answers.add(new Options(answerCheck, "Respuesta"));
                        answersList.get(position).setOpcOptions(answers);
                        answerCheck = 0;
                        nextTo = true;
                    } else if (answersList.get(position).getOpcOptions() != null && answersList.get(position).getOpcOptions().size() > 0) {
                        for (Options entry : answersList.get(position).getOpcOptions()) {
                            answerCheck = entry.getOpcKey();
                            if (answerCheck != 0) {
                                nextTo = true;
                            }
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.msgErrorQuestionOpc), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            if (nextTo) {
                answersList.get(position).setIdQuiz(questionsList.get(position).getIdQuiz());
                answersList.get(position).setDateQuiz(new Timestamp(System.currentTimeMillis()));
                mViewPager.setCurrentItem((position) + 1, true);
            }

            return nextTo;
        }

        private boolean yearValidation(String text) {
            try {
                int digits = 0;
                char characters[] = text.toCharArray();
                for (int i = 0; i < characters.length; i++) {
                    if (Character.isDigit(characters[i])) {
                        digits++;
                    }
                }
                if (digits == 4) {
                    int year = Integer.parseInt(text);
                    if (year >= 1950 && year <= 2005) {
                        return true;
                    }
                }
            } catch (Exception e) {
                return false;
            }
            return false;
        }

        private boolean emailValidation(String text) {
            String email = text;
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            return pattern.matcher(email).matches();
        }

        public void setAnswersReturn(EditText edtxValor, List<RadioButton> options, int position) {
            switch (questionsList.get(position).getType()) {
                case 0:
                    //edtxValor.setText(answersList.get(position-1).getTxtDescription());
                    break;
                case 1:
                    for (Options entry : answersList.get(position).getOpcOptions()) {
                        answerCheck = entry.getOpcKey();
                    }
                    break;
                case 2:
                    for (Options entry : answersList.get(position).getOpcOptions()) {
                        answerCheck = entry.getOpcKey();
                    }
                    break;
            }
            mViewPager.setCurrentItem((position + 1) - 1, true);
        }

        public void onRadioButtonClicked(View view) {
            boolean checked = ((RadioButton) view).isChecked();
            switch (view.getId()) {
                case R.id.rdbtnValor1:
                    if (checked)
                        answerCheck = 1;
                    break;
                case R.id.rdbtnValor2:
                    if (checked)
                        answerCheck = 2;
                    break;
                case R.id.rdbtnValor3:
                    if (checked)
                        answerCheck = 3;
                    break;
                case R.id.rdbtnValor4:
                    if (checked)
                        answerCheck = 4;
                    break;
                case R.id.rdbtnValor5:
                    if (checked)
                        answerCheck = 5;
                    break;
            }
        }


        public void insertData(final Map<String, String> params, final Button endBtn) {

            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest postRequest1 = new StringRequest(
                    Request.Method.POST,
                    Constants.URL_CONSUMMER,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response1) {
                            Log.d(TAG + ".onResponseID", params.toString());
                            Log.d(TAG + ".onResponseID", response1);
                            try {
                                User user = new Gson().fromJson(response1, User.class);
                                if (user != null && user.getEstado() == 1) {//Valida que se insertó bien - Falta validar que el email ya está en uso
                                    DataSession.saveDataSession(mContext, Constants.INFO_SESSION_KEY, response1);
                                    Toast.makeText(mContext, mContext.getText(R.string.msgWelcome) + user.getNombres() + ".!", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(mContext, mContext.getText(R.string.msgEmailSent), Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    mActivity.startActivity(intent);
                                    mActivity.finish();
                                    response1=null;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println(e.getMessage());
                                Toast.makeText(mContext, mContext.getText(R.string.msgErrorUser), Toast.LENGTH_SHORT).show();
                                endBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext, mContext.getText(R.string.msgErrorServerResponse), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }
            };
            queue.add(postRequest1);
        }


    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return questionsList.size();
        }
    }

    public void loadQuestions(final Map<String, String> params) {

        RequestQueue queue;
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        queue = new RequestQueue(cache, network);
        // Start the queue
        queue.start();

        //RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST,
                Constants.URL_CONSUMMER,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG + ".onResponseLQ param", params.toString());
                        Log.d(TAG + ".onResponseLQ", response);
                        questionsList = new ArrayList<>();
                        Gson gson = new Gson();
                        Questions[] questions = gson.fromJson(response, Questions[].class);
                        questionsList.addAll(Arrays.asList(questions));
                        answersList = new ArrayList<>();
                        for (int i = 0; i < questionsList.size(); i++) {
                            answersList.add(new Questions());
                        }
                        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                        mViewPager = (CustomViewPager) findViewById(R.id.container);
                        mViewPager.setAdapter(mSectionsPagerAdapter);
                        mViewPager.setPagingEnabled(false);
                        response=null;
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), getString(R.string.msgErrorServerResponse), Toast.LENGTH_SHORT).show();
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