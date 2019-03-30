package com.example.estepanova.vocablurybooster;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

    private HashMap<String, Double> topicProgressMap = new HashMap<>();
    private HashMap<TextView, TextView> topicTitleMap = new HashMap<TextView, TextView>();
    private HashMap<TextView, TextView> topicProgressViewMap = new HashMap<TextView, TextView>();


    ArrayList<TextView> topics = new ArrayList<TextView>();
    ArrayList<TextView> titles = new ArrayList<TextView>();
    ArrayList<TextView> progresses = new ArrayList<TextView>();

    String dict_source;
    String topic_selected;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_choice);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //Assigning textviews (topic numbers and titles) and binding them together in a map
        for (int i =1; i<11; i++){
            TextView topic = (TextView)findViewById(getResources().getIdentifier("topic"+i,"id", getPackageName()));
            topics.add(topic);
            TextView title = (TextView)findViewById(getResources().getIdentifier("title"+i,"id", getPackageName()));
            titles.add(title);
            TextView progress = (TextView)findViewById(getResources().getIdentifier("progress"+i,"id", getPackageName()));
            progresses.add(progress);
            topicTitleMap.put(title, topic);
            topicProgressViewMap.put(progress,topic);
        }


        for (TextView topic : topics){
            topic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView field = (TextView) view;
                    topic_selected = field.getText().toString();

                    checkProgress();

                }
            });
        }

        for (TextView title : titles){
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView field = topicTitleMap.get((TextView) view);
                    topic_selected = field.getText().toString();

                    checkProgress();


                }
            });
        }

        for (final TextView progressView: progresses){
            progressView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final TextView topicToResetView = topicProgressViewMap.get((TextView) view);
                    //alert about resetting the progress for this topic
                    AlertDialog.Builder builder = new AlertDialog.Builder(TopicsChoice.this);
                    builder.setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            resetProgress(topicToResetView, progressView);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do nothing
                        }
                    });
                    builder.setTitle(R.string.reset_topic_title);
                    builder.setMessage(R.string.reset_topic_msg);

                    AlertDialog alert = builder.create();
                    alert.show();
                    Log.i("selected topic", topicToResetView.getText().toString());
                }
            });
        }

        //get the map with topics and progresses from preferences, if there's none, set the progress to 0%

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("topicProgressMap", "");
        if (!json.isEmpty()) {
            topicProgressMap = gson.fromJson(json, HashMap.class);
        } else {
            //automatically for every topic fill 0%
            for (TextView topic : topics){
                topicProgressMap.put(topic.getText().toString(), 0.0);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.feedback:
                startFeedback();
                return true;

            case R.id.activity_welcome:
                startWelcome();
                return true;

        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onClick(View view) {
        TextView field = (TextView) view;
        topic_selected = field.getText().toString();
        Log.i("selected", topic_selected);
        startTopicsMode();
    }


    public void startTopicsMode(){
        Log.i("selected", topic_selected);
        // if the sharedPrefs for language-translation-general exists, load currentdictionary from sharedPrefs, else import

        Gson gson = new Gson();
        String saved_source = dict_source + "-" + topic_selected;
        Log.i("saved source", saved_source);
        String json = preferences.getString(saved_source, "");
        if (!json.isEmpty()) {

            Log.i("Importing", "from prefs");
            topicDict = gson.fromJson(json, Dictionary.class);
            Log.i("Importing", Integer.toString(topicDict.getProgress()));

        } else {

            Log.i("Importing", "from file");
            importTopicFile();
        }

            //then start the main (learning) activity
        Intent i = new Intent(this, MainShow.class);
        i.putExtra("dictionary", topicDict);
        this.startActivity(i);

    }

    public void initRevise(){
        Log.i("selected", topic_selected);
        // if the sharedPrefs for language-translation-general exists, load currentdictionary from sharedPrefs, else import

        Gson gson = new Gson();
        String saved_source = dict_source + "-" + topic_selected;
        Log.i("saved source", saved_source);
        String json = preferences.getString(saved_source, "");
        if (!json.isEmpty()) {

            Log.i("Importing", "from prefs");
            topicDict = gson.fromJson(json, Dictionary.class);
            Log.i("Importing", Integer.toString(topicDict.getProgress()));

        } else {

            Log.i("Importing", "from file");
            importTopicFile();
        }
        Intent i = new Intent(this, Revision.class);
        i.putExtra("dictionary", topicDict);
        Log.i("Starting revision: ", topicDict.getSource());
        this.startActivity(i);
    }

    public void importTopicFile() {


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
                        if (topic.equals(topic_selected)) {
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
            topicDict = new Dictionary(dict_source, wordsMap, unlearned, to_learn, inProcessMap, learned, topic_selected, 0);

    }

    public void progressSetter(){

        double progress;

        //get progress from topicProgressMap for every topic

        for (int i = 0; i < topics.size(); i++){

            //String topic_number = topics.get(i).getText().toString().split(" ")[0];
            if (topicProgressMap==null){
                progress=0.0;
                topicProgressMap = new HashMap<>();
            } else {
                progress = topicProgressMap.get(topics.get(i).getText());
            }

            String text = getString(R.string.progress, progress);
            progresses.get(i).setText(text);
        }

    }

    private void savePrefTopicMap(){

        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(topicProgressMap);
        prefsEditor.putString("topicProgressMap", json);
        prefsEditor.commit();

    }

    private void checkProgress(){

        double progress = topicProgressMap.get(topic_selected);
        if (progress==100){
            //alarm learned all words in this category, go back to topic choice
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton(R.string.revise, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    importTopicFile();
                    initRevise();
                }
            });
            builder.setNegativeButton(R.string.reset, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    topicProgressMap.put(topic_selected,0.0);
                    savePrefTopicMap();
                    importTopicFile();
                    Intent i = new Intent(TopicsChoice.this, MainShow.class);
                    i.putExtra("dictionary", topicDict);
                    startActivity(i);

                }
            });
            builder.setTitle(R.string.congrats_title);
            builder.setMessage(R.string.congrats_msg);

            AlertDialog alert = builder.create();
            alert.show();
        } else {
            startTopicsMode();
        }

    }

    private void resetProgress(TextView topicToResetView, TextView progressView){
        //define, what topic belongs to this progress textview
        String topic_to_reset = topicToResetView.getText().toString();
        //reset the textview to 0.0
        progressView.setText(getString(R.string.progress, 0.0));
        //reset the progress in topicprogressMap to 0.0
        topicProgressMap.put(topic_to_reset,0.0);
        //clear SharedPrefrences file for this topic*/
        String saved_source = dict_source+ "-" + topic_to_reset;
        preferences.edit().remove(saved_source).apply();
    }
    private void startFeedback(){
        Intent i = new Intent(this, Feedback.class);
        this.startActivity(i);

    }

    private void startWelcome(){
        Intent i = new Intent(this, WelcomeActivity.class);
        i.putExtra("again", 1);
        this.startActivity(i);

    }

}
