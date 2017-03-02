package com.example.maxim.alzheimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class StartPage extends AppCompatActivity {

    public static String username = "Standard Username";
    public static int numberOfQuestions=10;
    public static int secondsPerQuestion=15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_page);

        findViewById(R.id.beginnen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = ((EditText)findViewById(R.id.numOfQuestionsInput)).getText().toString();
                String anzahl = ((EditText)findViewById(R.id.numOfQuestionsInput)).getText().toString();
                String zeit = ((EditText)findViewById(R.id.timePerQuestionInput)).getText().toString();
                if(!anzahl.isEmpty())
                    numberOfQuestions = Integer.parseInt(anzahl);
                if(!zeit.isEmpty())
                    secondsPerQuestion = Integer.parseInt(zeit);
                startActivity(new Intent(StartPage.this, QuizPage.class));
            }
        });

    }

}
