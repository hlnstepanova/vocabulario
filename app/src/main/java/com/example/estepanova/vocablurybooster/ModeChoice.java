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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ModeChoice extends AppCompatActivity {

    SharedPreferences preferences;

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
            btnGeneral,
            btnReset;

    private String dict_source;
    private String topic = "general";

    protected void onCreate(Bundle savedInstanceState) {
        Log.i("DEBUG", "ModeOnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_choice);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        btnTopics = (Button) findViewById(R.id.topBtn);
        btnGeneral = (Button) findViewById(R.id.genBtn);
        btnReset = (Button) findViewById(R.id.resetBtn);

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

                generalMode();
                initMainShow();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetAll();
            }
        });

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


    private void initMainShow(){

        //then start the main (learning) activity
        Intent i = new Intent(this, MainShow.class);
        i.putExtra("dictionary", currentDictionary);
        this.startActivity(i);

    }

    private void startTopicChoice(){
        Intent i = new Intent(this, TopicsChoice.class);
        i.putExtra("source", dict_source);
        this.startActivity(i);
    }

    private void generalMode(){
        // if the sharedPrefs for language-translation-general exists, load currentdictionary from sharedPrefs, else import

        Gson gson = new Gson();
        String saved_source = dict_source + "-" + topic;
        String json = preferences.getString(saved_source, "");
        if (!json.isEmpty()) {
            currentDictionary = gson.fromJson(json, Dictionary.class);

        } else {
            importFile();
        }

    }

    public void resetAll(){
        //alert about resetting all the progress to 0
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User chose to reset all the progress
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ModeChoice.this);
                preferences.edit().clear().apply();
                Toast.makeText(getApplicationContext(),
                        "All you progress was successfuly reset!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User chose to cancel resetting = do nothing
            }
        });
        builder.setTitle(R.string.reset_title);
        builder.setMessage(R.string.reset_msg);

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void importFile() {

            //first import the correspondent dictionary
            String filePath = dict_source;
            Log.i("import from", filePath);

            try {
                int reverse = 1;

                //check if file exists
                File f = new File(filePath);
                if(f.exists() && !f.isDirectory()) {
                    //if file exists, proceed to import

                } else {
                    //remove everything after . in the string, split by - and swap, new filepath, tryagain, reverse->2
                    String language_translation = filePath.split(".")[0];
                    String language_choice = language_translation.split("-")[0];
                    String translation_choice = language_translation.split("-")[1];
                    filePath = (translation_choice + "-" + language_choice).toLowerCase() + ".txt";

                    //indicator to import correct keys and values afterwards
                    reverse=2;

                }
                // import from file
                BufferedReader reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open(filePath)));
                String line;

                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" - ", 3);
                    if (parts.length == 3) {
                        String key = parts[1*reverse%3]; // if dict reversed, keys become values
                        String value = parts[2*reverse%3];
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
            currentDictionary = new Dictionary (dict_source, wordsMap, unlearned, to_learn, inProcessMap, learned, topic, 0);

    }

}
