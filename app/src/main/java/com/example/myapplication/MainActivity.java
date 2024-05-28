package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    static int maxDataID = 0;
    static int currentDataID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("BrightScout");
    }

    public void createMatch(View v){
        Button matchButton = new Button(this);
        matchButton.setText("Quals ???");
        maxDataID++;
        matchButton.setId(maxDataID);
        matchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDataID = v.getId();
                startActivity(new Intent(MainActivity.this, ScoutActivity.class));
            }
        });

        LinearLayout mainLayout = findViewById(R.id.linearLayout);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams
                (ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);

        if(findViewById(R.id.textView2) != null){
            TextView text = findViewById(R.id.textView2);
            text.setVisibility(View.INVISIBLE);

            Space spacer = findViewById(R.id.spacer);
            spacer.setVisibility(View.INVISIBLE);

            ImageView image = findViewById(R.id.imageView);
            image.setVisibility(View.INVISIBLE);
        }

        mainLayout.addView(matchButton, layoutParams);
    }
}