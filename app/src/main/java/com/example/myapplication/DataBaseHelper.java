package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

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

    public ScoutModel getScoutModel(int dataID){
        try {
            createTable();
            System.out.println("No table detected, new table made.");
        } catch(Exception e) {
            System.out.println("Table detected, no table made.");
            e.printStackTrace();
        }

        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE WHERE DATA_ID=" + dataID, null);

            if(cursor.moveToFirst()){
                dataID = cursor.getInt(0);
                String name = cursor.getString(1);
                int teamScouted = cursor.getInt(2);
                int qualNumber = cursor.getInt(3);
                String robotPosition = cursor.getString(4);

                db.close();
                cursor.close();
                return new ScoutModel(dataID, name, teamScouted, qualNumber, robotPosition);
            } else {
                Log.d("Error", "No table data found.");

                db.close();
                cursor.close();
                return null;
            }
        } catch(Exception exception){
            return null;
        }
    }

    public ArrayList<ScoutModel> getTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE", null);
        ArrayList<ScoutModel> returnList = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                int dataID = cursor.getInt(0);
                String name = cursor.getString(1);
                int teamScouted = cursor.getInt(2);
                int qualNumber = cursor.getInt(3);
                String robotPosition = cursor.getString(4);

                returnList.add(new ScoutModel(dataID, name, teamScouted, qualNumber, robotPosition));
            } while (cursor.moveToNext());

            Log.d("we got the table", "message");

            db.close();
            cursor.close();
        }

        return returnList;
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

    public void addOne(ScoutModel scoutModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("DATA_ID", scoutModel.getDataID());
        cv.put("SCOUT_NAME", scoutModel.getName());
        cv.put("SCOUTED_TEAM", scoutModel.getTeamScouted());
        cv.put("QUALS_MATCH", scoutModel.getQualNumber());
        cv.put("ROBOT_POSITION", scoutModel.getRobotPosition());

        // if there was data already there, then delete it? (i'm not sure this is necessary, but whatever)
        Cursor cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE WHERE DATA_ID=" + scoutModel.getDataID(), null);
        if(cursor.moveToFirst()){
            db.delete("SCOUTING_TABLE","DATA_ID=" + scoutModel.getDataID(), null);
        }

        db.insert("SCOUTING_TABLE", null, cv);
    }

    public void removeOne(int dataID){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("SCOUTING_TABLE","DATA_ID=" + dataID, null);
    }
}
