package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainShow extends AppCompatActivity{
    SharedPreferences preferences;

    private Dictionary currentDictionary;


    private TextView
            newWord,
            newTrans;

    private Button
            btnGotIt;

    private Integer count;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        newWord = (TextView) findViewById(R.id.newWordView);
        newTrans = (TextView) findViewById(R.id.newTransView);

        btnGotIt = (Button) findViewById(R.id.gotBtn);

        //on first create, if there's no intent, you should get it from language choice

        final Intent saveIntent = getIntent();

        if (saveIntent.getExtras() == null) {
            initLanguageChoice();
        } else {//create an instance of a dictionary
            currentDictionary = (Dictionary) saveIntent.getSerializableExtra("dictionary");
        }

        count=0;

        btnGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count==5) {
                    initWordCheck();
                } else {
                    wordShow();
                }


            }
        });

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(currentDictionary.getProgress());

        wordShow();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return(super.onOptionsItemSelected(item));
    }

    private void wordShow() {

        //if count < 25, show one mord word

            String randomKey = currentDictionary.showNewWord(); // get a random word from unlearned array

            if (randomKey.equals("no_unlearned_words")){
                initWordCheck();
            }

            String value = currentDictionary.showTranslation(randomKey); // get the translation for that word
            newWord.setText(randomKey);
            newTrans.setText(value);

            count++;
            Log.d("Count: ", count.toString());

    }
    

    private void initWordCheck(){
        savePreferences();

        Intent i = new Intent(this, WordCheck.class);
        i.putExtra("dictionary", currentDictionary);
        startActivity(i);
    }

    private void initLanguageChoice() {
        startActivity(
                new Intent(this, LanguageChoice.class));
    }

    private void savePreferences(){
        String saved_source = currentDictionary.getSource()+ "-" + currentDictionary.getTopic();
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(currentDictionary);
        prefsEditor.putString(saved_source, json);
        prefsEditor.commit();
    }

    private void initModeChoice() {
        Intent i = new Intent(this, ModeChoice.class);
        String dict_source = currentDictionary.getSource();
        i.putExtra("source", dict_source);
        startActivity(i);
    }


}
