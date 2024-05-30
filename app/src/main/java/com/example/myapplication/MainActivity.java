package com.example.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("BrightScout");
    }

    public void createMatch(View v){
        //declares layout
        LinearLayout mainLayout = findViewById(R.id.linearLayout);

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

        //set up layout for everything
        LinearLayout everythingLayout = new LinearLayout(MainActivity.this);
        everythingLayout.setOrientation(LinearLayout.HORIZONTAL);
        everythingLayout.setGravity(Gravity.CENTER);

        //set up layout for text
        LinearLayout textLayout = new LinearLayout(MainActivity.this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setGravity(Gravity.CENTER);
        maxDataID++;
        textLayout.setId(maxDataID);
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

        // set up team text
        TextView teamText = new TextView(this);
        teamText.setText("Team ????");

        //set up trash button
        ImageButton trashButton = new ImageButton(this);
        trashButton.setImageResource(R.drawable.trash_can);
        trashButton.setScaleType(ImageButton.ScaleType.FIT_XY);
        trashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"id referenced=" + textLayout.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        //set up spacer
        TextView spacingLine = new TextView(this);
        spacingLine.setGravity(Gravity.CENTER);
        spacingLine.setText("-------------------------------------------------------------");


        //sets default images and text to be not visible
        TextView text = findViewById(R.id.textView2);
        text.setVisibility(View.INVISIBLE);

        Space spacer = findViewById(R.id.spacer);
        spacer.setVisibility(View.INVISIBLE);

        ImageView image = findViewById(R.id.imageView);
        image.setVisibility(View.INVISIBLE);

        //adds views to where needed
        textLayout.addView(qualText, textViewParams);
        textLayout.addView(teamText, textViewParams);

        everythingLayout.addView(textLayout, textLayoutParams);
        everythingLayout.addView(trashButton, trashParams);

        mainLayout.addView(everythingLayout, everythingLayoutParams);
        mainLayout.addView(spacingLine, spacingLineParams);
    }
}