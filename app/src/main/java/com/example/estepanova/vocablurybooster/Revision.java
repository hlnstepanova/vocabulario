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
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class Revision extends AppCompatActivity {

    Random random = new Random();

    private Dictionary currentDictionary;

    SharedPreferences preferences;

    private TextView
            trans2,
            answer;

    private Button
            btnCorrect,
            btnWrong;

    private String word;
    private Integer count;

    HashMap<String, String> wordsMap;
    protected List<String> learned;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_answer);
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
            Log.i("Wordcheck:", "no words to check");
        } else {
            currentDictionary = (Dictionary) saveIntent.getSerializableExtra("dictionary");
            wordsMap = currentDictionary.getWordsMap();
            learned = currentDictionary.getLearned();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setMessage(R.string.dialog_wordcheck_intro);

        btnCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if correct answer, change the count of correct answers in ProcessMap
                if (count%2==0) {
                    showTranslation();
                } else {
                    //alarm that first you should guess the word and tap the screen
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
                    showTranslation();
                } else {
                    //alarm that first you should guess the word and tap the screen
                    AlertDialog alert = builder.create();
                    alert.show();
                }


            }
        });

        ConstraintLayout touch_area = (ConstraintLayout) findViewById(R.id.touch_area);
        touch_area.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (count%2!=0) {
                    showAnswer();
                }

            }

        });

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

        int wordPos = random.nextInt(wordsMap.size());
        word = learned.get(wordPos);

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

}

