package be.uliege.uce.smartgps.utilities;

public class Constants {

    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 15*1000;

    public static final int FREQUENCY_SECOND = 15;

    public static final String DETECTED_ACTIVITY = "activityData";
    public static final String LOCATION_ACTIVITY = "locationData";
    public static final String SENSOR_ACTIVITY = "sensorData";
    public static final String GOOGLE_LOCATION_ACTIVITY = "googleLocationData";
    public static final String GPS_ACTIVITY = "gpsData";

    public static final String URL_SERVER = "http://www.gmoncayoresearch.com";
    public static final String URL_SERVICE = "/SmartGPS/api";
    public static final String URL_SERVICE_SMART_GPS = "/SmartGPS/api";
    //public static final String URL_NOTIFICADOR_TELEGRAM = URL_SERVER +URL_SERVICE+"/notificadorTelegram.php";

    public static final int SPLASH_TIME_IN_MILLISECONDS = 2000;

    public static String INFO_SESSION_KEY = "INFO_SESSION_KEY";

    public static Integer PROVIDER_ON = 1;
    public static Integer PROVIDER_OFF = 0;

    public static final String DATABASE_NAME = "db_sensor.db";
    public static final String SENSOR_TABLE_NAME = "sensor";
    public static final String SENSOR_COLUMN_DTA_ID = "dta_id";
    public static final String SENSOR_COLUMN_DATA = "data";

    public static String URL_CONSUMMER = URL_SERVER+URL_SERVICE_SMART_GPS+"/consummer.php";

    public static int TIME_RECOVER = 30;
}