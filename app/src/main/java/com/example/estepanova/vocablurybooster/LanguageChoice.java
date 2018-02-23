package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

                //define the name of the file with the words-translation pairs
                dict_source = (language_choice + "-" + translation_choice).toLowerCase() + ".txt";
                startModeActivity();
            }
        });

        chooseLanguage();

    }

    private void chooseLanguage(){

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> WAdapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> TAdapter = ArrayAdapter.createFromResource(this,
                R.array.translations, android.R.layout.simple_spinner_item);

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
                Toast.makeText(LanguageChoice.this, "language_choice: " + language_choice, Toast.LENGTH_LONG).show();

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
                Toast.makeText(LanguageChoice.this, "translation_choice: " + translation_choice, Toast.LENGTH_LONG).show();

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
