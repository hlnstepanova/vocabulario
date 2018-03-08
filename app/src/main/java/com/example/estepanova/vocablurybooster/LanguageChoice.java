package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LanguageChoice extends AppCompatActivity {

    private Spinner
            spWords,
            spTrans;

    private Button btnApply;

    String language_choice;
    String translation_choice;
    String dict_source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_choice);

        btnApply = (Button) findViewById(R.id.aplBtn);

        spWords = (Spinner) findViewById(R.id.vocabSpin);
        spTrans = (Spinner) findViewById(R.id.transSpin);

        //when apply button is clicked, move to choose mode activity
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (language_choice.equals(translation_choice)){
                    Toast toast = Toast.makeText(LanguageChoice.this, "Please choose different languages", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                } else {
                    //define the name of the file with the words-translation pairs
                    dict_source = (language_choice + "-" + translation_choice).toLowerCase() + ".txt";
                    startModeActivity();
                }
            }
        });

        chooseLanguage();

    }

    //TODO: implement progress bars in TopicChoice, Mainshow, WorCheck and WordAnswer
    //TODO: think about where it's better to save sharedPrefs and how to start at the same learning stage (learning/checking)
    //TODO: remove Check button and replace by tapping any part of screen
    //TODO: relative layout everywhere
    //TODO: if progress = 100% (no unlearned words), ask the user if he want to revise or restart

    private void chooseLanguage(){

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> WAdapter = ArrayAdapter.createFromResource(this,
                R.array.languages, R.layout.spinner_item);

        ArrayAdapter<CharSequence> TAdapter = ArrayAdapter.createFromResource(this,
                R.array.translations, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        WAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spWords.setAdapter(WAdapter);
        spTrans.setAdapter(TAdapter);

        //spinner for input language
        spWords.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                language_choice = spWords.getSelectedItem().toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        //spinner for output language
        spTrans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                translation_choice = spTrans.getSelectedItem().toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

    }

    private void startModeActivity(){
        Intent i = new Intent(this, ModeChoice.class);
        i.putExtra("source", dict_source);
        this.startActivity(i);

    }
}
