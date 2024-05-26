package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(@Nullable Context context) {
        super(context, "scout.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE SCOUTING_TABLE (DATA_ID INT, SCOUT_NAME TEXT, SCOUTED_TEAM INT, QUALS_MATCH INT, ROBOT_POSITION TEXT)";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(ScoutModel scoutModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("DATA_ID", scoutModel.getDataID());
        cv.put("SCOUT_NAME", scoutModel.getName());
        cv.put("SCOUTED_TEAM", scoutModel.getTeamScouted());
        cv.put("QUALS_MATCH", scoutModel.getQualNumber());
        cv.put("ROBOT_POSITION", scoutModel.getRobotPosition());

        db.insert("SCOUTING_TABLE", null, cv);

        return true;
    }
}
