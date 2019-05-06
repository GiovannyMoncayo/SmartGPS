package be.uliege.uce.smartgps.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

import static be.uliege.uce.smartgps.utilities.Constants.DATABASE_NAME;
import static be.uliege.uce.smartgps.utilities.Constants.SENSOR_COLUMN_DATA;
import static be.uliege.uce.smartgps.utilities.Constants.SENSOR_COLUMN_DTA_ID;
import static be.uliege.uce.smartgps.utilities.Constants.SENSOR_TABLE_NAME;

public class SQLiteController extends SQLiteOpenHelper {

    public SQLiteController(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "create table "+SENSOR_TABLE_NAME+" ("+SENSOR_COLUMN_DTA_ID+" integer primary key, "+SENSOR_COLUMN_DATA+" text)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+SENSOR_TABLE_NAME);
    }

    public boolean insertData (String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SENSOR_COLUMN_DATA, data);
        db.insert(SENSOR_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+SENSOR_TABLE_NAME+" where "+SENSOR_COLUMN_DTA_ID+" = ? ", new String[] { Integer.toString(id) } );
        res.moveToFirst();
        return res;
    }

    public Cursor getLastData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select MAX("+SENSOR_COLUMN_DTA_ID+")  from "+SENSOR_TABLE_NAME, null );
        res.moveToFirst();
        return res;
    }

    public int countRowsData(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SENSOR_TABLE_NAME);
        return numRows;
    }

    public boolean updateData (Integer id, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SENSOR_COLUMN_DATA, data);
        db.update(SENSOR_TABLE_NAME, contentValues, SENSOR_COLUMN_DTA_ID+" = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteData (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer retorno = db.delete(SENSOR_TABLE_NAME, SENSOR_COLUMN_DTA_ID+" = ? ", new String[] { Integer.toString(id) });
        db.close();
        return retorno;
    }

    /*public ArrayList<String> getAllData() {
        ArrayList<String> arrayList = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+SENSOR_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            arrayList.add(res.getString(res.getColumnIndex(SENSOR_COLUMN_DATA)));
            res.moveToNext();
        }
        return arrayList;
    }*/

    public Map<String, String> getAllData() {
        Map<String, String> arrayList = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+SENSOR_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            arrayList.put(res.getString(res.getColumnIndex(SENSOR_COLUMN_DTA_ID)), res.getString(res.getColumnIndex(SENSOR_COLUMN_DATA)));
            res.moveToNext();
        }
        return arrayList;
    }
}