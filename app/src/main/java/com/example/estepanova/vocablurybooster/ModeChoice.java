package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ModeChoice extends AppCompatActivity {

    private Dictionary currentDictionary;

    //hashmap contains word-translation pairs
    private HashMap<String, String> wordsMap = new HashMap<String, String>();
    List<String> unlearned = new ArrayList<String>(wordsMap.keySet());
    List<String> to_learn = new ArrayList<String>();
    private HashMap<String, Integer> inProcess = new HashMap<String, Integer>();
    private List<String> learned = new ArrayList<String>();


    private Button
            btnTopics,
            btnGeneral;

    String dict_source;

    protected void onCreate(Bundle savedInstanceState) {
        Log.i("DEBUG", "ModeOnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_choice);

        Intent saveIntent = getIntent();

        if (saveIntent.getExtras() == null) {
            //do nothing
        } else {
            dict_source = (String) saveIntent.getSerializableExtra("source");
        }

        //if Topics button is clicked, we go to the Topics catalog
        btnTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTopicChoice();
            }
        });

        //if General button is clicked, all available words are imported and learning starts
        btnGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                generalMode();
            }
        });

        btnTopics = (Button) findViewById(R.id.topBtn);
        btnGeneral = (Button) findViewById(R.id.genBtn);
    }

    private void generalMode(){

        currentDictionary = new Dictionary(dict_source, wordsMap, unlearned, to_learn, inProcess, learned);
        currentDictionary.importFile();

        //then start the main (learning) activity
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("dictionary", currentDictionary);
        this.startActivity(i);

    }

    private void startTopicChoice(){
        Intent i = new Intent(this, TopicsChoice.class);
        i.putExtra("source", dict_source);
        this.startActivity(i);
    }
}
