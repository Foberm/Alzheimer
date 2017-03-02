package com.example.maxim.alzheimer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StartPage extends AppCompatActivity {

    public static String username = "Standard Username";
    public static int numberOfQuestions = 3;
    public static int secondsPerQuestion = 15;
    public static int numberOfTutorials = 1;
    public static String main_directory = "AlzheimerTestOrdner";
    public static List<String> pictures = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_page);

        File f = new File(Environment.getExternalStorageDirectory(), main_directory);
        if (!f.exists()) {
            f.mkdirs();
        }

        //Scanning the available pictures
        final Field[] fields = R.drawable.class.getDeclaredFields();
        String regex = "[a-z]+";
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().matches(regex))
                pictures.add(fields[i].getName());
        }

        //Let User know the maximum amount of Questions possible
        ((EditText)findViewById(R.id.numOfQuestionsInput)).setHint("Anzahl Fragen.. (Standard: 10, Maximal: " + (pictures.size()-1) + ")");

        findViewById(R.id.beginnen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = ((EditText)findViewById(R.id.numOfQuestionsInput)).getText().toString();
                String anzahl = ((EditText)findViewById(R.id.numOfQuestionsInput)).getText().toString();
                String zeit = ((EditText)findViewById(R.id.timePerQuestionInput)).getText().toString();
                if(!anzahl.isEmpty()) {
                    int a = Integer.parseInt(anzahl);
                    if(a > pictures.size()-1)
                        numberOfQuestions = a;
                }
                if(!zeit.isEmpty())
                    secondsPerQuestion = Integer.parseInt(zeit);
                startActivity(new Intent(StartPage.this, QuizPage.class));
            }
        });

    }

}
