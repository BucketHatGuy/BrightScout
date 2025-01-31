package com.example.myapplication;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class ScoutActivity extends AppCompatActivity {

    Button compileButton;
    EditText scoutNameBox, scoutedTeamBox, qualsMatchBox, robotPositionBox;
    MainActivity mainActivity = MainActivity.getInstance();
    Dialog dialog;
    Button dialogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scoutMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("BrightScout");

        dialog = new Dialog(ScoutActivity.this);
        dialog.setContentView(R.layout.qrcode_dialogue_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.qrcode_dialogue_bg));
        dialog.setCancelable(false);

        dialogButton = dialog.findViewById(R.id.dismissButton);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        compileButton = findViewById(R.id.compileButton);
        scoutNameBox = findViewById(R.id.scoutNameBoxView);
        scoutedTeamBox = findViewById(R.id.scoutedTeamBoxView);
        qualsMatchBox = findViewById(R.id.qualsMatchBoxView);
        robotPositionBox = findViewById(R.id.robotPositionBoxView);

        insertSavedData();

        compileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allDataFilledCheck()){
                    compileData();
                    dialog.show();
                }

                try{
                    mainActivity.refreshMatchTitle(MainActivity.currentDataID);
                }catch(Exception e){
                    Log.d("Error", e.getStackTrace().toString());
                }
            }
        });
    }

    public boolean allDataFilledCheck(){
        boolean isScoutNameBoxEmpty = scoutNameBox.getText().toString().isEmpty();
        boolean isScoutedTeamBoxEmpty = scoutedTeamBox.getText().toString().isEmpty();
        boolean isQualsMatchBoxEmpty = qualsMatchBox.getText().toString().isEmpty();
        boolean isRobotPositionBoxEmpty = robotPositionBox.getText().toString().isEmpty();

        try {
            if(isScoutNameBoxEmpty || isScoutedTeamBoxEmpty || isQualsMatchBoxEmpty || isRobotPositionBoxEmpty){
                if(isScoutNameBoxEmpty){
                    scoutNameBox.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                } else {
                    scoutNameBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                }

                if(isScoutedTeamBoxEmpty){
                    scoutedTeamBox.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                } else {
                    scoutedTeamBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                }

                if(isQualsMatchBoxEmpty){
                    qualsMatchBox.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                } else {
                    qualsMatchBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                }

                if(isRobotPositionBoxEmpty){
                    robotPositionBox.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                } else {
                    robotPositionBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                }

                Toast.makeText(ScoutActivity.this, "Blank Box Error (did you forget something?)", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }

        } catch (Exception e){
            Toast.makeText(ScoutActivity.this, "Unknown Compiling Error Occurred", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void compileData(){
        ArrayList<String> scoutModel = new ArrayList<>();

        scoutModel.add(String.valueOf(MainActivity.currentDataID));
        scoutModel.add(scoutNameBox.getText().toString());
        scoutModel.add(scoutedTeamBox.getText().toString());
        scoutModel.add(scoutNameBox.getText().toString());
        scoutModel.add(scoutNameBox.getText().toString());

        scoutNameBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        scoutedTeamBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        qualsMatchBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        robotPositionBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

        DataBaseHelper dataBaseHelper = new DataBaseHelper(ScoutActivity.this);
        dataBaseHelper.addOne(scoutModel);
        Toast.makeText(this, "Compiled Successfully!", Toast.LENGTH_LONG).show();

        generateQRCode(makeCSVString(scoutModel));
    }

    public void insertSavedData(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(ScoutActivity.this);
        ArrayList<String> scoutModel = dataBaseHelper.getScoutingData(MainActivity.currentDataID);

        if(scoutModel != null){
            scoutNameBox.setText(scoutModel.get(1));
            scoutedTeamBox.setText(String.valueOf(scoutModel.get(2)));
            qualsMatchBox.setText(String.valueOf(scoutModel.get(3)));
            robotPositionBox.setText(scoutModel.get(4));
        }
    }

    private void generateQRCode(String text) {
        ImageView imageView3 = dialog.findViewById(R.id.imageView3);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 1000, 1000);
            imageView3.setImageBitmap(bitmap);
        }
        catch (WriterException e) {
            Log.d("Error", "Something went wrong at generateQRCode()");
        }
    }

    public String makeCSVString(ArrayList<String> scoutModel){
        StringBuilder csvString = new StringBuilder();

        csvString.append(scoutModel.get(1)).append(",");
        csvString.append(scoutModel.get(2)).append(",");
        csvString.append(scoutModel.get(3)).append(",");
        csvString.append(scoutModel.get(4));

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", csvString.toString());
        clipboard.setPrimaryClip(clip);

        return csvString.toString();
    }
}