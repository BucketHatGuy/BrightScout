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

public class MainActivity extends AppCompatActivity {

    static MainActivity mainActivity;

    static int maxDataID = 0;
    static int currentDataID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainActivity = this;
        Log.d("Marker", "Marker 7");

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
            File file = MainActivity.this.getExternalFilesDir(null);
            try {
                FileWriter fileWriter = new FileWriter(file + "/BrightScoutExport.txt");
                Log.d("file path", file + "/BrightScoutExport.txt");
                fileWriter.write(makeCSVString());
                fileWriter.close();
                Toast.makeText(this, "yay!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, "an error occured", Toast.LENGTH_LONG).show();
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
                (300, 100);

        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams
                (300, 50);

        LinearLayout.LayoutParams trashParams = new LinearLayout.LayoutParams
                (100, 100);

        LinearLayout.LayoutParams spacingLineParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, 40);

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
        Log.d("realQualsTextId", String.valueOf(qualText.getId()));

        // set up team text
        TextView teamText = new TextView(this);
        teamText.setText("Team ????");
        teamText.setId(View.generateViewId());
        teamText.setId(2000 + dataID);
        Log.d("realTeamTextId", String.valueOf(teamText.getId()));

        //set up spacer
        TextView spacingLine = new TextView(this);
        spacingLine.setGravity(Gravity.CENTER);
        spacingLine.setText("-------------------------------------------------------------");

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
        Log.d("Marker", "Marker 3");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        Toast.makeText(MainActivity.this, "Syncing data...", Toast.LENGTH_LONG).show();

        if(dataBaseHelper.checkForTable()){
            ArrayList<ArrayList<String>> scoutingDataList = dataBaseHelper.getTable();

            if(scoutingDataList.isEmpty()){
                generateDefaultHomeScreen();
            } else {
                for (ArrayList<String> scoutModel : scoutingDataList) {
                    Log.d("ID NUMBER", scoutModel.get(0));
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
        Log.d("Marker", "Marker 4");
    }

    public void refreshMatchTitle(int dataID){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        ArrayList<String> scoutModel = dataBaseHelper.getScoutingData(dataID);

        if(scoutModel != null){
            int qualId = 1000 + dataID;
            TextView qualsText = findViewById(qualId);
            Log.d("qualsTextIdAttempted", String.valueOf(1000 + dataID));

            int teamId = 2000 + dataID;
            TextView teamText = findViewById(teamId);
            Log.d("teamTextIdAttempted", String.valueOf(2000 + dataID));

            try {
                qualsText.setText("Quals " + scoutModel.get(3));
                teamText.setText("Team " + scoutModel.get(2));
            } catch(Exception e){
                Log.d("Error", "i no no wanna");
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

    public String makeCSVString(){
        Log.d("hi", "hi");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        ArrayList<ArrayList<String>> scoutingDataList = dataBaseHelper.getTable();
        StringBuilder csvString = new StringBuilder();
        int columnTotal = 0;
        int columnNumber = 0;

        Log.d("what", "what");

        for (ArrayList<String> scoutModel : scoutingDataList) {
            columnTotal = scoutModel.size();

            for (String string : scoutModel){
                csvString.append(string);
                if(columnNumber != columnTotal){
                    csvString.append(",");
                }
            }
            columnNumber++;

            csvString.append("\n");
        }

        return csvString.toString();
    }

    public void compileQRData(String dataString){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        String[] dataList = new String[4];

        maxDataID++;

        try {
            dataList = dataString.split(",");

            ArrayList<String> scoutModel = new ArrayList<>();

            Collections.addAll(scoutModel, dataList);

            dataBaseHelper.addOne(scoutModel);
            createMatch(maxDataID);
            refreshMatchTitle(maxDataID);

            Toast.makeText(MainActivity.this, "Compiling Successful!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error while compiling data.", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }
}