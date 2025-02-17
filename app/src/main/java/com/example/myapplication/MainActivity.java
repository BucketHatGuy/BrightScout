package com.example.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    static MainActivity mainActivity;

    static int maxDataID = 0;
    static int currentDataID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainActivity = this;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("BrightScout");

        FloatingActionButton newmatchFab = findViewById(R.id.newMatchFab);
        newmatchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxDataID++;
                createMatch(maxDataID);
                Toast.makeText(MainActivity.this, "New match made! Please scroll and click the title to begin scouting.", Toast.LENGTH_SHORT).show();
            }
        });
        syncAllData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Export data to CSV");
        menu.add("Import via QR");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Export data to CSV")){
            File rawFile = MainActivity.this.getExternalFilesDir(null);
            File averagedFile = MainActivity.this.getExternalFilesDir(null);

            try {
                FileWriter rawFileWriter = new FileWriter(rawFile + "/BrightScout_rawExport.txt");
                rawFileWriter.write(makeCSVString(null));
                rawFileWriter.close();

                FileWriter avgFileWriter = new FileWriter(averagedFile + "/BrightScout_averageExport.txt");
                avgFileWriter.write(makeCSVString(getAverages()));
                avgFileWriter.close();

                Toast.makeText(this, "Data has been saved. Search for \"BrightScout\" in your files app.", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, "An error occurred.", Toast.LENGTH_LONG).show();
                throw new RuntimeException(e);
            }

        }

        if(item.getTitle().equals("Import via QR")) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
            intentIntegrator.setPrompt("Please scan a valid BrightScout QR Code.");
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.initiateScan();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            if(intentResult.getContents() != null){
                compileQRData(intentResult.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void createMatch(int dataID){
        //declares layout
        LinearLayout mainLayout = findViewById(R.id.linearLayout);
        ScrollView scrollView = findViewById(R.id.matchListView);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);

        // set up params
        LinearLayout.LayoutParams everythingLayoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams
                (400, 150);

        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams
                (400, 75);

        LinearLayout.LayoutParams trashParams = new LinearLayout.LayoutParams
                (175, 175);

        LinearLayout.LayoutParams spacingLineParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, 60);

        LinearLayout.LayoutParams invisibleParams = new LinearLayout.LayoutParams
                (1, 1);


        //set up layout for everything
        LinearLayout everythingLayout = new LinearLayout(MainActivity.this);
        everythingLayout.setOrientation(LinearLayout.HORIZONTAL);
        everythingLayout.setGravity(Gravity.CENTER);

        //set up layout for text
        LinearLayout textLayout = new LinearLayout(MainActivity.this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setGravity(Gravity.CENTER);

        textLayout.setId(dataID);
        currentDataID = dataID;

        textLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDataID = v.getId();
                startActivity(new Intent(MainActivity.this, ScoutActivity.class));
            }
        });

        // set up qual text
        TextView qualText = new TextView(this);
        qualText.setText("Quals ??");
        qualText.setTypeface(null, Typeface.BOLD);
        qualText.setId(1000 + dataID);
        qualText.setTextSize(25);

        // set up team text
        TextView teamText = new TextView(this);
        teamText.setText("Team ????");
        teamText.setId(View.generateViewId());
        teamText.setId(2000 + dataID);
        teamText.setTextSize(25);

        //set up spacer
        TextView spacingLine = new TextView(this);
        spacingLine.setGravity(Gravity.CENTER);
        spacingLine.setText("----------------------------------------------------");
        spacingLine.setTextSize(25);

        //set up trash button
        ImageButton trashButton = new ImageButton(this);
        trashButton.setImageResource(R.drawable.trash_can);
        trashButton.setScaleType(ImageButton.ScaleType.FIT_XY);
        trashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Match data has been deleted.", Toast.LENGTH_SHORT).show();
                mainLayout.removeView(everythingLayout);
                mainLayout.removeView(spacingLine);
                dataBaseHelper.removeOne(textLayout.getId());
            }
        });

        //sets default images and text to be not visible
        TextView text = findViewById(R.id.textView);
        text.setVisibility(View.INVISIBLE);
        text.setLayoutParams(invisibleParams);

        Space spacer = findViewById(R.id.spacer);
        spacer.setVisibility(View.INVISIBLE);
        spacer.setLayoutParams(invisibleParams);

        ImageView image = findViewById(R.id.imageView);
        image.setVisibility(View.INVISIBLE);
        image.setLayoutParams(invisibleParams);

        //adds views to where needed
        textLayout.addView(qualText, textViewParams);
        textLayout.addView(teamText, textViewParams);

        everythingLayout.addView(textLayout, textLayoutParams);
        everythingLayout.addView(trashButton, trashParams);

        mainLayout.addView(everythingLayout, everythingLayoutParams);
        mainLayout.addView(spacingLine, spacingLineParams);
    }

    public static MainActivity getInstance(){
        return mainActivity;
    }

    public void syncAllData(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        Toast.makeText(MainActivity.this, "Syncing data...", Toast.LENGTH_SHORT).show();

        if(dataBaseHelper.checkForTable()){
            ArrayList<ArrayList<String>> scoutingDataList = dataBaseHelper.getTable(false);

            if(scoutingDataList.isEmpty()){
                generateDefaultHomeScreen();
            } else {
                for (ArrayList<String> scoutModel : scoutingDataList) {
                    createMatch(Integer.parseInt(scoutModel.get(0)));
                    refreshMatchTitle(Integer.parseInt(scoutModel.get(0)));

                    if(maxDataID < Integer.parseInt(scoutModel.get(0))){
                        maxDataID = Integer.parseInt(scoutModel.get(0));
                    }
                }
            }
        } else {
            dataBaseHelper.createTable();
            generateDefaultHomeScreen();
        }
    }

    public void refreshMatchTitle(int dataID){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        ArrayList<String> scoutModel = dataBaseHelper.getScoutingData(dataID);

        if(scoutModel != null){
            int qualId = 1000 + dataID;
            TextView qualsText = findViewById(qualId);

            int teamId = 2000 + dataID;
            TextView teamText = findViewById(teamId);

            try {
                qualsText.setText("Quals " + scoutModel.get(3));
                teamText.setText("Team " + scoutModel.get(2));
            } catch(Exception e){
                Log.d("Error", "Error refreshing match title.");
            }
        } else {
            Log.d("Error", "Scout Model returned Null");
            Log.d("Id Attempted", String.valueOf(MainActivity.currentDataID));
        }
    }

    public void generateDefaultHomeScreen(){
        ImageView image = findViewById(R.id.imageView);
        image.setVisibility(View.VISIBLE);
        image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Space spacer = findViewById(R.id.spacer);
        spacer.setVisibility(View.VISIBLE);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20));

        TextView text = findViewById(R.id.textView);
        text.setVisibility(View.VISIBLE);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    public ArrayList<ArrayList<String>> getAverages(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        HashSet<String> teamNumbersSet = new HashSet<>(dataBaseHelper.getTableSpecific("SCOUTED_TEAM", null));
        ArrayList<String> columnNamesArray = new ArrayList<>();
        ArrayList<String> resultsArray = new ArrayList<>();
        ArrayList<String> endgameDropdownArray = new ArrayList<>();

        ArrayList<ArrayList<String>> averageTable = new ArrayList<>();

        Collections.addAll(columnNamesArray, dataBaseHelper.getColumnNames());

        columnNamesArray.remove("DATA_ID");
        columnNamesArray.remove("SCOUT_NAME");
        columnNamesArray.remove("QUALS_MATCH");
        columnNamesArray.remove("ROBOT_POSITION");
        columnNamesArray.remove("COMMENTS");

        averageTable.add(columnNamesArray);

        for(String teamNumber : teamNumbersSet){
            ArrayList<String> teamAverageArray = new ArrayList<>();

            for(String column : columnNamesArray){
                resultsArray = dataBaseHelper.getTableSpecific(column, "SCOUTED_TEAM = \"" + teamNumber + "\"");
                Log.d("size", String.valueOf(resultsArray.size()));
                Log.d("Cycles", "We have cycled through the loop!");

                if(column.equals("AUTO_MOVE") || column.equals("ALGAE_REMOVAL_TOP") || column.equals("ALGAE_REMOVAL_BOTTOM")){
                    int totalYesAnswers = 0;
                    int totalAnswers = resultsArray.size();

                    for(String yesOrNo : resultsArray){
                        if(yesOrNo.equals("1")){
                            totalYesAnswers++;
                        }
                    }

                    Log.d("yesAnswers", String.valueOf(totalYesAnswers));
                    Log.d("allAnswers", String.valueOf(totalAnswers));
                    Log.d("division", String.valueOf(Math.round((double) totalYesAnswers/totalAnswers * 100.0)));

                    teamAverageArray.add(Math.round((double) totalYesAnswers/totalAnswers * 100.0) + "%");
                } else if(column.equals("END_GAME")){
                    String mostCommonAnswer = null;
                    int maxCount = -1;

                    for (String answer : resultsArray) {
                        int count = Collections.frequency(resultsArray, answer);
                        if (count > maxCount) {
                            mostCommonAnswer = answer;
                            maxCount = count;
                        }
                    }

                    teamAverageArray.add(mostCommonAnswer + " (" + Math.round((double) maxCount/resultsArray.size() * 100.0) + "%)");
//                    teamAverageArray.add(mostCommonAnswer + " (" + (maxCount/resultsArray.size())*100 + "%)");
                } else if (column.equals("SCOUTED_TEAM")){
                    teamAverageArray.add(teamNumber);
                } else {
                    int total = 0;
                    double result = 0.0;

                    for(String number : resultsArray){
                        total += Integer.parseInt(number);
                    }

                    result = Math.round((double) total/resultsArray.size() * 100.0) / 100.0;

                    teamAverageArray.add(String.valueOf(result));
                    Log.d("result", String.valueOf(result));
                }
            }

            averageTable.add(teamAverageArray);
        }

        return averageTable;
    }

    public String makeCSVString(ArrayList<ArrayList<String>> table){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        ArrayList<ArrayList<String>> scoutingDataList = (table != null) ? table : dataBaseHelper.getTable(true);
        StringBuilder csvString = new StringBuilder();
        int columnTotal = 0;
        int columnNumber = 0;

        Log.d("scoutingDataList.size", String.valueOf(scoutingDataList.size()));

        for (ArrayList<String> scoutModel : scoutingDataList) {
            columnTotal = scoutModel.size();
            columnNumber = 0;

            for (String string : scoutModel){
                columnNumber++;
                csvString.append(string);
                if(columnNumber != columnTotal){
                    csvString.append(",");
                }
            }

            csvString.append("\n");
        }

        return csvString.toString();
    }

    public void compileQRData(String dataString){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        maxDataID++;

        try {
            ArrayList<String> scoutModel = new ArrayList<>();

            scoutModel.add(String.valueOf(maxDataID));
            Collections.addAll(scoutModel, dataString.split(","));

            dataBaseHelper.addOne(scoutModel);

            createMatch(maxDataID);
            refreshMatchTitle(maxDataID);

            Toast.makeText(MainActivity.this, "Compiling Successful!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error while compiling data.", Toast.LENGTH_SHORT).show();
        }
    }
}