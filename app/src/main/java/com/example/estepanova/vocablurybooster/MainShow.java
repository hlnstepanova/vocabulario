package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainShow extends AppCompatActivity{

    private Dictionary currentDictionary;


    private TextView
            newWord,
            newTrans;

    private Button
            btnGotIt;

    private Integer count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        wordShow();

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
        Intent i = new Intent(this, WordCheck.class);
        i.putExtra("dictionary", currentDictionary);
        startActivity(i);
    }

    private void initLanguageChoice() {
        startActivity(
                new Intent(this, LanguageChoice.class));
    }


}
