package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

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

    public void createTable(){
        // this will be used in the case of someone opening the app for the first time
        // which... i'm not sure when to exactly execute, but i can't just do this during onCreate like I did before.
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableStatement = "CREATE TABLE SCOUTING_TABLE (DATA_ID INT PRIMARY KEY, SCOUT_NAME TEXT, SCOUTED_TEAM INT, QUALS_MATCH INT, ROBOT_POSITION TEXT)";
        try{
            db.delete("SCOUTING_TABLE",null,null);
        } catch(Exception e){
            Log.d("Error", "Table not found. Opting to make new table.");
        }

        db.execSQL(createTableStatement);
        Log.d("we made the table!","message");
    }

    public boolean addOne(ScoutModel scoutModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("DATA_ID", scoutModel.getDataID());
        cv.put("SCOUT_NAME", scoutModel.getName());
        cv.put("SCOUTED_TEAM", scoutModel.getTeamScouted());
        cv.put("QUALS_MATCH", scoutModel.getQualNumber());
        cv.put("ROBOT_POSITION", scoutModel.getRobotPosition());

        Cursor cursor = db.rawQuery("SELECT * FROM SCOUTING_TABLE WHERE DATA_ID=" + scoutModel.getDataID(), null);
        if(cursor.moveToFirst()){
            db.delete("SCOUTING_TABLE","DATA_ID=" + scoutModel.getDataID(), null);
        }

        db.insert("SCOUTING_TABLE", null, cv);

        return true;
    }
}
