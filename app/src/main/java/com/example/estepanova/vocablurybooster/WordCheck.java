package com.example.estepanova.vocablurybooster;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;


public class WordCheck extends AppCompatActivity {

    private Dictionary currentDictionary;

    private HashMap<String, Double> topicProgressMap;

    private SharedPreferences preferences;

    private TextView
            trans2,
            answer;

    private Button
            btnCorrect,
            btnWrong;

    private ConstraintLayout constraintLayout;

    private Integer count;
    private String word;
    private double progress;

    private ProgressBar progressBar;

    private HashMap<String, String> wordsMap;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_answer);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);

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
            Log.i("Wordcheck:", "no words to check");
        } else {
            currentDictionary = (Dictionary) saveIntent.getSerializableExtra("dictionary");
            wordsMap = currentDictionary.getWordsMap();

            if(saveIntent.getSerializableExtra("count")==null){
                count=0;
            } else {
                count = (Integer) saveIntent.getSerializableExtra("count");
            }
            Log.i("Wordcheck:", count.toString());
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        btnCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if correct answer, change the count of correct answers in ProcessMap
                if (count%2==0) {
                    correctAnswer();
                } else {
                    //alarm that first you should guess the word and tap the screen
                    builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });
                    builder.setMessage(R.string.dialog_wordcheck_intro);
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });

        btnWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if wrong, just go back to word show
                if (count%2==0) {
                    wrongAnswer();
                } else {
                    //alarm that first you should guess the word and tap the screen
                    AlertDialog alert = builder.create();
                    alert.show();
                }


            }
        });

        constraintLayout.setOnTouchListener(new OnSwipeTouchListener(WordCheck.this) {
            public void onSwipeTop() {
                correctAnswer();
            }
            public void onSwipeRight() {
                wrongAnswer();
            }
            public void onSwipeLeft() {
                correctAnswer();
            }
            public void onSwipeBottom() {
                wrongAnswer();
            }

        });

        progressBar.setProgress(currentDictionary.getProgress());

        ConstraintLayout touch_area = (ConstraintLayout) findViewById(R.id.touch_area);
        touch_area.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (count%2!=0) {
                    showAnswer();
                }

            }

        });

        //get a topic HashMap with progresses from preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();

        String json = preferences.getString("topicProgressMap", "");
        if (!json.isEmpty()) {
            topicProgressMap = gson.fromJson(json, HashMap.class);
        } else {
            Log.d("WordAnswer", "no progress map found");
        }

        showTranslation();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return(super.onOptionsItemSelected(item));
    }

    private void showAnswer(){

        String translation = wordsMap.get(word);
        answer.setText(translation);
        count++;
    }


    private void showTranslation(){

        word = currentDictionary.testWord();
        trans2.setText(word);
        answer.setText("");
        //String translation = wordsMap.get(word);
        //trans2.setText(translation);
        count++;
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

    private void wrongAnswer(){

        if (count < 7) { //if less than 50 words checked, continue checking
            showTranslation();

        } else {//else go to main (learning) activity
            Intent i = new Intent(this, MainShow.class);
            i.putExtra("dictionary", currentDictionary);
            startActivity(i);
        }

    }

    private void correctAnswer(){

        currentDictionary.correctAnswer(word);
        progress = currentDictionary.calculateProgress();
        topicProgressMap.put(currentDictionary.getTopic(), progress);
        currentDictionary.setProgress((int) progress);
        progressBar.setProgress((int) progress);
        savePreferences();

        if (currentDictionary.checkEmpty()){
            //alarm learned all words in this category, go back to topic choice
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setNeutralButton(R.string.revise, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User chose to revise current topic

                    Intent i = new Intent(WordCheck.this, Revision.class);
                    i.putExtra("dictionary", currentDictionary);
                    startActivity(i);
                }
            });
            builder.setNeutralButton(R.string.new_topic, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User chose to learn a new topic
                    String dict_source = currentDictionary.getSource();

                    Intent i = new Intent(WordCheck.this, TopicsChoice.class);
                    i.putExtra("source", dict_source);
                    startActivity(i);
                }
            });
            builder.setTitle(R.string.congrats_title);
            builder.setMessage(R.string.congrats_msg);

            AlertDialog alert = builder.create();
            alert.show();

        } else {
            wrongAnswer();
        }


    }

    private void savePreferences(){

        //save into preferences currentDictionary and topicProgressMap (progress) after every correct answer

        String saved_source = currentDictionary.getSource()+ "-" + currentDictionary.getTopic();
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(currentDictionary);
        String topics = gson.toJson(topicProgressMap);
        prefsEditor.putString(saved_source, json);
        prefsEditor.putString("topicProgressMap", topics);
        prefsEditor.commit();

    }

}
