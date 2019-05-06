package be.uliege.uce.smartgps.utilities;

public class Constants {

    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 500;

    public static final int FREQUENSE_SECOND = 15;

    public static final String DETECTED_ACTIVITY = "detectedActivity";
    public static final String LOCATION_ACTIVITY = "locationActivity";
    public static final String SENSOR_ACTIVITY = "sensorActivity";
    public static final String GOOGLE_LOCATION_ACTIVITY = "googleLocationActivity";
    public static final String GPS_ACTIVITY = "gpsActivity";

    public static final String URL_SERVIDOR = "http://www.gmoncayoresearch.com";
    public static final String URL_SERVICE = "/SmartGPS/api";
    public static final String URL_SERVICE_SMART_MOB_UCE = "/SmartGPS/api";
    public static final String URL_NOTIFICATION = URL_SERVIDOR+URL_SERVICE+"/notificadorTelegram.php";

    public static final int DURACION_SPLASH = 2000;

    public static String INFO_SESSION_KEY = "INFO_SESSION_KEY";

    public static Integer PROVIDER_ON = 1;
    public static Integer PROVIDER_OFF = 0;

    public static final String DATABASE_NAME = "db_sensor.db";
    public static final String SENSOR_TABLE_NAME = "sensor";
    public static final String SENSOR_COLUMN_DTA_ID = "dta_id";
    public static final String SENSOR_COLUMN_DATA = "data";


    public static int TIME_RECOVER = 30;

    public static String URL_CONSUMMER = URL_SERVIDOR+URL_SERVICE_SMART_MOB_UCE+"/consummer.php";



}