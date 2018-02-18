package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Dictionary currentDictionary;

    private TextView
            newWord,
            newTrans;

    private Button
            btnGotIt;

    private Integer count = 0;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newWord = (TextView) findViewById(R.id.newWordView);
        newTrans = (TextView) findViewById(R.id.newTransView);

        btnGotIt = (Button) findViewById(R.id.gotBtn);

        final Intent saveIntent = getIntent();

        if (saveIntent.getExtras() == null) {
            //TODO: go to the language choice
        } else {
            currentDictionary = (Dictionary) saveIntent.getSerializableExtra("dictionary");
        }

        btnGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIntent.putExtra("dictionary", currentDictionary);
                finish();
                startActivity(saveIntent);
            }
        });

        wordShow();

    }

    private void wordShow() {

        //if count < 25, show one mord word
        if (count < 25){
            String randomKey = currentDictionary.showNewWord(); // get a random word from unlearned array

            if (randomKey.equals("no_unlearned_words")){
                initWordCheck();
            }

            String value = currentDictionary.showTranslation(randomKey); // get the translation for that word
            newWord.setText(randomKey);
            newTrans.setText(value);

            count++;


        } else {//if count = 25, set count to 0 and move to learning activity
            initWordCheck();

        }


    }

    private void initWordCheck(){
        count = 0;
        Intent i = new Intent(this, WordCheck.class);
        i.putExtra("dictionary", currentDictionary);
    }
}
