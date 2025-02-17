package com.example.myapplication;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Arrays;

public class ScoutActivity extends AppCompatActivity {

    Button compileButton, dismissDialogButton, finishedButton, commentButton;
    EditText scoutNameBox, scoutedTeamBox, qualsMatchBox, commentBox;
    TextView autoL1Text, autoL2Text, autoL3Text, autoL4Text, autoCoralDroppedText, teleopL1Text, teleopL2Text, teleopL3Text, teleopL4Text,
            teleopCoralDroppedText, processorText, netText;
    TextInputLayout endgameDropdownPreview;
    CheckBox moveCheckbox, topAlgaeCheckbox, bottomAlgaeCheckbox;
    MainActivity mainActivity = MainActivity.getInstance();
    Dialog qrDialog, commentDialog;

    String[] endgameItems = {"Deep Climb", "Shallow Climb", "Kept Scoring Coral", "Kept Scoring Algae", "Park", "Nothing"};
    String[] allianceItems = {"Red", "Blue"};
    String[] positionItems = {"1", "2", "3"};
    AutoCompleteTextView allianceDropdown, positionDropdown, endgameDropdown;
    ArrayAdapter<String> adapterItem;
    final String[] allianceItemSelected = new String[1];
    final String[] positionItemSelected = new String[1];
    final String[] endgameItemSelected = new String[1];

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

        qrDialog = new Dialog(ScoutActivity.this);
        qrDialog.setContentView(R.layout.qrcode_dialogue_box);
        qrDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        qrDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        qrDialog.setCancelable(false);

        dismissDialogButton = qrDialog.findViewById(R.id.returnButton);

        dismissDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrDialog.dismiss();
                startActivity(new Intent(ScoutActivity.this, MainActivity.class));
            }
        });

        commentDialog = new Dialog(ScoutActivity.this);
        commentDialog.setContentView(R.layout.comment_dialogue_box);
        commentDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        commentDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        commentDialog.setCancelable(false);

        finishedButton = commentDialog.findViewById(R.id.finishedButton);

        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDialog.dismiss();
            }
        });

        commentButton = findViewById(R.id.commentButton);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentDialog.show();
            }
        });

        compileButton = findViewById(R.id.compileButton);

        scoutNameBox = findViewById(R.id.scoutNameBoxView);
        scoutedTeamBox = findViewById(R.id.scoutedTeamBoxView);
        qualsMatchBox = findViewById(R.id.qualsMatchBoxView);
        commentBox = commentDialog.findViewById(R.id.commentsTextBox);

        autoL1Text = findViewById(R.id.autoL1Text);
        autoL2Text = findViewById(R.id.autoL2Text);
        autoL3Text = findViewById(R.id.autoL3Text);
        autoL4Text = findViewById(R.id.autoL4Text);
        autoCoralDroppedText = findViewById(R.id.autoCoralDroppedText);
        teleopL1Text = findViewById(R.id.teleopL1Text);
        teleopL2Text = findViewById(R.id.teleopL2Text);
        teleopL3Text = findViewById(R.id.teleopL3Text);
        teleopL4Text = findViewById(R.id.teleopL4Text);
        teleopCoralDroppedText = findViewById(R.id.teleopCoralDroppedText);
        processorText = findViewById(R.id.processorText);
        netText = findViewById(R.id.netText);

        moveCheckbox = findViewById(R.id.moveCheckbox);
        topAlgaeCheckbox = findViewById(R.id.topAlgaeCheckbox);
        bottomAlgaeCheckbox = findViewById(R.id.bottomAlgaeCheckbox);

        allianceDropdown = findViewById(R.id.allianceDropDown);
        positionDropdown = findViewById(R.id.positionDropDown);
        endgameDropdown = findViewById(R.id.endgameDropDown);

        insertSavedData();

        compileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allDataFilledCheck()){
                    compileData();
                    qrDialog.show();
                }

                try{
                    mainActivity.refreshMatchTitle(MainActivity.currentDataID);
                }catch(Exception e){
                    Log.d("Error", e.getMessage());
                }
            }
        });

        // setting up allianceDropdown
        adapterItem = new ArrayAdapter<String>(this, R.layout.list_item, allianceItems);

        allianceDropdown.setAdapter(adapterItem);

        allianceDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                allianceItemSelected[0] = parent.getItemAtPosition(position).toString();
            }
        });

        // setting up positionDropdown
        adapterItem = new ArrayAdapter<String>(this, R.layout.list_item, positionItems);

        positionDropdown.setAdapter(adapterItem);

        positionDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionItemSelected[0] = parent.getItemAtPosition(position).toString();
            }
        });

        // setting up endgameDropdown
        adapterItem = new ArrayAdapter<String>(this, R.layout.list_item, endgameItems);

        endgameDropdown.setAdapter(adapterItem);

        endgameDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                endgameItemSelected[0] = parent.getItemAtPosition(position).toString();
            }
        });
    }

    public boolean allDataFilledCheck(){
        boolean isScoutNameBoxEmpty = scoutNameBox.getText().toString().isEmpty();
        boolean isScoutedTeamBoxEmpty = scoutedTeamBox.getText().toString().isEmpty();
        boolean isQualsMatchBoxEmpty = qualsMatchBox.getText().toString().isEmpty();
        boolean isCommentBoxEmpty = commentBox.getText().toString().isEmpty();
        boolean isAllianceDropdownEmpty = false;
        boolean isPositionDropdownEmpty = false;
        boolean isEndgameDropdownEmpty = false;

        try{
            isAllianceDropdownEmpty = (allianceDropdown.getText().toString().isEmpty());
        } catch(Exception e){
            Log.d("Error", e.getMessage());
        }

        try{
            isPositionDropdownEmpty = (positionDropdown.getText().toString().isEmpty());
        } catch(Exception e){
            Log.d("Error", e.getMessage());
        }

        try{
            isEndgameDropdownEmpty = (endgameDropdown.getText().toString().isEmpty());
        } catch(Exception e){
            Log.d("Error", e.getMessage());
        }

        try {
            if(isScoutNameBoxEmpty || isScoutedTeamBoxEmpty || isQualsMatchBoxEmpty || isAllianceDropdownEmpty || isPositionDropdownEmpty || isEndgameDropdownEmpty || isCommentBoxEmpty){
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

                if(isCommentBoxEmpty){
                    commentBox.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                } else {
                    commentBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                }
                Toast.makeText(ScoutActivity.this, "Blank Box Error (did you forget something?)", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }

        } catch (Exception e){
            Toast.makeText(ScoutActivity.this, "Unknown Compiling Error Occurred.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void compileData(){
        ArrayList<String> scoutModel = new ArrayList<>();

        scoutModel.add(String.valueOf(MainActivity.currentDataID));
        scoutModel.add(scoutNameBox.getText().toString().replaceAll("[^A-Za-z0-9 ]",""));
        scoutModel.add(scoutedTeamBox.getText().toString());
        scoutModel.add(qualsMatchBox.getText().toString());
        scoutModel.add(allianceDropdown.getText().toString() + " " + positionDropdown.getText().toString());
        scoutModel.add(moveCheckbox.isChecked() ? "1" : "0");
        scoutModel.add(autoL1Text.getText().toString());
        scoutModel.add(autoL2Text.getText().toString());
        scoutModel.add(autoL3Text.getText().toString());
        scoutModel.add(autoL4Text.getText().toString());
        scoutModel.add(autoCoralDroppedText.getText().toString());
        scoutModel.add(teleopL1Text.getText().toString());
        scoutModel.add(teleopL2Text.getText().toString());
        scoutModel.add(teleopL3Text.getText().toString());
        scoutModel.add(teleopL4Text.getText().toString());
        scoutModel.add(teleopCoralDroppedText.getText().toString());
        scoutModel.add(topAlgaeCheckbox.isChecked() ? "1" : "0");
        scoutModel.add(bottomAlgaeCheckbox.isChecked() ? "1" : "0");
        scoutModel.add(processorText.getText().toString());
        scoutModel.add(netText.getText().toString());
        scoutModel.add(endgameDropdown.getText().toString());
        scoutModel.add(commentBox.getText().toString().replaceAll("[^A-Za-z0-9 ]",""));

        scoutNameBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        scoutedTeamBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        qualsMatchBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        commentBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

        DataBaseHelper dataBaseHelper = new DataBaseHelper(ScoutActivity.this);
        dataBaseHelper.addOne(scoutModel);
        Toast.makeText(this, "Compiled Successfully!", Toast.LENGTH_LONG).show();

        scoutModel.remove(0);
        generateQRCode(makeCSVString(scoutModel));
    }

    public void insertSavedData(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(ScoutActivity.this);
        ArrayList<String> scoutModel = dataBaseHelper.getScoutingData(MainActivity.currentDataID);

        try{
            String[] robotPositionArray = scoutModel.get(4).split(" ");

            scoutNameBox.setText(scoutModel.get(1));
            scoutedTeamBox.setText(String.valueOf(scoutModel.get(2)));
            qualsMatchBox.setText(String.valueOf(scoutModel.get(3)));
            allianceDropdown.setText(robotPositionArray[0]); positionDropdown.setText(robotPositionArray[1]);
            moveCheckbox.setChecked(scoutModel.get(5).equals("1"));
            autoL1Text.setText(scoutModel.get(6));
            autoL2Text.setText(scoutModel.get(7));
            autoL3Text.setText(scoutModel.get(8));
            autoL4Text.setText(scoutModel.get(9));
            autoCoralDroppedText.setText(scoutModel.get(10));
            teleopL1Text.setText(scoutModel.get(11));
            teleopL2Text.setText(scoutModel.get(12));
            teleopL3Text.setText(scoutModel.get(13));
            teleopL4Text.setText(scoutModel.get(14));
            teleopCoralDroppedText.setText(scoutModel.get(15));
            topAlgaeCheckbox.setChecked(scoutModel.get(16).equals("1"));
            bottomAlgaeCheckbox.setChecked(scoutModel.get(17).equals("1"));
            processorText.setText(scoutModel.get(18));
            netText.setText(scoutModel.get(19));
            endgameDropdown.setText(scoutModel.get(20), true);
            commentBox.setText(String.valueOf(scoutModel.get(21)));

        } catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, "No data to insert, leaving default.", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateQRCode(String text) {
        ImageView imageView3 = qrDialog.findViewById(R.id.imageView3);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 1000, 1000);
            Bitmap rawLogoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_image);
            int newDimension = 152;
            Bitmap adjustedLogoBitmap = Bitmap.createScaledBitmap(rawLogoBitmap, newDimension, newDimension, false);

            Bitmap combinedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

            Canvas canvas = new Canvas(combinedBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);

            int centerX = (bitmap.getWidth() - adjustedLogoBitmap.getWidth()) / 2;
            int centerY = (bitmap.getHeight() - adjustedLogoBitmap.getHeight()) / 2;

            canvas.drawBitmap(adjustedLogoBitmap, centerX, centerY, null);

            imageView3.setImageBitmap(combinedBitmap);
        }
        catch (WriterException e) {
            Log.d("Error", "Something went wrong at generateQRCode()");
        }
    }

    public String makeCSVString(ArrayList<String> scoutModel){
        StringBuilder csvString = new StringBuilder();

        for(String item : scoutModel){
            if(!(item.equals(scoutModel.get(scoutModel.size() - 1)))){
                csvString.append(item).append(",");
            } else {
                csvString.append(item);
            }
        }

//        csvString.append(scoutModel.get(1)).append(",");
//        csvString.append(scoutModel.get(2)).append(",");
//        csvString.append(scoutModel.get(3)).append(",");
//        csvString.append(scoutModel.get(4));

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", csvString.toString());
        clipboard.setPrimaryClip(clip);

        return csvString.toString();
    }

    public void addAndMinusHandler(View view){
        String buttonName = getResources().getResourceEntryName(view.getId());
        String buttonNameFiltered = "";
        TextView textView;
        int number = 0;

        if(buttonName.contains("Minus")){
            buttonNameFiltered = buttonName.replaceAll("MinusButton", "");
            int resID = getResources().getIdentifier(buttonNameFiltered + "Text", "id", getPackageName());
            textView = findViewById(resID);

            number = Integer.parseInt(textView.getText().toString());

            if(!((number - 1) < 0)){
                textView.setText(String.valueOf(number - 1));
            } else {
                textView.setText(String.valueOf(0));
            }
        } else if (buttonName.contains("Add")){
            buttonNameFiltered = buttonName.replaceAll("AddButton", "");
            int resID = getResources().getIdentifier(buttonNameFiltered + "Text", "id", getPackageName());
            textView = findViewById(resID);

            number = Integer.parseInt(textView.getText().toString());

            textView.setText(String.valueOf(number + 1));

        } else {
            Toast.makeText(this, "An error has occurred pressing this button", Toast.LENGTH_LONG).show();
        }

        // the robot should have moved if they were able to place coral, so we set it as moved if the scouter tries to add data saying they moved coral
        if(buttonName.contains("Add") && buttonName.contains("auto") && !moveCheckbox.isChecked()){
            moveCheckbox.setChecked(true);
        }

        if((buttonName.contains("processor") || buttonName.contains("net")) && (!topAlgaeCheckbox.isChecked() && !bottomAlgaeCheckbox.isChecked())){
            Toast.makeText(ScoutActivity.this, "Did you forget to check High or Low algae?", Toast.LENGTH_SHORT).show();
        }
    }
}