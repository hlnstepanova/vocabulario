package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TopicsChoice extends AppCompatActivity implements View.OnClickListener {

    private TopicDict topicDict;

    //hashmap contains word-translation pairs
    private HashMap<String, String> wordsMap = new HashMap<String, String>();
    List<String> unlearned = new ArrayList<String>(wordsMap.keySet());
    List<String> to_learn = new ArrayList<String>();
    private HashMap<String, Integer> inProcess = new HashMap<String, Integer>();
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

        //load correspondent topic dictionary
        top1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selected = top1.getText().toString();
                startTopicsMode(selected);

            }
        });

    }

    @Override
    public void onClick(View view) {
        TextView field = (TextView) view;
        String selected = field.getText().toString();
        startTopicsMode(selected);
    }

    public void startTopicsMode(String selected){
        topicDict = new TopicDict(dict_source, wordsMap, unlearned, to_learn, inProcess, learned, topicMap);
        topicDict.importTopics(selected);
        topicDict.importTopicFile(selected);

        //then start the main (learning) activity
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("dictionary", topicDict);
        this.startActivity(i);

    }


}
