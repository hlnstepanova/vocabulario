package com.example.estepanova.vocablurybooster;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Feedback extends AppCompatActivity {

    Button btnSend;
    EditText name,
            email,
            feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        btnSend = (Button) findViewById(R.id.sendBtn);
        name = (EditText) findViewById(R.id.nameTxt);
        email = (EditText) findViewById(R.id.emailTxt);
        feedback = (EditText) findViewById(R.id.feedbackTxt);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/html");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"hlnstepanova@yandex.ru"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback from VocabluryBooster");
                i.putExtra(Intent.EXTRA_TEXT, "Name : " + name.getText() + "\nEmail : " + email.getText() + "\nMessage : " + feedback.getText());
                try {
                    startActivity(Intent.createChooser(i, "Send feedback..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();

        }
        return(super.onOptionsItemSelected(item));
    }

}
