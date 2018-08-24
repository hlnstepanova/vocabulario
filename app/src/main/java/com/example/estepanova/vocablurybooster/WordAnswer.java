package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;


public class WordAnswer extends AppCompatActivity {

    SharedPreferences preferences;

    private Dictionary currentDictionary;
    private HashMap<String, Double> topicMap;

    private TextView
            trans2,
            answer;

    private Button
            btnCorrect,
            btnWrong;

    private Integer count;

    private String word;
    private double progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_answer);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        trans2 = (TextView) findViewById(R.id.trans2View);
        answer = (TextView) findViewById(R.id.ansView);

        btnCorrect = (Button) findViewById(R.id.corBtn);
        btnWrong = (Button) findViewById(R.id.wrongBtn);

        Intent saveIntent = getIntent();

        if (saveIntent.getExtras() == null) {
            //do nothing
        } else {
            currentDictionary = (Dictionary) saveIntent.getSerializableExtra("dictionary");
            word = (String) saveIntent.getSerializableExtra("word");
            count = (Integer) saveIntent.getSerializableExtra("count");
        }

        btnCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if correct answer, change the count of correct answers in ProcessMap
                correctAnswer();

            }
        });

        btnWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if wrong, just go back to word show
                wrongAnswer();

            }
        });

        //get a topic HashMap with progresses from preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();

        String json = preferences.getString("topicMap", "");
        if (!json.isEmpty()) {
            topicMap = gson.fromJson(json, HashMap.class);
        } else {
            Log.d("WordAnswer", "no progress map found");
        }

        showAnswer();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return(super.onOptionsItemSelected(item));
    }

    private void showAnswer(){
        HashMap <String, String> wordsMap = currentDictionary.getWordsMap();
        String translation = wordsMap.get(word);
        trans2.setText(translation);
        answer.setText(word);

    }

    private void wrongAnswer(){

        if (count < 7) { //if less than 50 words checked, continue checking
            Intent i = new Intent(this, WordCheck.class);
            i.putExtra("dictionary", currentDictionary);
            i.putExtra("count", count);
            startActivity(i);

        } else {//else go to main (learning) activity
            Intent i = new Intent(this, MainShow.class);
            i.putExtra("dictionary", currentDictionary);
            startActivity(i);
        }

    }

    private void correctAnswer(){

        currentDictionary.correctAnswer(word);
        progress = currentDictionary.calculateProgress();
        topicMap.put(currentDictionary.getTopic(), progress);
        savePreferences();

        if (currentDictionary.checkEmpty()){
            Intent i = new Intent(this, CongratsTopic.class);
            i.putExtra("dictionary", currentDictionary);
            startActivity(i);
        } else {
            wrongAnswer();
        }


    }

    private void savePreferences(){

        //save into preferences currentDictionary and topicMap (progress) after every correct answer

        String saved_source = currentDictionary.getSource()+ "-" + currentDictionary.getTopic();
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(currentDictionary);
        String topics = gson.toJson(topicMap);
        prefsEditor.putString(saved_source, json);
        prefsEditor.putString("topicMap", topics);
        prefsEditor.commit();

    }

    private void initLanguageChoice() {
        startActivity(
                new Intent(this, LanguageChoice.class));
    }

    private void initModeChoice() {
        Intent i = new Intent(this, ModeChoice.class);
        String dict_source = currentDictionary.getSource();
        i.putExtra("source", dict_source);
        startActivity(i);
    }


}
