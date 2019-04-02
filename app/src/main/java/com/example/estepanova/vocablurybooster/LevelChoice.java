package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LevelChoice extends AppCompatActivity implements View.OnClickListener {

    private TextView
            levelA1,
            levelB1;

    private ImageView
            imgA1,
            imgB1;

    String language_choice = "portugues";
    String translation_choice = "russo";
    String dict_source;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.level_choice);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

        levelA1 = findViewById(R.id.textViewA1);
        levelB1 = findViewById(R.id.textViewB1);

        imgA1 = findViewById(R.id.imageA1);
        imgB1 = findViewById(R.id.imageB1);


        levelA1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String level = "A1";
                //startLanguageMode(level);
                Toast.makeText(getApplicationContext(),
                        "This level is not available yet", Toast.LENGTH_SHORT).show();

            }
        });

        levelB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String level = "B1";
                startModeActivity(level);

            }
        });

        imgA1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String level = "A1";
                //startLanguageMode(level);
                Toast.makeText(getApplicationContext(),
                        "This level is not available yet", Toast.LENGTH_SHORT).show();

            }
        });

        imgB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String level = "B1";
                startModeActivity(level);

            }
        });


    }

    public void onClick(View view) {
    }

    private void startModeActivity(String level){
        dict_source = (level + "-" + language_choice + "-" + translation_choice).toLowerCase() + ".txt";
        Intent i = new Intent(this, ModeChoice.class);
        i.putExtra("source", dict_source);
        this.startActivity(i);

    }

}
