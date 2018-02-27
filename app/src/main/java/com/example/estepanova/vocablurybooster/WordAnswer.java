package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;


public class WordAnswer extends AppCompatActivity {

    private Dictionary currentDictionary;

    private TextView
            trans2,
            answer;

    private Button
            btnCorrect,
            btnWrong;

    private Integer count;

    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_answer);

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

        showAnswer();

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

        String check="Empty";

        currentDictionary.correctAnswer(word);

        if (check.equals(currentDictionary.checkEmpty())){
            Intent i = new Intent(this, CongratsTopic.class);
            i.putExtra("dictionary", currentDictionary);
            startActivity(i);
        }

        wrongAnswer();


    }


}
