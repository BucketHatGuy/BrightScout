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
        try {
            createTable();
            System.out.println("No table detected, new table made.");
        } catch(Exception e) {
            System.out.println("Table detected, no table made.");
            e.printStackTrace();
        }

        ArrayList<String> scoutingDataArray = new ArrayList<>();

        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE WHERE DATA_ID=" + dataID, null);

            int columnCount = cursor.getColumnCount();

            while (cursor.moveToNext()) {
                for (int i = 1; i <= columnCount; i++) {
                    scoutingDataArray.add(cursor.getString(i));
                    if (i < columnCount) {
                        scoutingDataArray.add(",");
                    }
                }
                scoutingDataArray.add("\n");
            }
        } catch(Exception exception){
            return null;
        }

        return scoutingDataArray;
    }

    public ArrayList<ArrayList<String>> getTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE", null);
        ArrayList<ArrayList<String>> dataTable = new ArrayList<>();

        int columnCount = cursor.getColumnCount();

        while (cursor.moveToNext()){
            ArrayList<String> scoutModel = new ArrayList<>();

            for (int i = 1; i <= columnCount; i++) {
                scoutModel.add(cursor.getString(i));
            }

            dataTable.add(scoutModel);
        }

        Log.d("we got the table", "message");

        db.close();
        cursor.close();

        return dataTable;
    }

    public boolean checkForTable(){
        Cursor cursor;

        try{
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE", null);
        } catch(Exception e) {
            return false;
        }

        return cursor.moveToFirst();
    }

    public void createTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableStatement = "CREATE TABLE SCOUTING_TABLE (DATA_ID INT PRIMARY KEY, SCOUT_NAME TEXT, SCOUTED_TEAM INT, QUALS_MATCH INT, ROBOT_POSITION TEXT)";
        db.execSQL(createTableStatement);
//        Log.d("we made the table!","message");
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
        cv.put("AUTO_SCORED_CORAL", scoutModel.get(6));
        cv.put("AUTO_FAILED_CORAL", scoutModel.get(7));
        cv.put("AUTO_ALGAE_REMOVAL", scoutModel.get(8));
        cv.put("TELEOP_SCORED_CORAL", scoutModel.get(9));
        cv.put("TELEOP_MISSED_CORAL", scoutModel.get(10));
        cv.put("TELEOP_ALGAE_REMOVAL", scoutModel.get(11));
        cv.put("TELEOP_PROCESSOR", scoutModel.get(12));
        cv.put("TELEOP_NET", scoutModel.get(13));
        cv.put("END_GAME", scoutModel.get(14));
        cv.put("COMMENTS", scoutModel.get(15));

        // if there was data already there, then delete it? (i'm not sure this is necessary, but whatever)
        Cursor cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE WHERE DATA_ID=" + scoutModel.get(0), null);
        if(cursor.moveToFirst()){
            db.delete("SCOUTING_TABLE","DATA_ID=" + scoutModel.get(0), null);
        }

        db.insert("SCOUTING_TABLE", null, cv);
    }

    public void removeOne(int dataID){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("SCOUTING_TABLE","DATA_ID=" + dataID, null);
    }
}
