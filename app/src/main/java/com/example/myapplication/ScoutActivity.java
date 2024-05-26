package com.example.myapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ScoutActivity extends AppCompatActivity {

    Button compileButton;
    EditText scoutNameBox, scoutedTeamBox, qualsMatchBox, robotPositionBox;
    ListView listView;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        compileButton = findViewById(R.id.compileButton);
        scoutNameBox = findViewById(R.id.scoutNameBoxView);
        scoutedTeamBox = findViewById(R.id.scoutedTeamBoxView);
        qualsMatchBox = findViewById(R.id.qualsMatchBoxView);
        robotPositionBox = findViewById(R.id.robotPositionBoxView);
        listView = findViewById(R.id.listView);

        compileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    } else {
                        ScoutModel scoutModel = new ScoutModel(-1,
                                scoutNameBox.getText().toString(),
                                Integer.parseInt(scoutedTeamBox.getText().toString()),
                                Integer.parseInt(qualsMatchBox.getText().toString()),
                                robotPositionBox.getText().toString());

                        scoutNameBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                        scoutedTeamBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                        qualsMatchBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                        robotPositionBox.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

                        Toast.makeText(ScoutActivity.this, scoutModel.toString(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){
                    Toast.makeText(ScoutActivity.this, "Unknown Compiling Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}