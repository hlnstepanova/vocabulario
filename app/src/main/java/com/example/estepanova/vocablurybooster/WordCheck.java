package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;


public class WordCheck extends AppCompatActivity {

    private Dictionary currentDictionary;

    private TextView trans1;
    private Button btnCheck;

    private Integer count;

    private String word;

    HashMap<String, String> wordsMap;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_check);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        trans1 = (TextView) findViewById(R.id.trans1View);
        btnCheck = (Button) findViewById(R.id.checkBtn);


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

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), WordAnswer.class);
                i.putExtra("dictionary", currentDictionary);
                i.putExtra("word", word);
                i.putExtra("count", count);
                startActivity(i);

            }
        });

        initializeCheck();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return(super.onOptionsItemSelected(item));
    }


    private void initializeCheck(){

        word = currentDictionary.testWord();
        String translation = wordsMap.get(word);
        trans1.setText(translation);
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
