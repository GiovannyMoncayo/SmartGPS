package be.uliege.uce.smartgps.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.gms.location.DetectedActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.uliege.uce.smartgps.entities.Sensor;

public class Utilidades {

    public static String getActivityLabel(int actividad){
        String label = "NULL";

        switch (actividad) {
            case DetectedActivity.IN_VEHICLE: {
                label = "IN_VEHICLE";
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = "ON_BICYCLE";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = "ON_FOOT";
                break;
            }
            case DetectedActivity.RUNNING: {
                label = "RUNNING";
                break;
            }
            case DetectedActivity.STILL: {
                label = "STILL";
                break;
            }
            case DetectedActivity.TILTING: {
                label = "TILTING";
                break;
            }
            case DetectedActivity.WALKING: {
                label = "WALKING";
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = "UNKNOWN";
                break;
            }
        }
        return label;
    }

    public static Bitmap getBitmapFromURL(String src) {

        if(src != null && src.length() > 0) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    public static boolean setSensorObject(Sensor sensor, Sensor sensorOld){
        if(sensor.getLatitude() != null && sensor.getLongitude() != null){
            if(sensorOld == null){
                return true;
            }else{
                if(sensorOld.getLatitude() != null && sensorOld.getLongitude() != null){
                    if(sensor.getLatitude() != sensorOld.getLatitude() || sensor.getLongitude() != sensorOld.getLongitude()){
                        return true;
                    }
                }else{
                    return true;
                }
            }
        }
        return false;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean checkSincronizate(Date time, Date start1, Date end1, Date start2, Date end2, int countData ){

        if(time.after(start1) && time.before(end1) && countData > 0){
            return true;
        }

        if(time.after(start2) && time.before(end2) && countData > 0){
            return true;
        }

        return false;
    }

    public static Date formaterStringTime(String time){

        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = simpleDateFormat.parse(time);
            return date;

        } catch (ParseException e) {
            return null;
        }
    }

    public static String formaterTimeString(Date date){
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    public static boolean timerInterval(Date date, int interval){
        int min = (Integer.parseInt(new SimpleDateFormat("mm").format(date)))%interval;
        int sec = Integer.parseInt(new SimpleDateFormat("ss").format(date));

        if(min == 0 && sec == 0) {
            return true;
        }
        return false;
    }

 
}
