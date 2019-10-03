package com.vocabulario.estepanova.vocablurybooster;

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

    private ConstraintLayout touchArea;

    private Integer count;
    private boolean reversed;
    private String word;
    private double progress;

    private ProgressBar progressBar;

    private HashMap<String, String> wordsMap;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_answer);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

        touchArea = (ConstraintLayout) findViewById(R.id.touch_area);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        trans2 = (TextView) findViewById(R.id.trans2View);
        answer = (TextView) findViewById(R.id.ansView);

        btnCorrect = (Button) findViewById(R.id.corBtn);
        btnWrong = (Button) findViewById(R.id.wrongBtn);

        Intent saveIntent = getIntent();

        if (saveIntent.getExtras() == null) {
            //no words to check
        } else {
            currentDictionary = (Dictionary) saveIntent.getSerializableExtra("dictionary");
            wordsMap = currentDictionary.getWordsMap();

            if(saveIntent.getSerializableExtra("count")==null){
                count=0;
            } else {
                count = (Integer) saveIntent.getSerializableExtra("count");
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        btnCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if correct answer, change the count of correct answers in ProcessMap
                if (count%2==0) {
                    correctAnswer();
                } else {
                    showAnswer();
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
                    showAnswer();
                }


            }
        });

        touchArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showAnswer();
            }
        });

       touchArea.setOnTouchListener(new OnSwipeTouchListener() {
            public boolean onSwipeRight() {
                if (count%2==0) {
                    correctAnswer();
                } else {
                    showAnswer();
                }
                return true;
            }
            public boolean onSwipeLeft() {
                if (count%2==0) {
                    wrongAnswer();
                } else {
                    showAnswer();
                }
                return true;
            }
        });

        progressBar.setProgress(currentDictionary.getProgress());

        touchArea.setOnClickListener(new View.OnClickListener() {

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
            topicProgressMap = new HashMap<>();
        }

        showTranslation();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.feedback){
            startFeedback();
            return true;
        } else if (item.getItemId()==R.id.activity_welcome){
            startWelcome();
            return true;
        }
        //if the chosen mode is "general", go back to  mode choice, else to topics choice
        else if (currentDictionary.getTopic().equals("general")){
            return(super.onOptionsItemSelected(item));
        }else {
            initTopicsChoice();
            return true;
        }
    }

    private void showAnswer(){

        if (reversed){
            answer.setText(word);
        } else {
        String translation = wordsMap.get(word);
        answer.setText(translation);
        }
        count++;
    }


    private void showTranslation(){

        word = currentDictionary.testWord();

        String word_to_show ="";
        double prob = Math.random();
        if (prob<0.4){
            word_to_show = word;
            reversed = false;
        } else {
            //show the translation instead of the word
            word_to_show = currentDictionary.reverseWord(word);
            reversed = true;
        }

        trans2.setText(word_to_show);
        answer.setText("");
        count++;
    }


    private void wrongAnswer(){

        int size = currentDictionary.getToLearnLength();

        if (currentDictionary.getUnlearnedLength()>0) {
            if (count < 0.6*size) { //if less than 0.6*number of to_learn words checked, continue checking
                showTranslation();

            } else {//else go to main (learning) activity
                Intent i = new Intent(this, MainShow.class);
                i.putExtra("dictionary", currentDictionary);
                startActivity(i);
            }
        } else {
            showTranslation();
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
            builder.setPositiveButton(R.string.revise, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User chose to revise current topic

                    Intent i = new Intent(WordCheck.this, Revision.class);
                    i.putExtra("dictionary", currentDictionary);
                    startActivity(i);
                }
            });
            builder.setNegativeButton(R.string.new_topic, new DialogInterface.OnClickListener() {
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

    private void initTopicsChoice(){
        Intent i = new Intent(this, TopicsChoice.class);
        String dict_source = currentDictionary.getSource();
        i.putExtra("source", dict_source);
        startActivity(i);
    }
    private void startFeedback(){
        Intent i = new Intent(this, Feedback.class);
        this.startActivity(i);

    }

    private void startWelcome(){
        Intent i = new Intent(this, WelcomeActivity.class);
        i.putExtra("Rewatch", "rewatch");
        this.startActivity(i);

    }

}
