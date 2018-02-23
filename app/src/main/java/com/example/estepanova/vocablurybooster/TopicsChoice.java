package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TopicsChoice extends AppCompatActivity implements View.OnClickListener {

    private Dictionary topicDict;

    //hashmap contains word-translation pairs
    private HashMap<String, String> wordsMap = new HashMap<String, String>();
    List<String> unlearned = new ArrayList<String>();
    List<String> to_learn = new ArrayList<String>();
    private HashMap<String, Integer> inProcessMap = new HashMap<String, Integer>();
    private List<String> learned = new ArrayList<String>();
    private HashMap<String, String> topicMap = new HashMap<String, String>();

    private TextView
            top1,
            top2,
            top3;

    String dict_source;


    protected void onCreate(Bundle savedInstanceState) {
        Log.i("DEBUG", "ModeOnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_choice);


        top1 = (TextView) findViewById(R.id.topic1);
        top1.setOnClickListener(this);
        top2 = (TextView) findViewById(R.id.topic2);
        top2.setOnClickListener(this);
        top3 = (TextView) findViewById(R.id.topic3);
        top3.setOnClickListener(this);

        Intent saveIntent = getIntent();

        if (saveIntent.getExtras() == null) {
            //do nothing
        } else {
            dict_source = (String) saveIntent.getSerializableExtra("source");
        }

    }

    @Override
    public void onClick(View view) {
        TextView field = (TextView) view;
        String selected = field.getText().toString();
        startTopicsMode(selected);
    }

    public void startTopicsMode(String selected){
        topicDict = new Dictionary(dict_source, wordsMap, unlearned, to_learn, inProcessMap, learned);
        //redundant: topicDict.importTopics(selected);
        importTopicFile(selected);

        //then start the main (learning) activity
        Intent i = new Intent(this, LanguageChoice.class);
        i.putExtra("dictionary", topicDict);
        this.startActivity(i);

    }

    public void importTopicFile(String selected) {

        //first import the correspondent dictionary
        String filePath = dict_source;
        Log.i("import from", filePath);

        try {
            AssetManager am = getApplicationContext().getAssets();
            InputStreamReader iss = new InputStreamReader(am.open(filePath));
            BufferedReader reader = new BufferedReader(iss);

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ", 3);
                if (parts.length >= 3) {
                    String key = parts[1];
                    String topic = parts[0];
                    String value = parts[2];
                    if (topic.equals(selected)) {
                        //create a hashmap only with words from selected topic
                        this.wordsMap.put(key, value);
                    }
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
