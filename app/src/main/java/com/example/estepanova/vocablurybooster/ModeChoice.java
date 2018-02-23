package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ModeChoice extends AppCompatActivity {

    private Dictionary currentDictionary;

    //hashmap contains word-translation pairs
    private HashMap<String, String> wordsMap = new HashMap<String, String>();
    //unlearned array for words not yet shown
    List<String> unlearned = new ArrayList<String>(wordsMap.keySet());
    //to_learn is for shown words, but not yet learned
    List<String> to_learn = new ArrayList<String>();
    //inProcess hashap is words to_learn and how many times the right answer was given
    private HashMap<String, Integer> inProcessMap = new HashMap<String, Integer>();
    //when Integer in inProcess > 5 -> the word becomes learned
    private List<String> learned = new ArrayList<String>();


    private Button
            btnTopics,
            btnGeneral;

    String dict_source;

    protected void onCreate(Bundle savedInstanceState) {
        Log.i("DEBUG", "ModeOnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_choice);

        btnTopics = (Button) findViewById(R.id.topBtn);
        btnGeneral = (Button) findViewById(R.id.genBtn);

        Intent saveIntent = getIntent();

        if (saveIntent.getExtras() == null) {
            Log.i("DEBUG", "mode choice no intent");
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

                importFile();
                generalMode();
            }
        });

    }

    private void generalMode(){

        currentDictionary = new Dictionary(dict_source, wordsMap, unlearned, to_learn, inProcessMap, learned);

        //then start the main (learning) activity
        Intent i = new Intent(this, MainShow.class);
        i.putExtra("dictionary", currentDictionary);
        this.startActivity(i);

    }

    private void startTopicChoice(){
        Intent i = new Intent(this, TopicsChoice.class);
        i.putExtra("source topics", dict_source);
        this.startActivity(i);
    }

    public void importFile() {

        //first import the correspondent dictionary
        String filePath = dict_source;
        Log.i("import from", filePath);

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open(filePath)));

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ", 3);
                if (parts.length == 3) {
                    String key = parts[1];
                    String value = parts[2];
                    wordsMap.put(key, value);
                } else {
                    Log.i("Import:", "ignoring line: " + line);
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        unlearned = new ArrayList<String>(wordsMap.keySet());

    }

}
