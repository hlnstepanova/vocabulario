package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TopicsChoice extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences preferences;

    private Dictionary topicDict;

    //hashmap contains word-translation pairs
    private HashMap<String, String> wordsMap = new HashMap<String, String>();
    List<String> unlearned = new ArrayList<String>();
    List<String> to_learn = new ArrayList<String>();
    private HashMap<String, Integer> inProcessMap = new HashMap<String, Integer>();
    private List<String> learned = new ArrayList<String>();
    private HashMap<String, Double> topicMap = new HashMap<String, Double>();


    ArrayList<TextView> topics = new ArrayList<TextView>();
    ArrayList<TextView> progresses = new ArrayList<TextView>();

    String dict_source;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_choice);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        topics.add((TextView)findViewById(R.id.topic1));
        topics.add((TextView)findViewById(R.id.topic2));
        topics.add((TextView)findViewById(R.id.topic3));


        for (TextView topic : topics){
            topic.setOnClickListener(this);
        }

        //get the map with topics and progresses from preferences, if there's none, set the progress to 0%

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("topicMap", "");
        if (!json.isEmpty()) {
            topicMap = gson.fromJson(json, HashMap.class);
        } else {
            //automatically for every topic fill 0%
            for (TextView topic : topics){
                topicMap.put(topic.getText().toString(), 0.0);
            }

            savePrefTopicMap();
        }

        Intent saveIntent = getIntent();

        if (saveIntent.getExtras() == null) {
            //do nothing
        } else {
            dict_source = (String) saveIntent.getSerializableExtra("source");
        }


        Log.i("Topics", dict_source);

        //set progresses by topics

        progressSetter();

    }

    @Override
    public void onClick(View view) {
        TextView field = (TextView) view;
        String topic_selected = field.getText().toString();
        Log.i("selected", topic_selected);
        startTopicsMode(topic_selected);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }

    public void startTopicsMode(String selected){
        Log.i("selected", selected);
        // if the sharedPrefs for language-translation-general exists, load currentdictionary from sharedPrefs, else import

        Gson gson = new Gson();
        String saved_source = dict_source + "-" + selected;
        String json = preferences.getString(saved_source, "");
        if (!json.isEmpty()) {
            topicDict = gson.fromJson(json, Dictionary.class);

        } else {
            importTopicFile(selected);
        }

            //then start the main (learning) activity
        Intent i = new Intent(this, MainShow.class);
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
            topicDict = new Dictionary(dict_source, wordsMap, unlearned, to_learn, inProcessMap, learned, selected);

    }

    public void progressSetter(){

        progresses.add((TextView) findViewById(R.id.progress1));
        progresses.add((TextView) findViewById(R.id.progress2));
        progresses.add((TextView) findViewById(R.id.progress3));

        double progress;

        //get progress from topicMap for every topic

        for (int i = 0; i < topics.size(); i++){
            progress = topicMap.get(topics.get(i).getText());
            String text = getString(R.string.progress, progress);
            progresses.get(i).setText(text);
        }

    }

    private void savePrefTopicMap(){

        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(topicMap);
        prefsEditor.putString("topicMap", json);
        prefsEditor.commit();

    }


}
