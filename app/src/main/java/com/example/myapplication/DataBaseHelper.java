package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(@Nullable Context context) {
        super(context, "scout.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<String> getScoutingData(int dataID){
        ArrayList<String> scoutingDataArray = new ArrayList<>();

        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE WHERE DATA_ID=" + dataID, null);
            int columnCount = cursor.getColumnCount();

            if(cursor.moveToFirst()) {
                for (int i = 0; i <= columnCount; i++) {
                    scoutingDataArray.add(cursor.getString(i));
                }
            }

            cursor.close();

        } catch(Exception exception){
            Log.d("Error", exception.getMessage());
        }

        return scoutingDataArray;
    }

    public ArrayList<ArrayList<String>> getTable(boolean exportFormat){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE", null);
        ArrayList<ArrayList<String>> dataTable = new ArrayList<>();

        int columnCount = cursor.getColumnCount();

        if(exportFormat) {
            ArrayList<String> columnNames = new ArrayList<>();
            Collections.addAll(columnNames, cursor.getColumnNames());
            dataTable.add(columnNames);
        }

        while (cursor.moveToNext()){
            ArrayList<String> scoutModel = new ArrayList<>();

            for (int i = 0; i < columnCount; i++) {
                scoutModel.add(cursor.getString(i));
            }

            dataTable.add(scoutModel);
        }

        db.close();
        cursor.close();

        return dataTable;
    }

    public ArrayList<String> getTableSpecific(String columnNames, String whereClause){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> requestedData = new ArrayList<>();
        int columnCount = 0;
        Cursor cursor = whereClause == null ? db.rawQuery("SELECT " + columnNames + " FROM SCOUTING_TABLE", null) :
                db.rawQuery("SELECT " + columnNames + " FROM SCOUTING_TABLE WHERE " + whereClause, null);

        columnCount = cursor.getColumnCount();

        while (cursor.moveToNext()){
            for (int i = 0; i < columnCount; i++) {
                requestedData.add(cursor.getString(i));
            }
        }

        return requestedData;
    }

    public String[] getColumnNames(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE", null);

        return cursor.getColumnNames();
    }

    public boolean checkForTable(){
        Cursor cursor;

        try{
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE", null);
            return cursor.getColumnCount() != 0;
        } catch(Exception e) {
            return false;
        }
    }

    public void createTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableStatement = "CREATE TABLE SCOUTING_TABLE (" +
                "DATA_ID INT PRIMARY KEY, " +
                "SCOUT_NAME TEXT, " +
                "SCOUTED_TEAM INT, " +
                "QUALS_MATCH INT, " +
                "ROBOT_POSITION TEXT," +
                "AUTO_MOVE TEXT," +
                "AUTO_l1 INT," +
                "AUTO_l2 INT," +
                "AUTO_l3 INT," +
                "AUTO_l4 INT," +
                "AUTO_MISSED_CORAL INT," +
                "TELEOP_l1 INT," +
                "TELEOP_l2 INT," +
                "TELEOP_l3 INT," +
                "TELEOP_l4 INT," +
                "TELEOP_MISSED_CORAL INT," +
                "ALGAE_REMOVAL_TOP TEXT," +
                "ALGAE_REMOVAL_BOTTOM TEXT," +
                "TELEOP_PROCESSOR INT," +
                "TELEOP_NET INT," +
                "END_GAME TEXT," +
                "COMMENTS TEXT," +
                "TOTAL_POINTS INT," +
                "AUTO_POINTS INT," +
                "TELEOP_POINTS INT);";

        db.execSQL(createTableStatement);
    }

    public void addOne(ArrayList<String> scoutModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("DATA_ID", scoutModel.get(0));
        cv.put("SCOUT_NAME", scoutModel.get(1));
        cv.put("SCOUTED_TEAM", scoutModel.get(2));
        cv.put("QUALS_MATCH", scoutModel.get(3));
        cv.put("ROBOT_POSITION", scoutModel.get(4));
        cv.put("AUTO_MOVE", scoutModel.get(5));
        cv.put("AUTO_l1", scoutModel.get(6));
        cv.put("AUTO_l2", scoutModel.get(7));
        cv.put("AUTO_l3", scoutModel.get(8));
        cv.put("AUTO_l4", scoutModel.get(9));
        cv.put("AUTO_MISSED_CORAL", scoutModel.get(10));
        cv.put("TELEOP_l1", scoutModel.get(11));
        cv.put("TELEOP_l2", scoutModel.get(12));
        cv.put("TELEOP_l3", scoutModel.get(13));
        cv.put("TELEOP_l4", scoutModel.get(14));
        cv.put("TELEOP_MISSED_CORAL", scoutModel.get(15));
        cv.put("ALGAE_REMOVAL_TOP", scoutModel.get(16));
        cv.put("ALGAE_REMOVAL_BOTTOM", scoutModel.get(17));
        cv.put("TELEOP_PROCESSOR", scoutModel.get(18));
        cv.put("TELEOP_NET", scoutModel.get(19));
        cv.put("END_GAME", scoutModel.get(20));
        cv.put("COMMENTS", scoutModel.get(21));
        cv.put("TOTAL_POINTS", scoutModel.get(22));
        cv.put("AUTO_POINTS", scoutModel.get(23));
        cv.put("TELEOP_POINTS", scoutModel.get(24));

        Cursor cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE WHERE DATA_ID=" + scoutModel.get(0), null);

        if(cursor.moveToFirst()){
            db.delete("SCOUTING_TABLE","DATA_ID=" + scoutModel.get(0), null);
        }

        long result = db.insert("SCOUTING_TABLE", null, cv);

        if (result == -1) {
            Log.e("Database Error", "Failed to insert data into SCOUTING_TABLE");
        } else {
            Log.i("Database Success", "Data inserted successfully into SCOUTING_TABLE");
        }
    }

    public void removeOne(int dataID){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("SCOUTING_TABLE","DATA_ID=" + dataID, null);
    }
}
